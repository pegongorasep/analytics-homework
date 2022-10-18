package com.example.library

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

class AnalyticsUploader(private val coroutineScope: CoroutineScope) {

    private var sendQueueJob: Job? = null
    fun sendQueue() {
        if (sendQueueJob != null) return

        sendQueueJob = coroutineScope.launch {
            retry {
//                while (queue.isNotEmpty()) {
//                    send(queue.peek()!!)
//                    queue.poll()
//                }
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
        block: suspend () -> Unit
    ) {
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

    fun enqueue(map: Map<String, Any>) {
        TODO("Not yet implemented")
    }

}