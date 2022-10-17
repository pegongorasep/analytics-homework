package com.example.analytics.library

import kotlinx.coroutines.CoroutineScope

data class Config(
    val coroutineScope: CoroutineScope,
    val version: String,
) {
    var pkg: String = ""
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