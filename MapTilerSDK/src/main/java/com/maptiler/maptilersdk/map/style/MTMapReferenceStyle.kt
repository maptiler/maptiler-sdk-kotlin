/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style

import java.net.URL

/**
 * Defines purpose and guidelines on what information is displayed.
 */
sealed class MTMapReferenceStyle {
    /**
     * The classic default style, perfect for urban areas.
     */
    data object STREETS : MTMapReferenceStyle()

    /**
     * High resolution satellite images.
     */
    data object SATELLITE : MTMapReferenceStyle()

    /**
     * A solid hiking companion, with peaks, parks, isolines and more.
     */
    data object OUTDOOR : MTMapReferenceStyle()

    /**
     * A map for developing skiing, snowboarding and other winter activities and adventures.
     */
    data object WINTER : MTMapReferenceStyle()

    /**
     * A minimalist style for data visualization.
     */
    data object DATAVIZ : MTMapReferenceStyle()

    /**
     *  A minimalist alternative to STREETS, with a touch of flat design.
     */
    data object BASIC : MTMapReferenceStyle()

    /**
     *  A minimalist style for high contrast navigation.
     */
    data object BRIGHT : MTMapReferenceStyle()

    /**
     *  Reference style for topographic study.
     */
    data object TOPO : MTMapReferenceStyle()

    /**
     * Reference style for very high contrast stylish maps.
     */
    data object TONER : MTMapReferenceStyle()

    /**
     * Neutral greyscale style with hillshading suitable for colorful terrain-aware visualization.
     */
    data object BACKDROP : MTMapReferenceStyle()

    /**
     * Reference style without any variants.
     */
    data object OPENSTREETMAP : MTMapReferenceStyle()

    /**
     * Watercolor map for creative use.
     */
    data object AQUARELLE : MTMapReferenceStyle()

    /**
     * Light terrain map for data overlays.
     */
    data object LANDSCAPE : MTMapReferenceStyle()

    /**
     * Detailed map of the ocean seafloor and bathymetry.
     */
    data object OCEAN : MTMapReferenceStyle()

    /**
     * Custom style from the URL.
     */
    data class CUSTOM(
        val url: URL,
    ) : MTMapReferenceStyle()

    fun getVariants(): List<MTMapStyleVariant>? =
        when (this) {
            STREETS ->
                listOf(
                    MTMapStyleVariant.DEFAULT_VARIANT,
                    MTMapStyleVariant.LIGHT,
                    MTMapStyleVariant.DARK,
                    MTMapStyleVariant.PASTEL,
                    MTMapStyleVariant.NIGHT,
                )
            SATELLITE ->
                listOf(
                    MTMapStyleVariant.DEFAULT_VARIANT,
                )
            OUTDOOR ->
                listOf(
                    MTMapStyleVariant.DEFAULT_VARIANT,
                    MTMapStyleVariant.LIGHT,
                    MTMapStyleVariant.DARK,
                )
            WINTER ->
                listOf(
                    MTMapStyleVariant.DEFAULT_VARIANT,
                    MTMapStyleVariant.LIGHT,
                    MTMapStyleVariant.DARK,
                )
            DATAVIZ ->
                listOf(
                    MTMapStyleVariant.DEFAULT_VARIANT,
                    MTMapStyleVariant.LIGHT,
                    MTMapStyleVariant.DARK,
                )
            BASIC ->
                listOf(
                    MTMapStyleVariant.DEFAULT_VARIANT,
                    MTMapStyleVariant.LIGHT,
                    MTMapStyleVariant.DARK,
                    MTMapStyleVariant.PASTEL,
                )
            BRIGHT ->
                listOf(
                    MTMapStyleVariant.DEFAULT_VARIANT,
                    MTMapStyleVariant.LIGHT,
                    MTMapStyleVariant.DARK,
                    MTMapStyleVariant.PASTEL,
                )
            TOPO ->
                listOf(
                    MTMapStyleVariant.DEFAULT_VARIANT,
                    MTMapStyleVariant.SHINY,
                    MTMapStyleVariant.PASTEL,
                    MTMapStyleVariant.TOPOGRAPHIQUE,
                )
            TONER ->
                listOf(
                    MTMapStyleVariant.DEFAULT_VARIANT,
                    MTMapStyleVariant.BACKGROUND,
                    MTMapStyleVariant.LITE,
                    MTMapStyleVariant.LINES,
                )
            BACKDROP ->
                listOf(
                    MTMapStyleVariant.DEFAULT_VARIANT,
                    MTMapStyleVariant.LIGHT,
                    MTMapStyleVariant.DARK,
                )
            OPENSTREETMAP ->
                listOf(
                    MTMapStyleVariant.DEFAULT_VARIANT,
                )
            AQUARELLE ->
                listOf(
                    MTMapStyleVariant.DEFAULT_VARIANT,
                    MTMapStyleVariant.DARK,
                    MTMapStyleVariant.VIVID,
                )
            LANDSCAPE ->
                listOf(
                    MTMapStyleVariant.DEFAULT_VARIANT,
                    MTMapStyleVariant.DARK,
                    MTMapStyleVariant.VIVID,
                )
            OCEAN ->
                listOf(
                    MTMapStyleVariant.DEFAULT_VARIANT,
                )
            is CUSTOM ->
                listOf(
                    MTMapStyleVariant.DEFAULT_VARIANT,
                )
        }

    fun isCustom(): Boolean = this is CUSTOM

    fun getName(): String =
        when (this) {
            STREETS -> "STREETS"
            SATELLITE -> "SATELLITE"
            OUTDOOR -> "OUTDOOR"
            WINTER -> "WINTER"
            DATAVIZ -> "DATAVIZ"
            BASIC -> "BASIC"
            BRIGHT -> "BRIGHT"
            TOPO -> "TOPO"
            TONER -> "TONER"
            BACKDROP -> "BACKDROP"
            OPENSTREETMAP -> "OPENSTREETMAP"
            AQUARELLE -> "AQUARELLE"
            LANDSCAPE -> "LANDSCAPE"
            OCEAN -> "OCEAN"
            is CUSTOM -> this.url.toString()
        }

    companion object {
        fun all(): List<MTMapReferenceStyle> =
            listOf(
                STREETS,
                SATELLITE,
                OUTDOOR,
                WINTER,
                DATAVIZ,
                BASIC,
                BRIGHT,
                TOPO,
                TONER,
                BACKDROP,
                OPENSTREETMAP,
                AQUARELLE,
                LANDSCAPE,
                OCEAN,
            )
    }
}
