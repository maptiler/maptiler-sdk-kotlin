/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.bridge

import android.content.Context
import android.webkit.JavascriptInterface

internal interface MTJavascriptDelegate {
    fun onError(message: String)

    fun onWebGLContextLost()
}

internal class MTJavaScriptInterface(private val context: Context) {
    var delegate: MTJavascriptDelegate? = null

    @JavascriptInterface
    fun onError(message: String) {
        delegate?.onError(message)
    }

    @JavascriptInterface
    fun onWebGLContextLost() {
        delegate?.onWebGLContextLost()
    }
}
