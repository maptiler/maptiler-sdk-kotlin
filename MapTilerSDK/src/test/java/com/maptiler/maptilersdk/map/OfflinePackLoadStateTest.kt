package com.maptiler.maptilersdk.map

import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.offline.MTBoundingBox
import com.maptiler.maptilersdk.offline.MTOfflineRegionDefinition
import com.maptiler.maptilersdk.offline.MTOfflineRegionGeometry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URL

class OfflinePackLoadStateTest {
    private val pendingPack =
        OfflinePackLoadState.PendingOfflinePack(
            referenceStyle = MTMapReferenceStyle.CUSTOM(URL("http://127.0.0.1:8080/offline/pack-id/style.json")),
            region =
                MTOfflineRegionDefinition(
                    geometry = MTOfflineRegionGeometry.BoundingBox(MTBoundingBox(8.52, 47.36, 8.56, 47.39)),
                    minZoom = 5,
                    maxZoom = 12,
                    referenceStyle = MTMapReferenceStyle.STREETS,
                ),
        )

    @Test
    fun packLoadedBeforeInitializationIsQueuedAndReturnedOnce() {
        val state = OfflinePackLoadState()

        assertTrue(state.loadPack(pendingPack))
        assertTrue(state.isPackLoaded)
        assertEquals(pendingPack, state.onMapInitialized())
        assertNull(state.onMapInitialized())
    }

    @Test
    fun packLoadedAfterInitializationIsNotQueued() {
        val state = OfflinePackLoadState()

        assertNull(state.onMapInitialized())
        assertFalse(state.loadPack(pendingPack))
        assertTrue(state.isPackLoaded)
        assertNull(state.onMapInitialized())
    }

    @Test
    fun unloadClearsPendingPack() {
        val state = OfflinePackLoadState()

        state.loadPack(pendingPack)
        state.unloadPack()

        assertFalse(state.isPackLoaded)
        assertNull(state.onMapInitialized())
    }
}
