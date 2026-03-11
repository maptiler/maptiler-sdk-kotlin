/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.navigation

import com.maptiler.maptilersdk.map.options.MTAnimationOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class RotateToTest {
    @Test
    fun `rotateTo command emits expected invocation without options`() {
        val command = RotateTo(bearing = 90.0)

        assertEquals("map.rotateTo(90.0);", command.toJS())
        assertFalse(command.isPrimitiveReturnType)
    }

    @Test
    fun `rotateTo command emits expected invocation with options`() {
        val options = MTAnimationOptions(duration = 1000.0)
        val command = RotateTo(bearing = 180.0, options = options)

        assertEquals("map.rotateTo(180.0, {\"duration\":1000.0});", command.toJS())
        assertFalse(command.isPrimitiveReturnType)
    }
}
