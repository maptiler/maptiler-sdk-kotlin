/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map

import kotlinx.serialization.Serializable

/**
 * A geographical location which contains a latitude, longitude pair.
 */
@Serializable
data class LngLat(
    val lng: Double,
    val lat: Double,
)

/**
 * Sets combination of center, bearing and pitch, as well as roll and elevation.
 */
class MTMapCameraHelper private constructor(
    val centerCoordinate: LngLat? = null,
    val bearing: Double? = null,
    val pitch: Double? = null,
    val roll: Double? = null,
    val elevation: Double? = null,
) {
    companion object {
        /**
         * Returns camera object with all properties set to 0.
         */
        fun getCamera(): MTMapCameraHelper =
            MTMapCameraHelper(
                centerCoordinate = LngLat(0.0, 0.0),
                bearing = 0.0,
                pitch = 0.0,
                roll = 0.0,
                elevation = 0.0,
            )

        /**
         * Returns camera object initialized from map style options.
         *
         * If any of the properties is not set within the style, it will default to 0.
         */
        fun getCameraFromMapStyle(): MTMapCameraHelper = MTMapCameraHelper()

        /**
         * Returns camera object with the given center coordinate, bearing, pitch, roll and elevation.
         */
        fun cameraLookingAtCenterCoordinate(
            centerCoordinate: LngLat,
            bearing: Double,
            pitch: Double,
            roll: Double,
            elevation: Double,
        ): MTMapCameraHelper =
            MTMapCameraHelper(
                centerCoordinate = centerCoordinate,
                bearing = bearing,
                pitch = pitch,
                roll = roll,
                elevation = elevation,
            )

        /**
         * Returns camera object with the given center coordinate, bearing and pitch.
         *
         * Looks for roll and elevation in the map's style object.
         * If they are not specified in the style, they will default to 0.
         */
        fun cameraLookingAtCenterCoordinate(
            centerCoordinate: LngLat,
            bearing: Double,
            pitch: Double,
        ): MTMapCameraHelper =
            MTMapCameraHelper(
                centerCoordinate = centerCoordinate,
                bearing = bearing,
                pitch = pitch,
            )
    }

    /**
     * Returns boolean indicating whether camera object is equal to the receiver.
     *
     * @param otherCamera MTMapCameraHelper to compare with.
     */
    fun isEqualTo(otherCamera: MTMapCameraHelper): Boolean {
        val centerIsEqual = centerCoordinate == otherCamera.centerCoordinate
        val bearingIsEqual = bearing == otherCamera.bearing
        val pitchIsEqual = pitch == otherCamera.pitch
        val rollIsEqual = roll == otherCamera.roll
        val elevationIsEqual = elevation == otherCamera.elevation

        return centerIsEqual && bearingIsEqual && pitchIsEqual && rollIsEqual && elevationIsEqual
    }
}
