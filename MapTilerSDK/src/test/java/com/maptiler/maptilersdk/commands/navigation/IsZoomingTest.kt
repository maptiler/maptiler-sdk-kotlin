/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IsZoomingTest {
    @Test
    fun `isZooming command emits expected invocation`() {
        val command = IsZooming()

        assertEquals("map.isZooming();", command.toJS())
        assertTrue(command.isPrimitiveReturnType)
    }
}
