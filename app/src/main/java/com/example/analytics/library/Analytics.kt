package com.example.analytics.library

interface Analytics {
    // to track a custom event
    fun track(event: Event)
    // track the beginning of an event
    fun begin(event: Event)
    // track the ending of an event
    fun end(event: Event)
}

data class Event(val id: String, val attributes: Map<String, Any>)