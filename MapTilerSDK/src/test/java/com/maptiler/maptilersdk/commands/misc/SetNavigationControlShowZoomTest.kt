/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.misc

import org.junit.Assert.assertEquals
import org.junit.Test

class SetNavigationControlShowZoomTest {
    @Test
    fun testToJS() {
        val commandTrue = SetNavigationControlShowZoom(true)
        val commandFalse = SetNavigationControlShowZoom(false)

        assertEquals("map.navigationControl.showZoom = true;", commandTrue.toJS())
        assertEquals("map.navigationControl.showZoom = false;", commandFalse.toJS())
    }
}
