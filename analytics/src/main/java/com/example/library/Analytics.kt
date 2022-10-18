package com.example.library

import kotlinx.coroutines.CoroutineScope

fun analytics(
    scope: CoroutineScope,
    version: String,
    init: Config.() -> Unit,
): Analytics {
    val config = Config(version)
    config.init()
    return AnalyticsImpl(config, AnalyticsUploader(scope))
}

interface Analytics {
    fun track(attributes: Event.() -> Unit = {})
    fun phase(attributes: Phase.() -> Unit = {}): Phase
}

interface Event {
    infix fun String.to(any: Any)
}
interface Phase {
    infix fun String.to(any: Any)
    fun end()
}