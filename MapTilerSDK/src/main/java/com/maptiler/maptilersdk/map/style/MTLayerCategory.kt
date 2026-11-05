/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style

import com.maptiler.maptilersdk.map.style.layer.MTLayerType

/**
 * Semantic groups of map layers, independent of the style in use.
 *
 * Layers are matched by their vector tile `source-layer` rather than by layer id, so the same category works across
 * MapTiler reference styles and their variants. Both tile schemas used by MapTiler styles are covered:
 * MapTiler Planet v4 (Streets, Outdoor, Winter, Base, Topo) and the OpenMapTiles based schema
 * (OpenStreetMap, Dataviz, Backdrop, Aquarelle, Landscape, Ocean, Satellite hybrid).
 * A category only contains the layers that exist in the current style.
 *
 * Note: in OpenMapTiles based styles roads, railways and ferries share the `transportation` source layer,
 * so they all belong to [ROADS] there; [TRANSIT] is only populated by v4 styles.
 *
 * @property defaultName English display name, suitable for a legend. Localize in the app if needed.
 */
enum class MTLayerCategory(
    val defaultName: String,
    internal val sourceLayers: Set<String>,
    internal val sourceLayerPrefixes: Set<String> = emptySet(),
    internal val layerTypes: Set<MTLayerType> = emptySet(),
    internal val sourceIdHints: Set<String> = emptySet(),
) {
    /** Oceans, lakes, rivers, streams, dams and their labels. */
    WATER(
        "Water",
        setOf("water", "waterway", "water_name", "water_label", "water_centroid", "dam"),
    ),

    /** Forests, woods, grass, scrub, farmland, sand, ice, parks and protected areas. */
    NATURE(
        "Nature",
        setOf(
            "landcover", "globallandcover", "park",
            "forest", "wood", "grass", "scrub", "vegetation", "farmland", "sand", "ice", "tree",
            "protected_area_major_label", "protected_area_minor_label",
        ),
    ),

    /** Residential, commercial, industrial, education, hospital, leisure, military and similar areas. */
    LANDUSE(
        "Land use",
        setOf(
            "landuse",
            "residential", "commercial", "industrial", "construction", "cemetery", "education", "hospital",
            "leisure", "military", "military_label", "parking", "pedestrian", "ski",
        ),
    ),

    /** Roads, paths, trails, bridges and their labels. */
    ROADS(
        "Roads",
        setOf(
            "transportation", "transportation_name",
            "road", "road_label", "road_exit", "bridge", "pier", "pathway", "pathway_label", "trail",
        ),
    ),

    /** Railways, subways, ferries and aerial lifts. */
    TRANSIT(
        "Transit",
        setOf(
            "railway",
            "railway_label",
            "subway",
            "subway_label",
            "ferry",
            "ferry_label",
            "aerialway",
            "aerialway_label",
        ),
    ),

    /** Building footprints, 3D buildings and house numbers. */
    BUILDINGS("Buildings", setOf("building", "housenumber", "building_number")),

    /** Airport areas, runways and taxiways. */
    AEROWAYS("Aeroways", setOf("aeroway", "aerodrome_label", "aviation", "aviation_line")),

    /** Country, state and other administrative boundaries. */
    BOUNDARIES("Boundaries", setOf("boundary", "country_border", "country_border_disputed", "sub_border")),

    /** Labels of continents, countries, states, cities, towns, villages and islands. */
    PLACES(
        "Places",
        setOf(
            "place",
            "continent_label", "country_label", "country_disputed_label", "state_label", "city_label",
            "town_label", "place_label", "island_label", "archipelago_label",
        ),
    ),

    /** Points of interest, transit stations and street furniture. */
    POI(
        "Points of interest",
        sourceLayers = setOf("poi", "outdoor_poi", "street_furniture"),
        sourceLayerPrefixes = setOf("poi_"),
    ),

    /** Hillshading, contour lines, bathymetry, landforms, peaks and other relief features. */
    TERRAIN(
        "Terrain",
        sourceLayers =
            setOf(
                "contour", "contour_ft", "contour_line", "landform",
                "mountain_peak", "peak", "volcano", "saddle", "ridge_label", "cliff", "cliff_label",
            ),
        layerTypes = setOf(MTLayerType.HILLSHADE),
        sourceIdHints = setOf("hillshade"),
    ),
    ;

    /**
     * Returns whether [layer] belongs to this category.
     */
    fun matches(layer: MTStyleLayerInfo): Boolean {
        val sourceLayer = layer.sourceLayer
        if (sourceLayer != null) {
            if (sourceLayer in sourceLayers) return true
            if (sourceLayerPrefixes.any { sourceLayer.startsWith(it) }) return true
        }
        if (layer.type != null && layer.type in layerTypes) return true

        val source = layer.source?.lowercase() ?: return false
        return sourceIdHints.any { source.contains(it) }
    }
}
