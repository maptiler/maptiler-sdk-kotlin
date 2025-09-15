/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map

import android.content.Context
import android.webkit.WebView
import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.annotations.MTMarker
import com.maptiler.maptilersdk.annotations.MTTextPopup
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTJavaScriptInterface
import com.maptiler.maptilersdk.bridge.MTJavascriptDelegate
import com.maptiler.maptilersdk.bridge.WebViewExecutor
import com.maptiler.maptilersdk.bridge.WebViewExecutorDelegate
import com.maptiler.maptilersdk.commands.InitializeMap
import com.maptiler.maptilersdk.commands.annotations.SetCoordinatesToMarker
import com.maptiler.maptilersdk.commands.annotations.SetCoordinatesToTextPopup
import com.maptiler.maptilersdk.events.EventProcessor
import com.maptiler.maptilersdk.events.EventProcessorDelegate
import com.maptiler.maptilersdk.events.MTEvent
import com.maptiler.maptilersdk.logging.MTLogType
import com.maptiler.maptilersdk.logging.MTLogger
import com.maptiler.maptilersdk.map.gestures.MTGestureService
import com.maptiler.maptilersdk.map.options.MTCameraOptions
import com.maptiler.maptilersdk.map.options.MTFlyToOptions
import com.maptiler.maptilersdk.map.options.MTPaddingOptions
import com.maptiler.maptilersdk.map.style.MTStyle
import com.maptiler.maptilersdk.map.types.MTData
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

    fun onEventTriggered(
        event: MTEvent,
        data: MTData?,
    )
}

/**
 * Object exposing methods and properties that enable changes to the map,
 * and events that can be interacted with.
 */
