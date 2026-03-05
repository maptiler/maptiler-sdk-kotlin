/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.navigation

import com.maptiler.maptilersdk.map.options.MTPaddingOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PaddingCommandsTest {
    @Test
    fun getPaddingToJS() {
        val command = GetPadding()

        val expectedJsString = "map.getPadding();"
        assertEquals(expectedJsString, command.toJS())
        assertTrue(command.isPrimitiveReturnType)
    }

    @Test
    fun setPaddingToJS() {
        val command = SetPadding(MTPaddingOptions(left = 1.0, top = 2.0, right = 3.0, bottom = 4.0))

        val expectedJsString = "map.setPadding({\"left\":1.0,\"top\":2.0,\"right\":3.0,\"bottom\":4.0});"
        assertEquals(expectedJsString, command.toJS())
    }
}
