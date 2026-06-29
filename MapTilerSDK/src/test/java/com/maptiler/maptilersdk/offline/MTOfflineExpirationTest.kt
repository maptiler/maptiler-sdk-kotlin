/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class MTOfflineExpirationTest {
    private val region =
        MTOfflineRegionDefinition(
            bbox = MTBoundingBox(0.0, 0.0, 10.0, 10.0),
            minZoom = 0,
            maxZoom = 10,
            referenceStyle = MTMapReferenceStyle.STREETS,
        )

    @Test
    fun testIsExpired() {
        val now = Instant.now()

        // Future expiration
        val futureMetadata =
            MTOfflinePackMetadata(
                id = "test-future",
                state = MTOfflinePackState.COMPLETED,
                size = 0,
                createdAt = now,
                expiresAt = now.plusSeconds(3600),
                region = region,
                totalResources = 0,
                totalTileResources = 0,
                downloadedResources = 0,
            )
        assertFalse(futureMetadata.isExpired)

        // Past expiration
        val pastMetadata =
            MTOfflinePackMetadata(
                id = "test-past",
                state = MTOfflinePackState.COMPLETED,
                size = 0,
                createdAt = now.minusSeconds(7200),
                expiresAt = now.minusSeconds(3600),
                region = region,
                totalResources = 0,
                totalTileResources = 0,
                downloadedResources = 0,
            )
        assertTrue(pastMetadata.isExpired)
    }

    @Test
    fun testIsPastGracePeriod() {
        val now = Instant.now()
        val gracePeriod = MTOfflineConfiguration.DEFAULT_GRACE_PERIOD

        // Just expired, within grace period
        val withinGraceMetadata =
            MTOfflinePackMetadata(
                id = "test-within-grace",
                state = MTOfflinePackState.EXPIRED,
                size = 0,
                createdAt = now.minusMillis(gracePeriod + 10000),
                expiresAt = now.minusMillis(gracePeriod / 2),
                region = region,
                totalResources = 0,
                totalTileResources = 0,
                downloadedResources = 0,
            )
        assertTrue(withinGraceMetadata.isExpired)
        assertFalse(withinGraceMetadata.isPastGracePeriod)

        // Past grace period
        val pastGraceMetadata =
            MTOfflinePackMetadata(
                id = "test-past-grace",
                state = MTOfflinePackState.EXPIRED,
                size = 0,
                createdAt = now.minusMillis(gracePeriod * 2),
                expiresAt = now.minusMillis(gracePeriod + 1000),
                region = region,
                totalResources = 0,
                totalTileResources = 0,
                downloadedResources = 0,
            )
        assertTrue(pastGraceMetadata.isExpired)
        assertTrue(pastGraceMetadata.isPastGracePeriod)
    }
}
