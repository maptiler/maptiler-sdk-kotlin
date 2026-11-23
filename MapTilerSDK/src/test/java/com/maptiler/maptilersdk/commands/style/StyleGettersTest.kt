package com.maptiler.maptilersdk.commands.style

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StyleGettersTest {
    @Test
    fun `getFilter emits expected JS`() {
        val command = GetFilter("layer1")
        assertEquals("JSON.stringify(map.getFilter(\"layer1\"));", command.toJS())
        assertTrue(command.isPrimitiveReturnType)
    }

    @Test
    fun `getGlyphs emits expected JS`() {
        val command = GetGlyphs()
        assertEquals("map.getGlyphs();", command.toJS())
        assertTrue(command.isPrimitiveReturnType)
    }

    @Test
    fun `getLayersOrder emits expected JS`() {
        val command = GetLayersOrder()
        assertEquals("JSON.stringify(map.getLayersOrder());", command.toJS())
        assertTrue(command.isPrimitiveReturnType)
    }

    @Test
    fun `getLight emits expected JS`() {
        val command = GetLight()
        assertEquals("JSON.stringify(map.getLight());", command.toJS())
        assertTrue(command.isPrimitiveReturnType)
    }

    @Test
    fun `GetLayerProperty with stringify emits expected JS`() {
        val command = GetLayerProperty("layer2", "prop", LayerPropertyKind.LAYOUT)
        assertEquals(
            "(function() { var m = map; if (!m.getLayer(\"layer2\")) { return null; } " +
                "var v = m.getLayoutProperty(\"layer2\", \"prop\"); return v === undefined ? null : JSON.stringify(v); })();",
            command.toJS(),
        )
        assertTrue(command.isPrimitiveReturnType)
    }

    @Test
    fun `GetSky emits expected JS`() {
        val command = GetSky()
        assertEquals("JSON.stringify(map.getSky());", command.toJS())
        assertTrue(command.isPrimitiveReturnType)
    }

    @Test
    fun `GetSource emits expected JS`() {
        val command = GetSource("mysource")
        assertEquals("JSON.stringify(map.getSource(\"mysource\").serialize());", command.toJS())
        assertTrue(command.isPrimitiveReturnType)
    }

    @Test
    fun `GetSprite emits expected JS`() {
        val command = GetSprite()
        assertEquals("JSON.stringify(map.getSprite());", command.toJS())
        assertTrue(command.isPrimitiveReturnType)
    }

    @Test
    fun `GetStyle emits expected JS`() {
        val command = GetStyle()
        assertEquals("JSON.stringify(map.getStyle());", command.toJS())
        assertTrue(command.isPrimitiveReturnType)
    }

    @Test
    fun `GetTerrain emits expected JS`() {
        val command = GetTerrain()
        assertEquals("JSON.stringify(map.getTerrain());", command.toJS())
        assertTrue(command.isPrimitiveReturnType)
    }

    @Test
    fun `GetTerrainExaggeration emits expected JS`() {
        val command = GetTerrainExaggeration()
        assertEquals("map.getTerrainExaggeration();", command.toJS())
        assertTrue(command.isPrimitiveReturnType)
    }

    @Test
    fun `IsLanguageUpdated emits expected JS`() {
        val command = IsLanguageUpdated()
        assertEquals("map.isLanguageUpdated();", command.toJS())
        assertTrue(command.isPrimitiveReturnType)
    }
}
