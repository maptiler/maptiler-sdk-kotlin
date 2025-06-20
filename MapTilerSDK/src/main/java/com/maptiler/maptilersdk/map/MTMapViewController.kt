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
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.MTMapStyleVariant
import com.maptiler.maptilersdk.map.style.MTStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MTMapViewController(
    private val context: Context,
) : WebViewExecutorDelegate {
    private var coroutineScope: CoroutineScope? = null

    var style: MTStyle? = null
    var options: MTMapOptions? = null

    private var bridge: MTBridge? = null
    private var webViewExecutor: WebViewExecutor? =
        WebViewExecutor(context).apply {
            delegate = this@MTMapViewController
        }

    init {
        webViewExecutor = WebViewExecutor(context)
        bridge = MTBridge(webViewExecutor)
    }

    suspend fun initializeMap() {
        val apiKey = MTConfig.getAPIKey()

        if (options != null) {
            MTConfig.setSessionLogic(options!!.isSessionLogicEnabled)
        }

        val isSessionLogicEnabled = MTConfig.isSessionLogicEnabled

        style = MTStyle(MTMapReferenceStyle.STREETS, MTMapStyleVariant.DEFAULT_VARIANT)

        bridge!!.execute(
            InitializeMap(
                apiKey,
                options,
                style!!.referenceStyle,
                style!!.styleVariant,
                isSessionLogicEnabled,
            ),
        )
    }

    fun bind(scope: CoroutineScope) {
        coroutineScope = scope
    }

    fun destroy() {
        webViewExecutor?.webViewManager!!.destroy()
    }

    fun getAttachableWebView(): WebView = webViewExecutor?.webViewManager!!.getAttachableWebView()

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
