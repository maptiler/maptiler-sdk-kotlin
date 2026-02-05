/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import com.maptiler.maptilersdk.helpers.MTDeviceProfile.Tier
import com.maptiler.maptilersdk.map.MTMapOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MTDeviceProfileTest {
    @Test
    fun `applyLeanDefaultsIfUnset sets only missing fields for LOW`() {
        val base = MTMapOptions()
        val merged = MTDeviceProfile.applyLeanDefaultsIfUnset(base, Tier.LOW)

        assertTrue(merged.cancelPendingTileRequestsWhileZooming == true)
        assertFalse(merged.crossSourceCollisionsAreEnabled ?: true)
        assertFalse(merged.shouldRefreshExpiredTiles ?: true)
        assertEquals(4.0, merged.maxTileCacheZoomLevels, 0.0)
        assertEquals(1.0, merged.pixelRatio, 0.0)
    }

    @Test
    fun `applyLeanDefaultsIfUnset does not override developer values`() {
        val base =
            MTMapOptions(
                pixelRatio = 2.0,
                shouldRefreshExpiredTiles = true,
                cancelPendingTileRequestsWhileZooming = false,
                maxTileCacheZoomLevels = 7.0,
                crossSourceCollisionsAreEnabled = true,
            )

        val merged = MTDeviceProfile.applyLeanDefaultsIfUnset(base, Tier.MID)

        assertEquals(2.0, merged.pixelRatio, 0.0)
        assertTrue(merged.shouldRefreshExpiredTiles == true)
        assertFalse(merged.cancelPendingTileRequestsWhileZooming ?: true)
        assertEquals(7.0, merged.maxTileCacheZoomLevels, 0.0)
        assertTrue(merged.crossSourceCollisionsAreEnabled == true)
    }
}
