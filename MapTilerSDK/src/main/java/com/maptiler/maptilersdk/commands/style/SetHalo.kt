/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.options.MTHalo
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Sets the globe halo (atmospheric glow) configuration.
 * Unspecified fields keep their previous values.
 */
internal data class SetHalo(
    val halo: MTHalo,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val content = mutableMapOf<String, kotlinx.serialization.json.JsonElement>()
        halo.scale?.let { content["scale"] = JsonPrimitive(it) }
        halo.stops?.let { stopsList ->
            val jsonStops =
                JsonArray(
                    stopsList.map { stop ->
                        JsonArray(listOf(JsonPrimitive(stop.position), JsonPrimitive(stop.color)))
                    },
                )
            content["stops"] = jsonStops
        }
        val json = JsonConfig.json.encodeToString(JsonObject(content))
        return "${MTBridge.MAP_OBJECT}.setHalo($json);"
    }
}

/** Enables the halo with the default gradient. */
internal class EnableHalo : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String = "${MTBridge.MAP_OBJECT}.setHalo(true);"
}

/** Disables the halo (animates out). */
internal class DisableHalo : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String = "${MTBridge.MAP_OBJECT}.setHalo(false);"
}

/** Disables halo animations (state transitions). */
internal class DisableHaloAnimations : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String = "${MTBridge.MAP_OBJECT}.disableHaloAnimations();"
}

/** Disables space animations (state transitions). */
internal class DisableSpaceAnimations : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String = "${MTBridge.MAP_OBJECT}.disableSpaceAnimations();"
}
