package com.example.library

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

// class <T>ObservableSaveableQueue(onAdded: () -> Unit = {}): Queue by ConcurrentLinkedQueue() {
class ObservableSaveableQueue<T>(val onAdded: () -> Unit = {}) {
    private var queue: Queue<T> = load()
    
    fun add(value: T) {
        queue.add(value)
        save()
        onAdded()
    }
    fun poll(): T {
        queue = load()
        val value = queue.poll()
        save()
        return value
    }

    fun peek(): T = queue.peek()
    fun isNotEmpty(): Boolean = queue.isNotEmpty()

    fun save() {
        // save locally
    }
    fun load(): Queue<T> {
        // load locally
        return ConcurrentLinkedQueue()
    }
}
