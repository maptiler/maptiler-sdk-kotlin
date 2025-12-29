/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.workers.navigable

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTBridgeReturnType.BoolValue
import com.maptiler.maptilersdk.bridge.MTBridgeReturnType.DoubleValue
import com.maptiler.maptilersdk.bridge.MTBridgeReturnType.Null
import com.maptiler.maptilersdk.bridge.MTBridgeReturnType.StringValue
import com.maptiler.maptilersdk.commands.navigation.AreTilesLoaded
import com.maptiler.maptilersdk.commands.navigation.CenterOnIpPoint
import com.maptiler.maptilersdk.commands.navigation.EaseTo
import com.maptiler.maptilersdk.commands.navigation.FitBounds
import com.maptiler.maptilersdk.commands.navigation.FitToIpBounds
import com.maptiler.maptilersdk.commands.navigation.FlyTo
import com.maptiler.maptilersdk.commands.navigation.GetBearing
import com.maptiler.maptilersdk.commands.navigation.GetBounds
import com.maptiler.maptilersdk.commands.navigation.GetCameraTargetElevation
import com.maptiler.maptilersdk.commands.navigation.GetCenter
import com.maptiler.maptilersdk.commands.navigation.GetCenterClampedToGround
import com.maptiler.maptilersdk.commands.navigation.GetCenterElevation
import com.maptiler.maptilersdk.commands.navigation.GetMaxBounds
import com.maptiler.maptilersdk.commands.navigation.GetMaxPitch
import com.maptiler.maptilersdk.commands.navigation.GetMinPitch
import com.maptiler.maptilersdk.commands.navigation.GetPitch
import com.maptiler.maptilersdk.commands.navigation.GetPixelRatio
import com.maptiler.maptilersdk.commands.navigation.GetRenderWorldCopies
import com.maptiler.maptilersdk.commands.navigation.GetRoll
import com.maptiler.maptilersdk.commands.navigation.JumpTo
import com.maptiler.maptilersdk.commands.navigation.PanBy
import com.maptiler.maptilersdk.commands.navigation.PanTo
import com.maptiler.maptilersdk.commands.navigation.Project
import com.maptiler.maptilersdk.commands.navigation.SetBearing
import com.maptiler.maptilersdk.commands.navigation.SetCenter
import com.maptiler.maptilersdk.commands.navigation.SetCenterClampedToGround
import com.maptiler.maptilersdk.commands.navigation.SetCenterElevation
import com.maptiler.maptilersdk.commands.navigation.SetMaxBounds
import com.maptiler.maptilersdk.commands.navigation.SetPadding
import com.maptiler.maptilersdk.commands.navigation.SetPitch
import com.maptiler.maptilersdk.commands.navigation.SetPixelRatio
import com.maptiler.maptilersdk.commands.navigation.SetRoll
import com.maptiler.maptilersdk.commands.navigation.SetVerticalFieldOfView
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.options.MTCameraOptions
import com.maptiler.maptilersdk.map.options.MTFitBoundsOptions
import com.maptiler.maptilersdk.map.options.MTFlyToOptions
import com.maptiler.maptilersdk.map.options.MTPaddingOptions
import com.maptiler.maptilersdk.map.types.MTBounds
import com.maptiler.maptilersdk.map.types.MTPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString

private const val DEFAULT_VERTICAL_FIELD_OF_VIEW = 36.87

