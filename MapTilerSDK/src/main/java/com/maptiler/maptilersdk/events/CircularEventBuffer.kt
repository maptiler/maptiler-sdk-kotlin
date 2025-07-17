/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.events

internal class CircularEventBuffer(
    private val capacity: Int,
) {
    private val buffer: Array<MTEvent?> = arrayOfNulls(capacity)
    private var head: Int = 0
    private var tail: Int = 0
    private var isFull: Boolean = false
    val isEmpty: Boolean
        get() = !isFull && head == tail

    fun enqueue(event: MTEvent) {
        buffer[tail] = event
        tail = (tail + 1) % capacity

        if (isFull) {
            head = (head + 1) % capacity
        }

        isFull = tail == head
    }

    fun dequeue(): MTEvent? {
        if (isEmpty) return null

        val event = buffer[head]
        buffer[head] = null
        head = (head + 1) % capacity
        isFull = false

        return event
    }

    fun contains(event: MTEvent): Boolean {
        var index = head

        while (index != tail) {
            if (buffer[index] == event) {
                return true
            }

            index = (index + 1) % capacity
        }

        return false
    }

    fun clear() {
        for (i in buffer.indices) {
            buffer[i] = null
        }

        head = 0
        tail = 0
        isFull = false
    }
}
