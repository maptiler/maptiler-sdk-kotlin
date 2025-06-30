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
import com.maptiler.maptilersdk.map.types.MTPoint
import com.maptiler.maptilersdk.map.workers.navigable.MTNavigable
import com.maptiler.maptilersdk.map.workers.navigable.NavigableWorker
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
    MTZoomable,
    MTNavigable {
    private var coroutineScope: CoroutineScope? = null

    private var bridge: MTBridge? = null
    private var webViewExecutor: WebViewExecutor? =
        WebViewExecutor(context).apply {
            delegate = this@MTMapViewController
        }

    private lateinit var zoomableWorker: ZoomableWorker
    private lateinit var navigableWorker: NavigableWorker

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
        zoomableWorker = ZoomableWorker(bridge!!, coroutineScope!!)
        navigableWorker = NavigableWorker(bridge!!, coroutineScope!!)
    }

    fun bind(scope: CoroutineScope) {
        coroutineScope = scope
        initializeWorkers()
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

    // ZOOMABLE

    /**
     * Increases the map's zoom level by 1.
     */
    override fun zoomIn() = zoomableWorker.zoomIn()

    /**
     * Decreases the map's zoom level by 1.
     */
    override fun zoomOut() = zoomableWorker.zoomOut()

    /**
     * Returns the map's current zoom level.
     */
    override suspend fun getZoom(): Double = zoomableWorker.getZoom()

    /**
     * Sets the map's zoom level.
     *
     * @param zoom The zoom level to set (0-20).
     */
    override fun setZoom(zoom: Double) = zoomableWorker.setZoom(zoom)

    /**
     * Sets the map's maximum zoom.
     * @param maxZoom Desired zoom.
     */
    override fun setMaxZoom(maxZoom: Double) = zoomableWorker.setMaxZoom(maxZoom)

    /**
     * Sets the map's minimum zoom.
     * @param minZoom Desired zoom.
     */
    override fun setMinZoom(minZoom: Double) = zoomableWorker.setMinZoom(minZoom)

    // NAVIGABLE

    /**
     * Pans the map by the specified offset.
     *
     * @param offset Offset to pan by.
     */
    override fun panBy(offset: MTPoint) = navigableWorker.panBy(offset)

    /**
     * Pans the map to the specified location with an animated transition.
     *
     * @param coordinates Coordinates to pan to.
     */
    override fun panTo(coordinates: LngLat) = navigableWorker.panTo(coordinates)
}
