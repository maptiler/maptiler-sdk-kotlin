/**
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 *
 * @author sasaprodribaba
 * Created 30. 5. 2025. at 11:42
 */

package com.maptiler.maptilersdk

import android.graphics.Color
import com.maptiler.maptilersdk.helpers.toHexString
import org.junit.Assert.assertTrue
import org.junit.Test

class MapTilerSDKTests {
    @Test fun colorToHexString_ReturnsCorrectString() {
        val color = Color.WHITE
        val hex = color.toHexString()

        assertTrue(hex, hex == "#FFFFFF")
    }
}
