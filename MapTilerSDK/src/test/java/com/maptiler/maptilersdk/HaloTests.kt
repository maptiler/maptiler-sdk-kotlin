/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.commands.style.DisableHaloAnimations
import com.maptiler.maptilersdk.commands.style.SetHalo
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.options.MTHalo
import com.maptiler.maptilersdk.map.options.MTHaloOption
import com.maptiler.maptilersdk.map.options.MTHaloStop
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HaloTests {
    @Test fun setHalo_ToJS_ReturnsValidJSString() {
        val halo =
            MTHalo(
                scale = 1.5,
                stops =
                    listOf(
                        MTHaloStop(0.2, "transparent"),
                        MTHaloStop(0.4, "#ff0000"),
                        MTHaloStop(1.0, "transparent"),
                    ),
            )
        val js = SetHalo(halo).toJS()
        assertEquals(
            "${MTBridge.MAP_OBJECT}.setHalo({\"scale\":1.5,\"stops\":[[0.2,\"transparent\"],[0.4,\"#ff0000\"],[1.0,\"transparent\"]]});",
            js,
        )
    }

    @Test fun mapOptions_Serializes_HaloField_ConfigAndEnabled() {
        val options = MTMapOptions(halo = MTHalo(2.0, listOf(MTHaloStop(0.0, "rgba(1,2,3,1)"))))
        val json = JsonConfig.json.encodeToString(options)
        assertTrue(json.contains("\"halo\""))
        assertTrue(json.contains("\"scale\":2.0"))
        assertTrue(json.contains("\"stops\""))

        val optionsEnabled = MTMapOptions()
        optionsEnabled.enableHalo()
        val jsonEnabled = JsonConfig.json.encodeToString(optionsEnabled)
        assertTrue(jsonEnabled.contains("\"halo\":true"))
    }

    @Test fun mapOptions_Serializes_HaloEnabled_ByOption() {
        val options = MTMapOptions(halo = MTHaloOption.Enabled)
        val json = JsonConfig.json.encodeToString(options)
        assertTrue(json.contains("\"halo\":true"))
    }

    @Test fun disableHaloAnimations_ToJS_ReturnsValidJSString() {
        val js = DisableHaloAnimations().toJS()
        assertEquals("${MTBridge.MAP_OBJECT}.disableHaloAnimations();", js)
    }
}
