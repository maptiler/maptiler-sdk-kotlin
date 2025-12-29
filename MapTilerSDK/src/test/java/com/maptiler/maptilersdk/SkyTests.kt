/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.commands.style.SetSky
import com.maptiler.maptilersdk.map.options.MTSky
import com.maptiler.maptilersdk.map.style.dsl.PropertyValue
import com.maptiler.maptilersdk.map.style.dsl.StyleValue
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SkyTests {
    @Test fun setSky_AllFields_ToJS_MatchesMapSignature() {
        val sky =
            MTSky(
                skyColor = StyleValue.Color(0x199EF3),
                skyHorizonBlend = StyleValue.Number(0.5),
                horizonColor = StyleValue.Str("#ffffff"),
                horizonFogBlend = StyleValue.Number(0.5),
                fogColor = StyleValue.Str("#0000ff"),
                fogGroundBlend = StyleValue.Number(0.5),
                atmosphereBlend =
                    StyleValue.Expression(
                        PropertyValue.array(
                            PropertyValue.Str("interpolate"),
                            PropertyValue.array(PropertyValue.Str("linear")),
                            PropertyValue.array(PropertyValue.Str("zoom")),
                            PropertyValue.Num(0.0),
                            PropertyValue.Num(1.0),
                            PropertyValue.Num(10.0),
                            PropertyValue.Num(1.0),
                            PropertyValue.Num(12.0),
                            PropertyValue.Num(0.0),
                        ),
                    ),
            )
        val js = SetSky(sky).toJS()
        assertEquals(
            "${MTBridge.MAP_OBJECT}.setSky({\"sky-color\":\"#199EF3\",\"sky-horizon-blend\":0.5," +
                "\"horizon-color\":\"#ffffff\",\"horizon-fog-blend\":0.5,\"fog-color\":\"#0000ff\"," +
                "\"fog-ground-blend\":0.5,\"atmosphere-blend\":[\"interpolate\",[\"linear\"],[\"zoom\"]," +
                "0.0,1.0,10.0,1.0,12.0,0.0]});",
            js,
        )
    }

    @Test fun setSky_BlendsAreClamped_ToRange() {
        val sky =
            MTSky(
                skyHorizonBlend = StyleValue.Number(-1.0),
                horizonFogBlend = StyleValue.Number(2.0),
                fogGroundBlend = StyleValue.Number(Double.NaN),
                atmosphereBlend = StyleValue.Number(5.0),
            )
        val js = SetSky(sky).toJS()
        assertTrue(js.contains("\"sky-horizon-blend\":0.0"))
        assertTrue(js.contains("\"horizon-fog-blend\":1.0"))
        assertTrue(js.contains("\"fog-ground-blend\":0.0"))
        assertTrue(js.contains("\"atmosphere-blend\":1.0"))
    }
}
