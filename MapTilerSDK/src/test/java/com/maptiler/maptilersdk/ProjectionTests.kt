/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.commands.style.IsGlobeProjectionEnabled
import junit.framework.TestCase.assertEquals
import org.junit.Test

class ProjectionTests {
    @Test fun isGlobeProjectionEnabledToJS_ReturnsCorrectJSString() {
        val command = IsGlobeProjectionEnabled()
        assertEquals("${MTBridge.MAP_OBJECT}.isGlobeProjection();", command.toJS())
    }
}

