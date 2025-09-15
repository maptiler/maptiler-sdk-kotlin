/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.workers.navigable

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTBridgeReturnType.DoubleValue
import com.maptiler.maptilersdk.bridge.MTBridgeReturnType.StringValue
import com.maptiler.maptilersdk.commands.navigation.EaseTo
import com.maptiler.maptilersdk.commands.navigation.FlyTo
import com.maptiler.maptilersdk.commands.navigation.GetBearing
import com.maptiler.maptilersdk.commands.navigation.GetCenter
import com.maptiler.maptilersdk.commands.navigation.GetRoll
import com.maptiler.maptilersdk.commands.navigation.JumpTo
import com.maptiler.maptilersdk.commands.navigation.PanBy
import com.maptiler.maptilersdk.commands.navigation.PanTo
import com.maptiler.maptilersdk.commands.navigation.Project
import com.maptiler.maptilersdk.commands.navigation.SetBearing
import com.maptiler.maptilersdk.commands.navigation.SetCenter
import com.maptiler.maptilersdk.commands.navigation.SetCenterClampedToGround
import com.maptiler.maptilersdk.commands.navigation.SetCenterElevation
import com.maptiler.maptilersdk.commands.navigation.SetPadding
import com.maptiler.maptilersdk.commands.navigation.SetRoll
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.options.MTCameraOptions
import com.maptiler.maptilersdk.map.options.MTFlyToOptions
import com.maptiler.maptilersdk.map.options.MTPaddingOptions
import com.maptiler.maptilersdk.map.types.MTPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString

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