internal class NavigableWorker(
    private val bridge: MTBridge,
    private val scope: CoroutineScope,
) : MTNavigable {
    override fun panBy(offset: MTPoint) {
        scope.launch {
            bridge.execute(
                PanBy(offset),
            )
        }
    }

    override fun panTo(coordinates: LngLat) {
        scope.launch {
            bridge.execute(
                PanTo(coordinates),
            )
        }
    }

    override fun flyTo(
        cameraOptions: MTCameraOptions,
        flyToOptions: MTFlyToOptions?,
    ) {
        scope.launch {
            bridge.execute(
                FlyTo(cameraOptions, flyToOptions),
            )
        }
    }

    override fun jumpTo(cameraOptions: MTCameraOptions) {
        scope.launch {
            bridge.execute(
                JumpTo(cameraOptions),
            )
        }
    }

    override fun easeTo(cameraOptions: MTCameraOptions) {
        scope.launch {
            bridge.execute(
                EaseTo(cameraOptions),
            )
        }
    }

    override fun fitBounds(
        bounds: MTBounds,
        options: MTFitBoundsOptions?,
    ) {
        scope.launch {
            bridge.execute(
                FitBounds(bounds, options),
            )
        }
    }

    override suspend fun getBounds(): MTBounds {
        val returnTypeValue =
            bridge.execute(
                GetBounds(),
            )

        return when (returnTypeValue) {
            is StringValue -> JsonConfig.json.decodeFromString<MTBounds>(returnTypeValue.value)
            else -> MTBounds(-180.0, -90.0, 180.0, 90.0)
        }
    }

    override fun fitToIpBounds() {
        scope.launch {
            bridge.execute(
                FitToIpBounds(),
            )
        }
    }

    override fun centerOnIpPoint() {
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            bridge.execute(
                CenterOnIpPoint(),
            )
        }
    }

    override suspend fun getMaxBounds(): MTBounds? {
        val returnTypeValue =
            bridge.execute(
                GetMaxBounds(),
            )

        return when (returnTypeValue) {
            is StringValue -> JsonConfig.json.decodeFromString<MTBounds>(returnTypeValue.value)
            is Null -> null
            else -> null
        }
    }

    override fun setMaxBounds(bounds: MTBounds?) {
        scope.launch {
            bridge.execute(
                SetMaxBounds(bounds),
            )
        }
    }

    override suspend fun getBearing(): Double {
        val returnTypeValue =
            bridge.execute(
                GetBearing(),
            )

        return when (returnTypeValue) {
            is StringValue -> returnTypeValue.value.toDouble()
            is DoubleValue -> returnTypeValue.value
            else -> 0.0
        }
    }

    override fun setBearing(bearing: Double) {
        scope.launch {
            bridge.execute(
                SetBearing(bearing),
            )
        }
    }

    override suspend fun getRoll(): Double {
        val returnTypeValue =
            bridge.execute(
                GetRoll(),
            )

        return when (returnTypeValue) {
            is StringValue -> returnTypeValue.value.toDouble()
            is DoubleValue -> returnTypeValue.value
            else -> 0.0
        }
    }

    override fun setRoll(roll: Double) {
        scope.launch {
            bridge.execute(
                SetRoll(roll),
            )
        }
    }

    override suspend fun getPitch(): Double {
        val returnTypeValue =
            bridge.execute(
                GetPitch(),
            )

        return when (returnTypeValue) {
            is StringValue -> returnTypeValue.value.toDouble()
            is DoubleValue -> returnTypeValue.value
            else -> 0.0
        }
    }

    override suspend fun getMaxPitch(): Double {
        val returnTypeValue =
            bridge.execute(
                GetMaxPitch(),
            )

        return when (returnTypeValue) {
            is StringValue -> returnTypeValue.value.toDouble()
            is DoubleValue -> returnTypeValue.value
            else -> 0.0
        }
    }

    override suspend fun getMinPitch(): Double {
        val returnTypeValue =
            bridge.execute(
                GetMinPitch(),
            )

        return when (returnTypeValue) {
            is StringValue -> returnTypeValue.value.toDouble()
            is DoubleValue -> returnTypeValue.value
            else -> 0.0
        }
    }

    override fun setPitch(pitch: Double) {
        val clamped = pitch.coerceIn(0.0, 85.0)
        scope.launch {
            bridge.execute(
                SetPitch(clamped),
            )
        }
    }

    override fun setVerticalFieldOfView(verticalFieldOfView: Double) {
        val sanitized =
            verticalFieldOfView.takeIf { it.isFinite() }
                ?.coerceIn(0.0, 50.0)
                ?: DEFAULT_VERTICAL_FIELD_OF_VIEW

        scope.launch {
            bridge.execute(
                SetVerticalFieldOfView(sanitized),
            )
        }
    }

    override suspend fun getCenter(): LngLat {
        val returnTypeValue =
            bridge.execute(
                GetCenter(),
            )

        return when (returnTypeValue) {
            is StringValue -> JsonConfig.json.decodeFromString<LngLat>(returnTypeValue.value)
            else -> LngLat(0.0, 0.0)
        }
    }

    override suspend fun getCenterClampedToGround(): Boolean {
        val returnTypeValue =
            bridge.execute(
                GetCenterClampedToGround(),
            )

        return when (returnTypeValue) {
            is BoolValue -> returnTypeValue.value
            is DoubleValue -> returnTypeValue.value != 0.0
            is StringValue -> {
                val normalized = returnTypeValue.value.trim().lowercase()
                when (normalized) {
                    "true" -> true
                    "false" -> false
                    else -> normalized.toDoubleOrNull()?.let { it != 0.0 } ?: false
                }
            }
            else -> false
        }
    }

    override suspend fun getRenderWorldCopies(): Boolean {
        val returnTypeValue =
            bridge.execute(
                GetRenderWorldCopies(),
            )

        return when (returnTypeValue) {
            is BoolValue -> returnTypeValue.value
            is DoubleValue -> returnTypeValue.value != 0.0
            is StringValue -> {
                val normalized = returnTypeValue.value.trim().lowercase()
                when (normalized) {
                    "true" -> true
                    "false" -> false
                    else -> normalized.toDoubleOrNull()?.let { it != 0.0 } ?: false
                }
            }
            else -> false
        }
    }

    override suspend fun areTilesLoaded(): Boolean {
        val returnTypeValue =
            bridge.execute(
                AreTilesLoaded(),
            )

        return when (returnTypeValue) {
            is BoolValue -> returnTypeValue.value
            is DoubleValue -> returnTypeValue.value != 0.0
            is StringValue -> {
                val normalized = returnTypeValue.value.trim().lowercase()
                when (normalized) {
                    "true" -> true
                    "false" -> false
                    else -> normalized.toDoubleOrNull()?.let { it != 0.0 } ?: false
                }
            }
            else -> false
        }
    }

    override suspend fun getPixelRatio(): Double {
        val returnTypeValue =
            bridge.execute(
                GetPixelRatio(),
            )

        return when (returnTypeValue) {
            is DoubleValue -> returnTypeValue.value
            is StringValue -> returnTypeValue.value.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
    }

    override suspend fun getCenterElevation(): Double {
        val returnTypeValue =
            bridge.execute(
                GetCenterElevation(),
            )

        return when (returnTypeValue) {
            is DoubleValue -> returnTypeValue.value
            is StringValue -> returnTypeValue.value.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
    }

    override suspend fun getCameraTargetElevation(): Double {
        val returnTypeValue =
            bridge.execute(
                GetCameraTargetElevation(),
            )

        return when (returnTypeValue) {
            is DoubleValue -> returnTypeValue.value
            is StringValue -> returnTypeValue.value.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
    }

    override suspend fun project(coordinates: LngLat): MTPoint {
        val returnTypeValue =
            bridge.execute(
                Project(coordinates),
            )

        return when (returnTypeValue) {
            is StringValue -> JsonConfig.json.decodeFromString<MTPoint>(returnTypeValue.value)
            else -> MTPoint(0.0, 0.0)
        }
    }

    override fun setCenter(center: LngLat) {
        scope.launch {
            bridge.execute(
                SetCenter(center),
            )
        }
    }

    override fun setIsCenterClampedToGround(isCenterClampedToGround: Boolean) {
        scope.launch {
            bridge.execute(
                SetCenterClampedToGround(isCenterClampedToGround),
            )
        }
    }

    override fun setPixelRatio(pixelRatio: Double) {
        val sanitized =
            if (pixelRatio.isFinite()) {
                pixelRatio.coerceAtLeast(0.0)
            } else {
                0.0
            }

        scope.launch {
            bridge.execute(
                SetPixelRatio(sanitized),
            )
        }
    }

    override fun setCenterElevation(elevation: Double) {
        scope.launch {
            bridge.execute(
                SetCenterElevation(elevation),
            )
        }
    }

    override fun setPadding(padding: MTPaddingOptions) {
        scope.launch {
            bridge.execute(
                SetPadding(padding),
            )
        }
    }
}
