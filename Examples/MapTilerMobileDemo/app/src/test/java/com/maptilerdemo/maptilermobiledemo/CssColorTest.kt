/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptilerdemo.maptilermobiledemo

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CssColorTest {
    @Test fun parsesHex() {
        assertEquals(Color(0xFFAAD3DF), parseCssColor("#aad3df"))
        assertEquals(Color(0xFFFFFFFF), parseCssColor("#fff"))
        assertEquals(Color(0x80112233), parseCssColor("#11223380"))
    }

    @Test fun parsesRgb() {
        assertEquals(Color(1f / 255f, 2f / 255f, 3f / 255f, 0.5f), parseCssColor("rgba(1, 2, 3, 0.5)"))
    }

    @Test fun parsesHsl() {
        assertEquals(Color.hsl(0f, 1f, 0.5f), parseCssColor("hsl(0, 100%, 50%)"))
        assertEquals(Color.hsl(204f, 0.92f, 0.75f, 0.45f), parseCssColor("hsla(204, 92%, 75%, 0.45)"))
    }

    @Test fun rejectsInvalid() {
        assertNull(parseCssColor(null))
        assertNull(parseCssColor("linear"))
        assertNull(parseCssColor("#12345"))
    }
}
