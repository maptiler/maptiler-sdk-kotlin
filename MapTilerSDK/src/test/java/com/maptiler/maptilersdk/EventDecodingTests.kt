/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk

import com.maptiler.maptilersdk.events.MTEvent
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class EventDecodingTests {
    @Test fun dblclick_DecodesTo_ON_DOUBLE_TAP() {
        val event: MTEvent = Json.decodeFromString("\"dblclick\"")
        assertEquals(MTEvent.ON_DOUBLE_TAP, event)
    }
}
