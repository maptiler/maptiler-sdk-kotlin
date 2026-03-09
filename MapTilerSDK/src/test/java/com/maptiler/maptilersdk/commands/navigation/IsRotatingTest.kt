/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IsRotatingTest {
    @Test
    fun `isRotating command emits expected invocation`() {
        val command = IsRotating()

        assertEquals("map.isRotating();", command.toJS())
        assertTrue(command.isPrimitiveReturnType)
    }
}
