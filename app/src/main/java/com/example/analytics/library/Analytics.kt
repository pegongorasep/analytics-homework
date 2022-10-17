package com.example.analytics.library

interface Analytics {
    // to track a custom event
    fun track(event: Event)
    // track the start of an event
    fun beginEvent(event: Event)
    // track the ending of an event
    fun endEvent(event: Event)
}

data class Event(val id: String, val attributes: Map<String, Any>)