/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

/**
 * Returns a lightweight snapshot of every layer in the current style, in rendering order.
 *
 * Uses `getLayersOrder()` + `getLayer()` instead of `getStyle()` to avoid serializing sources and expressions.
 */
internal object GetStyleLayers : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String =
        "(function() {" +
            " var m = ${MTBridge.MAP_OBJECT};" +
            " if (!m.getLayersOrder) { return []; }" +
            " return m.getLayersOrder().map(function(id) {" +
            " var l = m.getLayer(id);" +
            " if (!l) { return null; }" +
            " return {" +
            " id: l.id," +
            " type: l.type," +
            " source: l.source || null," +
            " sourceLayer: l.sourceLayer || null," +
            " visibility: m.getLayoutProperty(id, 'visibility') || 'visible'," +
            " minzoom: (typeof l.minzoom === 'number') ? l.minzoom : null," +
            " maxzoom: (typeof l.maxzoom === 'number') ? l.maxzoom : null" +
            " };" +
            " }).filter(function(l) { return l !== null; });" +
            " })();"
}
