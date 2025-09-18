/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import com.maptiler.maptilersdk.R
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.MTMapStyleVariant
import com.maptiler.maptilersdk.map.style.MTStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Object representing the map on the screen. Used with XML.
 *
 * For Compose use [MTMapView].
 */
class MTMapViewClassic(
    context: Context,
    attrs: AttributeSet,
) : FrameLayout(context, attrs) {
    private lateinit var webView: WebView

    @Suppress("PropertyName")
    private lateinit var _referenceStyle: MTMapReferenceStyle

    @Suppress("PropertyName")
    private lateinit var _controller: MTMapViewController

    @Suppress("PropertyName")
    private lateinit var _options: MTMapOptions

    @Suppress("PropertyName")
    private var _styleVariant: MTMapStyleVariant? = null

    val scope = CoroutineScope(Dispatchers.Default)

    internal var bridge: MTBridge? = null

    fun initialize(
        referenceStyle: MTMapReferenceStyle,
        options: MTMapOptions,
        controller: MTMapViewController,
        styleVariant: MTMapStyleVariant? = null,
    ) {
        this._referenceStyle = referenceStyle
        this._options = options
        this._controller = controller
        this._styleVariant = styleVariant

        // Inflate and bind WebView first so the executor manages loading and callbacks.
        val rootView = LayoutInflater.from(context).inflate(R.layout.mtmapview_layout, this, true)
        webView = rootView.findViewById(R.id.map)
        _controller.setWebView(webView)

        // Configure settings only (clients + loading are handled by the executor).
        webView.apply {
            settings.javaScriptEnabled = true
            settings.allowFileAccess = true
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true
            settings.domStorageEnabled = true
            settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true

            isVerticalScrollBarEnabled = false
        }

        // Now bind the controller (attaches JS interface) and set options/style.
        _controller.bind(scope)
        _controller.options = options

        val style = MTStyle(referenceStyle, styleVariant)
        _controller.style = style
    }
}