class MTMapViewController(
    private val context: Context,
) : WebViewExecutorDelegate,
    EventProcessorDelegate,
    MTJavascriptDelegate,
    MTZoomable,
    MTNavigable {
    private var coroutineScope: CoroutineScope? = null

    private var bridge: MTBridge? = null
    private var eventProcessor: EventProcessor =
        EventProcessor().apply {
            delegate = this@MTMapViewController
        }
    private var webViewExecutor: WebViewExecutor? =
        WebViewExecutor(context).apply {
            delegate = this@MTMapViewController
        }

    private lateinit var zoomableWorker: ZoomableWorker
    private lateinit var navigableWorker: NavigableWorker

    private val jsInterface: MTJavaScriptInterface =
        MTJavaScriptInterface(context).apply {
            delegate = this@MTMapViewController
        }

    /**
     * Proxy style object of the map.
     */
    var style: MTStyle? = null
        set(value) {
            field = value
            onStyleUpdate()
        }

    private fun onStyleUpdate() {
        val br = bridge
        val scope = coroutineScope
        val currentStyle = style

        if (br != null && scope != null && currentStyle != null) {
            currentStyle.initWorker(br, scope)
        }
    }

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
        bridge = MTBridge(webViewExecutor)
    }

    internal suspend fun initializeMap() {
        val apiKey = MTConfig.apiKey

        if (options != null) {
            MTConfig.isSessionLogicEnabled = (options!!.isSessionLogicEnabled)
        }

        val isSessionLogicEnabled = MTConfig.isSessionLogicEnabled

        val currentStyle = style ?: return

        bridge!!.execute(
            InitializeMap(
                apiKey,
                options,
                currentStyle.referenceStyle,
                currentStyle.styleVariant,
                isSessionLogicEnabled,
            ),
        )
    }

    private fun initializeWorkers() {
        zoomableWorker = ZoomableWorker(bridge!!, coroutineScope!!)
        navigableWorker = NavigableWorker(bridge!!, coroutineScope!!)
    }

    internal fun bind(scope: CoroutineScope) {
        coroutineScope = scope
        gestureService = MTGestureService.create(coroutineScope!!, bridge!!, eventProcessor!!, this)

        initializeWorkers()

        webViewExecutor?.addJSInterface(jsInterface)
    }

    internal fun destroy() {
        webViewExecutor?.destroy()
    }

    internal fun setWebView(webView: WebView) {
        webViewExecutor?.setWebView(webView)
    }

    internal fun getAttachableWebView(): WebView = webViewExecutor?.getAttachableWebView()!!

    override fun onNavigationFinished(url: String) {
        coroutineScope?.launch(Dispatchers.Default) {
            try {
                initializeMap()
            } catch (error: Exception) {
                MTLogger.log("Map Init error $error", MTLogType.ERROR)
            }
        }
    }

    fun reload() {
        webViewExecutor?.reload()
    }

    // ANNOTATIONS

    internal fun setCoordinatesToMarker(marker: MTMarker) {
        coroutineScope?.launch {
            bridge?.execute(
                SetCoordinatesToMarker(marker),
            )
        }
    }

    internal fun setCoordinatesToTextPopup(popup: MTTextPopup) {
        coroutineScope?.launch {
            bridge?.execute(
                SetCoordinatesToTextPopup(popup),
            )
        }
    }

    // EVENTS

    /**
     * Map error handler.
     *
     * @param message Error message.
     */
    override fun onError(message: String) {
        MTLogger.log("Map error. Make sure your API Key and/or Style JSON are correct.", MTLogType.ERROR)
    }

    /**
     * WebGL Context error handler
     */
    override fun onWebGLContextLost() {
        MTLogger.log("Context lost, consider calling reload on MTMapViewController", MTLogType.CRITICAL_ERROR)
    }

    override fun onEvent(
        event: MTEvent,
        data: MTData?,
    ) {
        eventProcessor.registerEvent(event, data)
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

    /**
     * Changes any combination of center, zoom, bearing, and pitch, animating the transition along a curve that evokes flight.
     *
     * @param cameraOptions Options for controlling the desired location, zoom, bearing, and pitch of the camera.
     * @param flyToOptions Options describing the destination and animation of the transition.
     */
    override fun flyTo(
        cameraOptions: MTCameraOptions,
        flyToOptions: MTFlyToOptions?,
    ) = navigableWorker.flyTo(cameraOptions, flyToOptions)

    /**
     * Changes any combination of center, zoom, bearing, and pitch, without an animated transition
     *
     *@param cameraOptions Options for controlling the desired location, zoom, bearing, and pitch of the camera.
     */
    override fun jumpTo(cameraOptions: MTCameraOptions) = navigableWorker.jumpTo(cameraOptions)

    /**
     * Changes any combination of center, zoom, bearing and pitch with an animated transition between old and new values.
     *
     *@param cameraOptions Options for controlling the desired location, zoom, bearing, and pitch of the camera.
     */
    override fun easeTo(cameraOptions: MTCameraOptions) = navigableWorker.easeTo(cameraOptions)

    /**
     * Returns the map's current bearing.
     */
    override suspend fun getBearing(): Double = navigableWorker.getBearing()

    /**
     * Sets bearing of the map.
     *
     *@param bearing The bearing of the map, measured in degrees counter-clockwise from north.
     */
    override fun setBearing(bearing: Double) = navigableWorker.setBearing(bearing)

    /**
     * Returns the map's current roll.
     */
    override suspend fun getRoll(): Double = navigableWorker.getRoll()

    /**
     * Sets the map's roll angle.
     *
     *@param roll Desired roll.
     */
    override fun setRoll(roll: Double) = navigableWorker.setRoll(roll)

    /**
     * Returns the map's current center.
     */
    override suspend fun getCenter(): LngLat = navigableWorker.getCenter()

    /**
     * Project coordinates to point on the container.
     */
    override suspend fun project(coordinates: LngLat): com.maptiler.maptilersdk.map.types.MTPoint = navigableWorker.project(coordinates)

    /**
     * Sets the geographical center of the map.
     *
     *@param center Geographical center of the map.
     */
    override fun setCenter(center: LngLat) = navigableWorker.setCenter(center)

    /**
     * Sets the center clamped to the ground.
     *
     * If true, the elevation of the center point will automatically be set to the
     * terrain elevation (or zero if terrain is not enabled). If false, the elevation
     * of the center point will default to sea level and will not automatically update.
     *
     *@param isCenterClampedToGround Boolean indicating whether center is clamped to the ground.
     */
    override fun setIsCenterClampedToGround(isCenterClampedToGround: Boolean) =
        navigableWorker.setIsCenterClampedToGround(isCenterClampedToGround)

    /**
     * Sets the elevation of the map's center point, in meters above sea level.
     *
     *@param elevation Desired elevation.
     */
    override fun setCenterElevation(elevation: Double) = navigableWorker.setCenterElevation(elevation)

    /**
     * Sets the padding in pixels around the viewport.
     *
     *@param padding Custom options to use.
     */
    override fun setPadding(padding: MTPaddingOptions) = navigableWorker.setPadding(padding)

    override fun onEventTriggered(
        processor: EventProcessor,
        event: MTEvent,
        data: MTData?,
    ) {
        MTLogger.log("MTEvent triggered: $event", MTLogType.EVENT)

        delegate?.onEventTriggered(event, data)

        if (event == MTEvent.ON_READY) {
            delegate?.onMapViewInitialized()
        }

        if (event == MTEvent.ON_IDLE && style != null) {
            style?.processLayersQueueIfNeeded()
        }
    }
}
