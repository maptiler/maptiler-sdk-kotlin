/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.bridge

import android.content.Context
import android.webkit.JavascriptInterface
import com.maptiler.maptilersdk.events.MTEvent
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.types.MTData
import kotlinx.serialization.SerializationException

internal interface MTJavascriptDelegate {
    fun onError(message: String)

    fun onWebGLContextLost()

    fun onEvent(
        event: MTEvent,
        data: MTData?,
    )
}

internal class MTJavaScriptInterface(
    private val context: Context,
) {
    var delegate: MTJavascriptDelegate? = null

    @JavascriptInterface
    fun onError(message: String) {
        delegate?.onError(message)
    }

    @JavascriptInterface
    fun onWebGLContextLost() {
        delegate?.onWebGLContextLost()
    }

    @JavascriptInterface
    fun onEvent(
        event: String,
        data: String,
    ) {
        val eventObject: MTEvent = JsonConfig.json.decodeFromString<MTEvent>("\"$event\"")

        val eventData: MTData? =
            try {
                JsonConfig.json.decodeFromString<MTData?>(data)
            } catch (e: SerializationException) {
                null
            }

        delegate?.onEvent(eventObject, eventData)
    }
}
