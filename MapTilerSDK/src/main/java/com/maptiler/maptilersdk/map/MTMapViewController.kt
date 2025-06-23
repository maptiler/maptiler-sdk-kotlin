/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map

import android.content.Context
import android.webkit.WebView
import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.WebViewExecutor
import com.maptiler.maptilersdk.bridge.WebViewExecutorDelegate
import com.maptiler.maptilersdk.commands.InitializeMap
import com.maptiler.maptilersdk.logging.MTLogType
import com.maptiler.maptilersdk.logging.MTLogger
import com.maptiler.maptilersdk.map.style.MTStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface MTMapViewDelegate {
    fun onMapViewInitialized()
}

/**
 * Object exposing methods and properties that enable changes to the map,
 * and events that can be interacted with.
 */
class MTMapViewController(
    private val context: Context,
) : WebViewExecutorDelegate {
    private var coroutineScope: CoroutineScope? = null

    private var bridge: MTBridge? = null
    private var webViewExecutor: WebViewExecutor? =
        WebViewExecutor(context).apply {
            delegate = this@MTMapViewController
        }

    /**
     * Proxy style object of the map.
     */
    var style: MTStyle? = null

    /**
     * Current options of the map object.
     */
    var options: MTMapOptions? = null

    /**
     * Delegate object responsible for event propagation
     */
    var delegate: MTMapViewDelegate? = null

    init {
        webViewExecutor = WebViewExecutor(context)
        bridge = MTBridge(webViewExecutor)
    }

    private suspend fun initializeMap() {
        val apiKey = MTConfig.getAPIKey()

        if (options != null) {
            MTConfig.setSessionLogic(options!!.isSessionLogicEnabled)
        }

        val isSessionLogicEnabled = MTConfig.isSessionLogicEnabled

        bridge!!.execute(
            InitializeMap(
                apiKey,
                options,
                style!!.referenceStyle,
                style!!.styleVariant,
                isSessionLogicEnabled,
            ),
        )

        delegate?.onMapViewInitialized()
    }

    fun bind(scope: CoroutineScope) {
        coroutineScope = scope
    }

    fun destroy() {
        webViewExecutor?.webViewManager!!.destroy()
    }

    internal fun getAttachableWebView(): WebView = webViewExecutor?.webViewManager!!.getAttachableWebView()

    override fun onNavigationFinished(url: String) {
        coroutineScope?.launch(Dispatchers.Default) {
            try {
                initializeMap()
            } catch (error: Exception) {
                MTLogger.log("Map Init error $error", MTLogType.ERROR)
            }
        }
    }
}
