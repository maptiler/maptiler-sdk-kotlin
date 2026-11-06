/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk

import com.maptiler.maptilersdk.commands.style.GetLayerProperty
import com.maptiler.maptilersdk.commands.style.GetStyleLayers
import com.maptiler.maptilersdk.commands.style.LayerPropertyKind
import com.maptiler.maptilersdk.commands.style.SetPropertyForLayers
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.style.MTLayerCategory
import com.maptiler.maptilersdk.map.style.MTLayerQuery
import com.maptiler.maptilersdk.map.style.MTLegendColorResolver
import com.maptiler.maptilersdk.map.style.MTStyleLayerInfo
import com.maptiler.maptilersdk.map.style.MTStyleLayerInfoSurrogate
import com.maptiler.maptilersdk.map.style.dsl.PropertyValue
import com.maptiler.maptilersdk.map.style.layer.MTLayerType
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LayerQueryTests {
    private fun layer(
        id: String,
        type: MTLayerType? = MTLayerType.FILL,
        source: String? = "maptiler_planet",
        sourceLayer: String? = null,
        isVisible: Boolean = true,
    ) = MTStyleLayerInfo(id, type, type?.name?.lowercase() ?: "unknown", source, sourceLayer, isVisible, null, null)

    // Commands

    @Test fun getStyleLayers_IsPrimitiveAndUsesLayersOrder() {
        val js = GetStyleLayers.toJS()
        assertTrue(GetStyleLayers.isPrimitiveReturnType)
        assertTrue(js.contains("getLayersOrder()"))
        assertTrue(js.contains("sourceLayer: l.sourceLayer || null"))
    }

    @Test fun getLayerProperty_EscapesIdAndName() {
        val js = GetLayerProperty("Water \"area\"", "fill-color", LayerPropertyKind.PAINT).toJS()
        assertTrue(js.contains("m.getLayer(\"Water \\\"area\\\"\")"))
        assertTrue(js.contains("m.getPaintProperty(\"Water \\\"area\\\"\", \"fill-color\")"))
    }

    @Test fun setPropertyForLayers_ToJS_BatchesAllIds() {
        val js =
            SetPropertyForLayers(
                listOf("Water", "River"),
                "visibility",
                PropertyValue.Str("none"),
                LayerPropertyKind.LAYOUT,
            ).toJS()
        assertEquals(
            "(function(ids, value) { var m = map;" +
                " ids.forEach(function(id) { if (m.getLayer(id)) { m.setLayoutProperty(id, \"visibility\", value); } });" +
                " })([\"Water\",\"River\"], \"none\");",
            js,
        )
    }

    // Decoding

    @Test fun surrogate_DecodesKnownAndUnknownTypes() {
        val json =
            """[{"id":"Water","type":"fill","source":"maptiler_planet","sourceLayer":"water","visibility":"none"},
               {"id":"Sky","type":"sky"}]"""
        val layers = JsonConfig.json.decodeFromString<List<MTStyleLayerInfoSurrogate>>(json).map { it.toInfo() }

        assertEquals(MTLayerType.FILL, layers[0].type)
        assertEquals("water", layers[0].sourceLayer)
        assertFalse(layers[0].isVisible)
        assertNull(layers[1].type)
        assertEquals("sky", layers[1].rawType)
        assertTrue(layers[1].isVisible)
    }

    // Query

    @Test fun emptyQuery_MatchesEverything() {
        assertTrue(MTLayerQuery().matches(layer("anything")))
    }

    @Test fun query_CombinesCriteriaWithAnd() {
        val query = MTLayerQuery(categories = setOf(MTLayerCategory.WATER), types = setOf(MTLayerType.LINE))
        assertTrue(query.matches(layer("River", MTLayerType.LINE, sourceLayer = "waterway")))
        assertFalse(query.matches(layer("Water", MTLayerType.FILL, sourceLayer = "water")))
        assertFalse(query.matches(layer("Road", MTLayerType.LINE, sourceLayer = "transportation")))
    }

    @Test fun query_IdContainsIsCaseInsensitive() {
        assertTrue(MTLayerQuery(idContains = "water").matches(layer("Water shadow")))
    }

    // Categories

    @Test fun categories_MatchBySourceLayer() {
        assertTrue(MTLayerCategory.WATER.matches(layer("Lake", sourceLayer = "water")))
        assertTrue(MTLayerCategory.NATURE.matches(layer("Grass", sourceLayer = "landcover")))
        assertTrue(MTLayerCategory.NATURE.matches(layer("Park", sourceLayer = "park")))
        assertFalse(MTLayerCategory.NATURE.matches(layer("Residential", sourceLayer = "landuse")))
    }

    @Test fun categories_MatchPlanetV4SourceLayers() {
        assertTrue(MTLayerCategory.WATER.matches(layer("River labels", MTLayerType.SYMBOL, sourceLayer = "water_label")))
        assertTrue(MTLayerCategory.NATURE.matches(layer("Forest", sourceLayer = "forest")))
        assertTrue(MTLayerCategory.ROADS.matches(layer("Road", MTLayerType.LINE, sourceLayer = "road")))
        assertTrue(MTLayerCategory.TRANSIT.matches(layer("Railway", MTLayerType.LINE, sourceLayer = "railway")))
        assertTrue(MTLayerCategory.PLACES.matches(layer("City labels", MTLayerType.SYMBOL, sourceLayer = "city_label")))
        assertTrue(MTLayerCategory.BOUNDARIES.matches(layer("Country border", MTLayerType.LINE, sourceLayer = "country_border")))
        assertTrue(MTLayerCategory.TERRAIN.matches(layer("Peak labels", MTLayerType.SYMBOL, sourceLayer = "peak")))
    }

    @Test fun poi_MatchesSourceLayerPrefix() {
        assertTrue(MTLayerCategory.POI.matches(layer("Food", MTLayerType.SYMBOL, sourceLayer = "poi_food")))
        assertFalse(MTLayerCategory.POI.matches(layer("Point", MTLayerType.SYMBOL, sourceLayer = "point")))
    }

    @Test fun categories_DoNotOverlap() {
        val sourceLayers = MTLayerCategory.entries.flatMap { it.sourceLayers }
        assertEquals(sourceLayers.size, sourceLayers.toSet().size)
    }

    @Test fun terrain_MatchesHillshadeTypeAndSourceHint() {
        assertTrue(MTLayerCategory.TERRAIN.matches(layer("Hillshade", MTLayerType.HILLSHADE, source = "terrain-rgb")))
        assertTrue(MTLayerCategory.TERRAIN.matches(layer("Shade", MTLayerType.RASTER, source = "hillshade")))
        assertFalse(MTLayerCategory.TERRAIN.matches(layer("Satellite", MTLayerType.RASTER, source = "satellite")))
    }

    // Legend colors

    @Test fun firstColor_PlainColor() {
        assertEquals("#aad3df", MTLegendColorResolver.firstColor(JsonPrimitive("#aad3df")))
    }

    @Test fun firstColor_InterpolateExpression() {
        val expr =
            JsonConfig.json.parseToJsonElement(
                """["interpolate",["linear"],["zoom"],4,"hsl(205, 56%, 73%)",10,"hsl(205, 56%, 80%)"]""",
            )
        assertEquals("hsl(205, 56%, 73%)", MTLegendColorResolver.firstColor(expr))
    }

    @Test fun firstColor_LegacyStops() {
        val stops = JsonConfig.json.parseToJsonElement("""{"base":1,"stops":[[0,"rgba(1,2,3,1)"],[10,"#fff"]]}""")
        assertEquals("rgba(1,2,3,1)", MTLegendColorResolver.firstColor(stops))
    }

    @Test fun firstColor_IgnoresNonColorStrings() {
        val expr = JsonConfig.json.parseToJsonElement("""["match",["get","class"],"wood","#c0d8a0","#e0e8d0"]""")
        assertEquals("#c0d8a0", MTLegendColorResolver.firstColor(expr))
        assertNull(MTLegendColorResolver.firstColor(JsonPrimitive("linear")))
    }

    @Test fun swatchSource_PrefersFillOverSymbol() {
        val layers =
            listOf(
                layer("Water name", MTLayerType.SYMBOL, sourceLayer = "water_name"),
                layer("River", MTLayerType.LINE, sourceLayer = "waterway"),
                layer("Water", MTLayerType.FILL, sourceLayer = "water"),
            )
        val (picked, property) = MTLegendColorResolver.swatchSource(MTLayerCategory.WATER, layers)!!
        assertEquals("Water", picked.id)
        assertEquals("fill-color", property)
    }

    @Test fun swatchSource_PrefersRepresentativeSourceLayer() {
        val layers =
            listOf(
                layer("Ice", sourceLayer = "ice"),
                layer("Grass", sourceLayer = "grass"),
                layer("Wood", sourceLayer = "wood"),
            )
        assertEquals("Wood", MTLegendColorResolver.swatchSource(MTLayerCategory.NATURE, layers)!!.first.id)
    }
}
