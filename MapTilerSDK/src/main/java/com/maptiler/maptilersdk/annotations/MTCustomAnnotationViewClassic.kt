/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.annotations

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import com.maptiler.maptilersdk.events.MTEvent
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.MTMapViewClassic
import com.maptiler.maptilersdk.map.MTMapViewContentDelegate
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.types.MTData
import com.maptiler.maptilersdk.map.types.MTPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Classic (XML/Views) variant of a custom annotation view that can be added to [MTMapViewClassic].
 *
 * Requirements: This view relies on `ON_MOVE` and `ON_ZOOM` events.
 * Set `MTMapOptions.eventLevel` to `CAMERA_ONLY` (recommended) or `ALL` so these events are forwarded.
 *
 * This is a container: add your own child Android Views into it to render custom UI.
 * The container is centered on the projected map coordinates.
 */
class MTCustomAnnotationViewClassic(
    context: Context,
    widthPx: Int,
    heightPx: Int,
    initialCoordinates: LngLat,
    initialOffset: MTPoint = MTPoint(0.0, 0.0),
) : FrameLayout(context),
    MTAnnotation,
    MTMapViewContentDelegate {
    override val identifier: String = "annot${UUID.randomUUID().toString().replace("-", "")}"

    private var _coordinates: LngLat = initialCoordinates
    override val coordinates: LngLat
        get() = _coordinates

    /** Pixel offset from the projected point (x to the right, y down). */
    var offset: MTPoint = initialOffset
        set(value) {
            field = value
            recalculatePosition()
        }

    private var controller: MTMapViewController? = null
    private var mapView: MTMapViewClassic? = null

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + Job())

    init {
        layoutParams =
            LayoutParams(widthPx, heightPx).apply {
                width = widthPx
                height = heightPx
            }
        // Let this container draw over WebView
        isClickable = false
        isFocusable = false
    }

    /**
     * Adds this annotation view to the classic map view and starts listening to map events.
     */
    fun addTo(
        mapView: MTMapViewClassic,
        controller: MTMapViewController,
    ) {
        this.mapView = mapView
        this.controller = controller

        // Register for movement events
        controller.addContentDelegate(this)

        // Ensure we are attached and above the WebView
        if (parent != mapView) {
            val lp: ViewGroup.LayoutParams = layoutParams ?: LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            mapView.addView(this, lp)
            // Raise z-order to ensure visibility above WebView
            elevation = 1000f
            translationZ = 1000f
            mapView.bringChildToFront(this)
        }

        // Position once attached
        post { recalculatePosition() }
    }

    /** Removes the annotation view from its parent and stops updates. */
    fun remove() {
        controller?.removeContentDelegate(this)
        (parent as? ViewGroup)?.removeView(this)
        controller = null
        mapView = null
    }

    // Offset updates are handled via the property setter.

    /** MTAnnotation API: updates internal coordinates and reprojects. */
    override fun setCoordinates(
        coordinates: LngLat,
        mapViewController: MTMapViewController,
    ) {
        _coordinates = coordinates
        recalculatePosition()
    }

    private fun recalculatePosition() {
        val ctrl = controller ?: return
        val dm = resources.displayMetrics
        val density = dm.density

        scope.launch {
            val p = ctrl.project(_coordinates)
            val xPx = ((p.x + offset.x) * density).toFloat()
            val yPx = ((p.y + offset.y) * density).toFloat()

            // Center this container on the projected point
            val halfW = width / 2f
            val halfH = height / 2f

            this@MTCustomAnnotationViewClassic.x = xPx - halfW
            this@MTCustomAnnotationViewClassic.y = yPx - halfH

            // Keep on top
            mapView?.bringChildToFront(this@MTCustomAnnotationViewClassic)
        }
    }

    // React to map camera/size events
    override fun onEvent(
        event: MTEvent,
        data: MTData?,
    ) {
        when (event) {
            MTEvent.ON_MOVE,
            MTEvent.ON_ZOOM,
            -> recalculatePosition()
            else -> Unit
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        controller?.removeContentDelegate(this)
        scope.cancel()
    }
}
