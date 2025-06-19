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
import com.maptiler.maptilersdk.bridge.WebViewManager
import com.maptiler.maptilersdk.commands.InitializeMap
import com.maptiler.maptilersdk.map.style.MTStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MTMapViewController(
    private val context: Context,
) : WebViewExecutorDelegate {
    private val webViewManager = WebViewManager(context)
    private var coroutineScope: CoroutineScope? = null

    var style: MTStyle? = null
    var options: MTMapOptions? = null

    private var bridge: MTBridge? = null
    private var webViewExecutor: WebViewExecutor? = null

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
        webViewManager.destroy()
    }

    fun getAttachableWebView(): WebView = getAttachableWebView()

    override fun onNavigationFinished(url: String) {
        coroutineScope?.launch {
            initializeMap()
        }
    }
}
