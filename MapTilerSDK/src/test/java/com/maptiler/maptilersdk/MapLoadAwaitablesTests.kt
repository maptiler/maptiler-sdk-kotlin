/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.commands.misc.MapOnLoadAsync
import com.maptiler.maptilersdk.commands.misc.MapOnLoadWithTerrainAsync
import com.maptiler.maptilersdk.commands.misc.MapOnReadyAsync
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class MapLoadAwaitablesTests {
    @Test fun mapOnLoadAsync_HasCorrectPropertiesAndToJS() {
        val command = MapOnLoadAsync()
        assertTrue(command.isPromiseReturnType)
        assertEquals(false, command.isPrimitiveReturnType)
        assertEquals("${MTBridge.MAP_OBJECT}.onLoadAsync();", command.toJS())
    }

    @Test fun mapOnReadyAsync_HasCorrectPropertiesAndToJS() {
        val command = MapOnReadyAsync()
        assertTrue(command.isPromiseReturnType)
        assertEquals(false, command.isPrimitiveReturnType)
        assertEquals("${MTBridge.MAP_OBJECT}.onReadyAsync();", command.toJS())
    }

    @Test fun mapOnLoadWithTerrainAsync_HasCorrectPropertiesAndToJS() {
        val command = MapOnLoadWithTerrainAsync()
        assertTrue(command.isPromiseReturnType)
        assertEquals(false, command.isPrimitiveReturnType)
        assertEquals("${MTBridge.MAP_OBJECT}.onLoadWithTerrainAsync();", command.toJS())
    }
}
