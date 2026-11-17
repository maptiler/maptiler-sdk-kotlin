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
}
