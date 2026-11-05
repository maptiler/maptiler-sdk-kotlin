/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.style.dsl.PropertyValue
import kotlinx.serialization.encodeToString

/**
 * Style property group a layer property belongs to.
 */
internal enum class LayerPropertyKind(
    val jsGetter: String,
    val jsSetter: String,
) {
    LAYOUT("getLayoutProperty", "setLayoutProperty"),
    PAINT("getPaintProperty", "setPaintProperty"),
}

/**
 * Returns the raw value of a layout or paint property, or `null` if the layer is missing or the property is unset.
 */
internal data class GetLayerProperty(
    val layerId: String,
    val name: String,
    val kind: LayerPropertyKind,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String {
        val id = JsonConfig.json.encodeToString(layerId)
        val property = JsonConfig.json.encodeToString(name)

        return "(function() {" +
            " var m = ${MTBridge.MAP_OBJECT};" +
            " if (!m.getLayer($id)) { return null; }" +
            " var v = m.${kind.jsGetter}($id, $property);" +
            " return v === undefined ? null : v;" +
            " })();"
    }
}

/**
 * Sets the same layout or paint property on many layers in a single bridge call. Missing layers are skipped.
 */
internal data class SetPropertyForLayers(
    val layerIds: List<String>,
    val name: String,
    val value: PropertyValue,
    val kind: LayerPropertyKind,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val ids = JsonConfig.json.encodeToString(layerIds)
        val property = JsonConfig.json.encodeToString(name)

        return "(function(ids, value) {" +
            " var m = ${MTBridge.MAP_OBJECT};" +
            " ids.forEach(function(id) { if (m.getLayer(id)) { m.${kind.jsSetter}(id, $property, value); } });" +
            " })($ids, ${value.asCode()});"
    }
}
