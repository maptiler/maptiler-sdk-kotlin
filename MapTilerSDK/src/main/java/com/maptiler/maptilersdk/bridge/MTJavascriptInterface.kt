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
    fun onError(
        message: String,
        status: Int,
        url: String,
    )

    fun onWebGLContextLost()

    fun onEvent(
        event: MTEvent,
        data: MTData?,
    )

    fun onModuleEvent(
        moduleId: String,
        event: String,
        data: String,
    )

    fun onPromiseResult(
        requestId: String,
        result: String?,
        error: String?,
    )
}

internal class MTJavaScriptInterface(
    private val context: Context,
) {
    var delegate: MTJavascriptDelegate? = null

    @JavascriptInterface
    fun onError(
        message: String,
        status: Int,
        url: String,
    ) {
        delegate?.onError(message, status, url)
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

    @JavascriptInterface
    fun onModuleEvent(
        moduleId: String,
        event: String,
        data: String,
    ) {
        delegate?.onModuleEvent(moduleId, event, data)
    }

    @JavascriptInterface
    fun onPromiseResult(
        requestId: String,
        result: String?,
        error: String?,
    ) {
        delegate?.onPromiseResult(requestId, result, error)
    }
}
