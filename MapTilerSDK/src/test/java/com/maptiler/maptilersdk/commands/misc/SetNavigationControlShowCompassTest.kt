/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.misc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SetNavigationControlShowCompassTest {
    @Test
    fun `toJS matches signature`() {
        val commandTrue = SetNavigationControlShowCompass(true)
        val commandFalse = SetNavigationControlShowCompass(false)

        assertEquals("map.navigationControl.showCompass = true;", commandTrue.toJS())
        assertEquals("map.navigationControl.showCompass = false;", commandFalse.toJS())
        assertFalse(commandTrue.isPrimitiveReturnType)
    }
}
