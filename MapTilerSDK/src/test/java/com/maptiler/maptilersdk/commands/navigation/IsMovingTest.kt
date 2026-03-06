/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IsMovingTest {
    @Test
    fun `isMoving command emits expected invocation`() {
        val command = IsMoving()

        assertEquals("map.isMoving();", command.toJS())
        assertTrue(command.isPrimitiveReturnType)
    }
}
