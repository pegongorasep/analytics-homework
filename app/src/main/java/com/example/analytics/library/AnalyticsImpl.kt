package com.example.analytics.library

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.HashMap

fun analytics(
    scope: CoroutineScope,
    version: String,
    init: Config.() -> Unit
): Analytics {
    val config = Config(scope, version)
    config.init()
    return AnalyticsImpl(config)
}

class AnalyticsImpl(private val config: Config) : Analytics {
    companion object {
        private const val PHASE_BEGIN = "phase_begin"
        private const val PHASE_END = "phase_end"
    }

    private var queue: Queue<Map<String, Any>> = ConcurrentLinkedQueue()
    private fun queueAndSend(map: Map<String, Any>) {
        queue.add(map)
        sendQueue()
    }

    override fun track(attributes: Event.() -> Unit) {
        val event = object: Event {
            val map = HashMap<String, Any>()
            fun build(): Map<String, Any> = map
            override fun String.to(any: Any) { map[this] = any }
        }
        queueAndSend(event.build())
    }

    override fun phase(attributes: Phase.() -> Unit): Phase {
        val phase = object: Phase {
            val map = HashMap<String, Any>().apply { PHASE_BEGIN to System.currentTimeMillis() }
            override fun String.to(any: Any) { map[this] = any }
            override fun end() {
                map[PHASE_END] = System.currentTimeMillis()
                queueAndSend(map)
            }
        }
        return phase
    }

    private var sendQueueJob: Job? = null
    private fun sendQueue() {
        if (queue.isEmpty() || sendQueueJob != null) return

        sendQueueJob = config.coroutineScope.launch {
            retry {
                while (queue.isNotEmpty()) {
                    send(queue.peek()!!)
                    queue.poll()
                }
            }

            sendQueueJob?.cancel()
            sendQueueJob = null
        }
    }

    private suspend fun send(map: Map<String, Any>) {
        // TODO send to backend
    }

    private suspend fun retry(
        times: Int = Int.MAX_VALUE,
        initialDelay: Long = 5 * 1000, // 5 second
        maxDelay: Long = 60 * 5 * 1000, // 5 minutes
        factor: Double = 2.0,
        block: suspend () -> Unit)
    {
        var currentDelay = initialDelay
        repeat(times - 1) {
            try {
                return block()
            } catch (e: IOException) {
                // try next time
            }
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }
        return block() // last attempt
    }

}