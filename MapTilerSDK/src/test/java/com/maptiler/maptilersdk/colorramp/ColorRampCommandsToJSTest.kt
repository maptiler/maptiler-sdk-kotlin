package com.maptiler.maptilersdk.colorramp

import com.maptiler.maptilersdk.commands.colorramp.CreateColorRamp
import com.maptiler.maptilersdk.commands.colorramp.CreateColorRampFromArrayDefinition
import com.maptiler.maptilersdk.commands.colorramp.CreateColorRampFromCollection
import com.maptiler.maptilersdk.commands.colorramp.GetColorRampBounds
import com.maptiler.maptilersdk.commands.colorramp.GetColorRampCanvasStrip
import com.maptiler.maptilersdk.commands.colorramp.GetColorRampColor
import com.maptiler.maptilersdk.commands.colorramp.GetColorRampColorHex
import com.maptiler.maptilersdk.commands.colorramp.GetColorRampColorRelative
import com.maptiler.maptilersdk.commands.colorramp.GetColorRampRawStops
import com.maptiler.maptilersdk.commands.colorramp.ResampleColorRamp
import com.maptiler.maptilersdk.commands.colorramp.ReverseColorRamp
import com.maptiler.maptilersdk.commands.colorramp.ScaleColorRamp
import com.maptiler.maptilersdk.commands.colorramp.ScaleColorRampInPlace
import com.maptiler.maptilersdk.commands.colorramp.SetColorRampStops
import com.maptiler.maptilersdk.commands.colorramp.SetColorRampStopsInPlace
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorRampCommandsToJSTest {
    private val id = "cr_test"

    @Test
    fun createColorRamp_encodesOptions() {
        val cmd =
            CreateColorRamp(
                identifier = id,
                options =
                    MTColorRampOptions(
                        min = 0.0,
                        max = 10.0,
                        stops =
                            listOf(
                                MTColorStop(0.0, listOf(0, 0, 0)),
                                MTColorStop(10.0, listOf(255, 255, 255, 255)),
                            ),
                    ),
            )

        val js = cmd.toJS()
        assertTrue(js.startsWith("const $id = new maptilersdk.ColorRamp({"))
        assertTrue(js.contains("\"min\":0.0"))
        assertTrue(js.contains("\"max\":10.0"))
        assertTrue(js.contains("\"stops\":[{"))
    }

    @Test
    fun createFromArrayDefinition_encodesStopsAsArrayPairs() {
        val cmd =
            CreateColorRampFromArrayDefinition(
                identifier = id,
                stops =
                    listOf(
                        MTArrayColorRampStop(0.0, listOf(0, 0, 0, 0)),
                        MTArrayColorRampStop(1.0, listOf(255, 255, 255, 255)),
                    ),
            )

        val js = cmd.toJS()
        val expectedPrefix = "const $id = maptilersdk.ColorRamp.fromArrayDefinition("
        assertTrue(js.startsWith(expectedPrefix))
        assertTrue(js.contains("[0.0,[0,0,0,0]]"))
        assertTrue(js.contains("[1.0,[255,255,255,255]]"))
    }

    @Test
    fun createFromCollection_clonesBuiltin() {
        val cmd =
            CreateColorRampFromCollection(
                identifier = id,
                collectionName = "TURBO",
            )
        assertEquals("const $id = maptilersdk.ColorRampCollection.TURBO.clone();", cmd.toJS())
    }

    @Test
    fun scale_withClone_createsNewIdentifier() {
        val cmd =
            ScaleColorRamp(
                identifier = id,
                newIdentifier = "cr_scaled",
                min = 1.0,
                max = 5.0,
                options = MTColorRampCloneOptions(clone = true),
            )
        assertEquals("const cr_scaled = $id.scale(1.0, 5.0, {\"clone\":true});", cmd.toJS())
    }

    @Test
    fun scale_inPlace_mutatesExisting() {
        val cmd =
            ScaleColorRampInPlace(
                identifier = id,
                min = 2.0,
                max = 8.0,
                options = null,
            )
        assertEquals("$id.scale(2.0, 8.0);", cmd.toJS())
    }

    @Test
    fun reverse_withAndWithoutOptions() {
        val cmd1 =
            ReverseColorRamp(
                identifier = id,
                newIdentifier = "cr_rev",
                options = null,
            )
        assertEquals("const cr_rev = $id.reverse();", cmd1.toJS())

        val cmd2 =
            ReverseColorRamp(
                identifier = id,
                newIdentifier = null,
                options = MTColorRampCloneOptions(clone = false),
            )
        assertEquals("$id.reverse({\"clone\":false});", cmd2.toJS())
    }

    @Test
    fun setStops_withAndWithoutClone() {
        val stops =
            listOf(
                MTColorStop(0.0, listOf(0, 0, 0)),
                MTColorStop(1.0, listOf(255, 255, 255, 255)),
            )
        val cmd1 =
            SetColorRampStops(
                identifier = id,
                newIdentifier = "cr2",
                stops = stops,
                options = null,
            )
        val js1 = cmd1.toJS()
        assertTrue(js1.startsWith("const cr2 = $id.setStops([{"))

        val cmd2 =
            SetColorRampStopsInPlace(
                identifier = id,
                stops = stops,
                options = MTColorRampCloneOptions(false),
            )
        val js2 = cmd2.toJS()
        assertTrue(js2.startsWith("$id.setStops([{"))
        assertTrue(js2.endsWith(");"))
        assertTrue(js2.contains("\"clone\":false"))
    }

    @Test
    fun resample_generatesMethodAndSamples() {
        val cmd =
            ResampleColorRamp(
                identifier = id,
                newIdentifier = "cr3",
                method = MTColorRampResamplingMethod.EASE_OUT_SQUARE,
                samples = 21,
            )
        assertEquals(
            "const cr3 = $id.resample('ease-out-square', 21);",
            cmd.toJS(),
        )
    }

    @Test
    fun getters_encodeArguments() {
        assertEquals("JSON.stringify($id.getRawColorStops());", GetColorRampRawStops(id).toJS())
        assertEquals("JSON.stringify($id.getBounds());", GetColorRampBounds(id).toJS())

        val colorJs = GetColorRampColor(id, 5.0, MTColorRampGetColorOptions(true)).toJS()
        assertTrue(colorJs.startsWith("JSON.stringify($id.getColor(5.0, {\"smooth\":true}))"))

        val colorRelJs = GetColorRampColorRelative(id, 0.5, null).toJS()
        assertEquals("JSON.stringify($id.getColorRelative(0.5));", colorRelJs)

        val hexJs = GetColorRampColorHex(id, 3.2, MTColorRampGetColorOptions(false)).toJS()
        assertEquals("$id.getColorHex(3.2, {\"smooth\":false});", hexJs)
    }

    @Test
    fun canvasStrip_optionsSerializedWhenProvided() {
        val jsNoOpts = GetColorRampCanvasStrip(id, null).toJS()
        assertEquals("$id.getCanvasStrip().toDataURL();", jsNoOpts)

        val jsWithOpts =
            GetColorRampCanvasStrip(
                id,
                MTColorRampCanvasOptions(horizontal = false, size = 256, smooth = false),
            ).toJS()
        assertEquals(
            "$id.getCanvasStrip({\"horizontal\":false,\"size\":256,\"smooth\":false}).toDataURL();",
            jsWithOpts,
        )
    }
}
