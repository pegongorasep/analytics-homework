package com.example.analytics.library

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class AnalyticsBuilder(private val coroutineScope: CoroutineScope) {
    var version = "1.0.0"
    var someOtherConfig = "1.0.0"

    fun build(): Analytics = AnalyticsImpl(
        AnalyticsConfig(version, someOtherConfig, coroutineScope)
    )
}

internal data class AnalyticsConfig(
    val version: String,
    val someOtherConfig: String,
    val coroutineScope: CoroutineScope,
)

internal class AnalyticsImpl(private val analyticsConfig: AnalyticsConfig) : Analytics {
    companion object {
        private const val EVENT = "event"
        private const val EVENT_BEGIN = "event_begin"
        private const val EVENT_END = "event_end"
    }

    private var events: Queue<Event> = ConcurrentLinkedQueue()

    override fun track(event: Event) { sendEvent(event) }

    override fun begin(event: Event) {
        val attributes = event.attributes.toMutableMap()
        attributes[EVENT] = EVENT_BEGIN
        sendEvent(event.copy(attributes = attributes))
    }

    override fun end(event: Event) {
        val attributes = event.attributes.toMutableMap()
        attributes[EVENT] = EVENT_END
        sendEvent(event.copy(attributes = attributes))
    }

    private fun sendEvent(event: Event) {
        analyticsConfig.coroutineScope.launch {
            try {
                send(event)
            } catch (ex: Exception) {
                events.add(event)
                sendLocalEvents()
            }
        }
    }

    private suspend fun send(event: Event) {
        // TODO send to backend
    }

    private var sendLocalEventsJob: Job? = null
    private fun sendLocalEvents() {
        if (events.isEmpty() || sendLocalEventsJob != null) return

        sendLocalEventsJob = analyticsConfig.coroutineScope.launch {
            retry {
                while (events.isNotEmpty()) {
                    send(events.peek()!!)
                    events.poll()
                }
            }

            sendLocalEventsJob?.cancel()
            sendLocalEventsJob = null
        }
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