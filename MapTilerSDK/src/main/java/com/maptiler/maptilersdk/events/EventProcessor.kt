/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.events

import com.maptiler.maptilersdk.logging.MTLogType
import com.maptiler.maptilersdk.logging.MTLogger
import com.maptiler.maptilersdk.map.types.MTData

interface EventProcessorDelegate {
    fun onEventTriggered(
        processor: EventProcessor,
        event: MTEvent,
        data: MTData? = null,
    )
}

class EventProcessor {
    private object Constants {
        const val CIRCULAR_EVENT_BUFFER_SIZE: Int = 20
        const val UNKNOWN_EVENT_MESSAGE: String = "Unknown event occurred."
    }

    private val eventQueue = CircularEventBuffer(Constants.CIRCULAR_EVENT_BUFFER_SIZE)
    private var lastTouchTimestamp: Double = 0.0
    private var doubleTapSensitivity: Double = 0.4

    var delegate: EventProcessorDelegate? = null

    fun registerEvent(
        event: MTEvent?,
        data: MTData? = null,
    ) {
        if (event == null) {
            MTLogger.log(Constants.UNKNOWN_EVENT_MESSAGE, MTLogType.WARNING)
            return
        }

        processEventIfNeeded(event, data)
    }

    private fun processEventIfNeeded(
        event: MTEvent,
        data: MTData? = null,
    ) {
        when (event) {
            MTEvent.ON_TOUCH_END -> processTap()
            MTEvent.ON_IDLE -> processIdleFollowingDoubleTap(data)
            else -> { /* do nothing */ }
        }

        if (event != MTEvent.ON_DOUBLE_TAP) {
            delegate?.onEventTriggered(this, event, data)
        }
    }

    private fun processTap() {
        eventQueue.enqueue(MTEvent.ON_TOUCH_END)

        val currentTimestamp = System.currentTimeMillis() / 1000.0

        if (currentTimestamp - lastTouchTimestamp < doubleTapSensitivity) {
            eventQueue.enqueue(MTEvent.ON_DOUBLE_TAP)
        }

        lastTouchTimestamp = currentTimestamp
    }

    private fun processIdleFollowingDoubleTap(data: MTData? = null) {
        if (eventQueue.contains(MTEvent.ON_DOUBLE_TAP)) {
            delegate?.onEventTriggered(this, MTEvent.ON_DOUBLE_TAP, data)
            eventQueue.clear()
        }
    }

    fun setDoubleTapSensitivity(sensitivity: Double) {
        doubleTapSensitivity = sensitivity
    }
}
