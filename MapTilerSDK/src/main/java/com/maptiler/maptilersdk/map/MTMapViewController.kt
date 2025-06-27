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
import com.maptiler.maptilersdk.map.gestures.MTGestureService
import com.maptiler.maptilersdk.map.style.MTStyle
import com.maptiler.maptilersdk.map.workers.zoomable.MTZoomable
import com.maptiler.maptilersdk.map.workers.zoomable.ZoomableWorker
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
) : WebViewExecutorDelegate,
    MTZoomable {
    var coroutineScope: CoroutineScope? = null

    private var bridge: MTBridge? = null
    private var webViewExecutor: WebViewExecutor? =
        WebViewExecutor(context).apply {
            delegate = this@MTMapViewController
        }

    private lateinit var zoomableWorker: ZoomableWorker

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

    /**
     * Service responsible for gestures handling
     */
    var gestureService: MTGestureService? = null

    init {
        webViewExecutor = WebViewExecutor(context)
        bridge = MTBridge(webViewExecutor)

        gestureService = MTGestureService.create(bridge!!, this)

        initializeWorkers()
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

    private fun initializeWorkers() {
        zoomableWorker = ZoomableWorker(bridge!!)
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

    override suspend fun zoomIn() = zoomableWorker.zoomIn()

    override suspend fun zoomOut() = zoomableWorker.zoomOut()

    override suspend fun getZoom(): Double = zoomableWorker.getZoom()

    override suspend fun setZoom(zoom: Double) = zoomableWorker.setZoom(zoom)

    override suspend fun setMaxZoom(maxZoom: Double) = zoomableWorker.setMaxZoom(maxZoom)

    override suspend fun setMinZoom(minZoom: Double) = zoomableWorker.setMinZoom(minZoom)
}
