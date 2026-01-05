/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.colorramp

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.colorramp.MTArrayColorRampStop
import com.maptiler.maptilersdk.colorramp.MTColorRampCanvasOptions
import com.maptiler.maptilersdk.colorramp.MTColorRampCloneOptions
import com.maptiler.maptilersdk.colorramp.MTColorRampGetColorOptions
import com.maptiler.maptilersdk.colorramp.MTColorRampOptions
import com.maptiler.maptilersdk.colorramp.MTColorRampResamplingMethod
import com.maptiler.maptilersdk.colorramp.MTColorStop
import com.maptiler.maptilersdk.helpers.JsonConfig
import kotlinx.serialization.builtins.ListSerializer

internal data class CreateColorRamp(
    val identifier: String,
    val options: MTColorRampOptions,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val optionsString = JsonConfig.json.encodeToString(MTColorRampOptions.serializer(), options)
        return "const $identifier = new ${MTBridge.SDK_OBJECT}.ColorRamp($optionsString);"
    }
}

internal data class CreateColorRampFromArrayDefinition(
    val identifier: String,
    val stops: List<MTArrayColorRampStop>,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val stopsString = JsonConfig.json.encodeToString(ListSerializer(MTArrayColorRampStop.serializer()), stops)
        return "const $identifier = ${MTBridge.SDK_OBJECT}.ColorRamp.fromArrayDefinition($stopsString);"
    }
}

internal data class CreateColorRampFromCollection(
    val identifier: String,
    val collectionName: String,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String = "const $identifier = ${MTBridge.SDK_OBJECT}.ColorRampCollection.$collectionName.clone();"
}

internal data class CloneColorRamp(
    val identifier: String,
    val newIdentifier: String,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String = "const $newIdentifier = $identifier.clone();"
}

internal data class ScaleColorRamp(
    val identifier: String,
    val newIdentifier: String,
    val min: Double,
    val max: Double,
    val options: MTColorRampCloneOptions?,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val args =
            buildString {
                append(min)
                append(", ")
                append(max)
                options?.let {
                    append(", ")
                    append(JsonConfig.json.encodeToString(MTColorRampCloneOptions.serializer(), it))
                }
            }
        return "const $newIdentifier = $identifier.scale($args);"
    }
}

internal data class ScaleColorRampInPlace(
    val identifier: String,
    val min: Double,
    val max: Double,
    val options: MTColorRampCloneOptions?,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val args =
            buildString {
                append(min)
                append(", ")
                append(max)
                options?.let {
                    append(", ")
                    append(JsonConfig.json.encodeToString(MTColorRampCloneOptions.serializer(), it))
                }
            }
        return "$identifier.scale($args);"
    }
}

internal data class ReverseColorRamp(
    val identifier: String,
    val newIdentifier: String?,
    val options: MTColorRampCloneOptions?,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val args =
            options?.let { JsonConfig.json.encodeToString(MTColorRampCloneOptions.serializer(), it) }
        val invocation =
            if (args != null) {
                "$identifier.reverse($args)"
            } else {
                "$identifier.reverse()"
            }
        return newIdentifier?.let { "const $it = $invocation;" } ?: "$invocation;"
    }
}

internal data class SetColorRampStops(
    val identifier: String,
    val newIdentifier: String,
    val stops: List<MTColorStop>,
    val options: MTColorRampCloneOptions?,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val stopsString = JsonConfig.json.encodeToString(ListSerializer(MTColorStop.serializer()), stops)
        val args =
            buildString {
                append(stopsString)
                options?.let {
                    append(", ")
                    append(JsonConfig.json.encodeToString(MTColorRampCloneOptions.serializer(), it))
                }
            }
        return "const $newIdentifier = $identifier.setStops($args);"
    }
}

internal data class SetColorRampStopsInPlace(
    val identifier: String,
    val stops: List<MTColorStop>,
    val options: MTColorRampCloneOptions?,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val stopsString = JsonConfig.json.encodeToString(ListSerializer(MTColorStop.serializer()), stops)
        val args =
            buildString {
                append(stopsString)
                options?.let {
                    append(", ")
                    append(JsonConfig.json.encodeToString(MTColorRampCloneOptions.serializer(), it))
                }
            }
        return "$identifier.setStops($args);"
    }
}

internal data class ResampleColorRamp(
    val identifier: String,
    val newIdentifier: String,
    val method: MTColorRampResamplingMethod,
    val samples: Int,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String = "const $newIdentifier = $identifier.resample('${method.jsName}', $samples);"
}

internal data class TransparentStartColorRamp(
    val identifier: String,
    val newIdentifier: String,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String = "const $newIdentifier = $identifier.transparentStart();"
}

internal data class HasTransparentStart(
    val identifier: String,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String = "$identifier.hasTransparentStart();"
}

internal data class GetColorRampBounds(
    val identifier: String,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String = "JSON.stringify($identifier.getBounds());"
}

internal data class GetColorRampColor(
    val identifier: String,
    val value: Double,
    val options: MTColorRampGetColorOptions?,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String {
        val optionsString =
            options?.let { JsonConfig.json.encodeToString(MTColorRampGetColorOptions.serializer(), it) }
        val args = optionsString?.let { "$value, $it" } ?: value.toString()
        return "JSON.stringify($identifier.getColor($args));"
    }
}

internal data class GetColorRampColorHex(
    val identifier: String,
    val value: Double,
    val options: MTColorRampGetColorOptions?,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String {
        val optionsString =
            options?.let { JsonConfig.json.encodeToString(MTColorRampGetColorOptions.serializer(), it) }
        val args = optionsString?.let { "$value, $it" } ?: value.toString()
        return "$identifier.getColorHex($args);"
    }
}

internal data class GetColorRampColorRelative(
    val identifier: String,
    val value: Double,
    val options: MTColorRampGetColorOptions?,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String {
        val optionsString =
            options?.let { JsonConfig.json.encodeToString(MTColorRampGetColorOptions.serializer(), it) }
        val args = optionsString?.let { "$value, $it" } ?: value.toString()
        return "JSON.stringify($identifier.getColorRelative($args));"
    }
}

internal data class GetColorRampRawStops(
    val identifier: String,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String = "JSON.stringify($identifier.getRawColorStops());"
}

internal data class GetColorRampCanvasStrip(
    val identifier: String,
    val options: MTColorRampCanvasOptions?,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String {
        val optionsString =
            options?.let { JsonConfig.json.encodeToString(MTColorRampCanvasOptions.serializer(), it) }
        val args = optionsString?.let { "$optionsString" } ?: ""
        val optionsArg = if (args.isBlank()) "" else args
        return "$identifier.getCanvasStrip($optionsArg).toDataURL();"
    }
}
