/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.commands.style.SetSpace
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.options.MTSpace
import com.maptiler.maptilersdk.map.options.MTSpaceFaces
import com.maptiler.maptilersdk.map.options.MTSpacePath
import com.maptiler.maptilersdk.map.options.MTSpacePreset
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpaceTests {
    @Test fun setSpace_ColorOnly_ToJS_ReturnsValidJSString() {
        val space = MTSpace(color = 0x111122)
        val js = SetSpace(space).toJS()
        assertEquals("${MTBridge.MAP_OBJECT}.setSpace({\"color\":\"#111122\"});", js)
    }

    @Test fun setSpace_Preset_ToJS_ReturnsValidJSString() {
        val space = MTSpace(preset = MTSpacePreset.SPACE)
        val js = SetSpace(space).toJS()
        assertEquals("${MTBridge.MAP_OBJECT}.setSpace({\"preset\":\"space\"});", js)
    }

    @Test fun setSpace_Faces_ToJS_ContainsAllFaces() {
        val faces =
            MTSpaceFaces(
                pX = "https://example.com/px.png",
                nX = "https://example.com/nx.png",
                pY = "https://example.com/py.png",
                nY = "https://example.com/ny.png",
                pZ = "https://example.com/pz.png",
                nZ = "https://example.com/nz.png",
            )
        val js = SetSpace(MTSpace(faces = faces)).toJS()
        assertTrue(js.contains("\"faces\""))
        assertTrue(js.contains("\"pX\":\"https://example.com/px.png\""))
        assertTrue(js.contains("\"nX\":\"https://example.com/nx.png\""))
        assertTrue(js.contains("\"pY\":\"https://example.com/py.png\""))
        assertTrue(js.contains("\"nY\":\"https://example.com/ny.png\""))
        assertTrue(js.contains("\"pZ\":\"https://example.com/pz.png\""))
        assertTrue(js.contains("\"nZ\":\"https://example.com/nz.png\""))
    }

    @Test fun setSpace_Path_ToJS_ContainsBaseUrlAndOptionalFormat() {
        val jsWithFormat =
            SetSpace(
                MTSpace(path = MTSpacePath(baseUrl = "https://example.com/spacebox/transparent", format = "png")),
            ).toJS()
        assertTrue(jsWithFormat.contains("\"path\""))
        assertTrue(jsWithFormat.contains("\"baseUrl\":\"https://example.com/spacebox/transparent\""))
        assertTrue(jsWithFormat.contains("\"format\":\"png\""))

        val jsWithoutFormat = SetSpace(MTSpace(path = MTSpacePath(baseUrl = "https://example.com/spacebox/transparent"))).toJS()
        assertTrue(jsWithoutFormat.contains("\"baseUrl\":\"https://example.com/spacebox/transparent\""))
        // explicitNulls=false in JsonConfig means omitted when null
        assertTrue(!jsWithoutFormat.contains("\"format\""))
    }

    @Test fun mapOptions_Serializes_SpaceField() {
        val options = MTMapOptions(MTSpace(preset = MTSpacePreset.STARS))

        val json = JsonConfig.json.encodeToString(options)
        assertTrue(json.contains("\"space\""))
        assertTrue(json.contains("\"preset\":\"stars\""))
    }

    @Test fun mapOptions_Serializes_SpaceEnabled_BooleanTrue() {
        val options = MTMapOptions(spaceEnabled = true)
        val json = JsonConfig.json.encodeToString(options)
        assertTrue(json.contains("\"space\":true"))
    }
}
