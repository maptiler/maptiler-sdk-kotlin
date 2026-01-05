/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.colorramp

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTBridgeReturnType
import com.maptiler.maptilersdk.commands.colorramp.CloneColorRamp
import com.maptiler.maptilersdk.commands.colorramp.GetColorRampBounds
import com.maptiler.maptilersdk.commands.colorramp.GetColorRampCanvasStrip
import com.maptiler.maptilersdk.commands.colorramp.GetColorRampColor
import com.maptiler.maptilersdk.commands.colorramp.GetColorRampColorHex
import com.maptiler.maptilersdk.commands.colorramp.GetColorRampColorRelative
import com.maptiler.maptilersdk.commands.colorramp.GetColorRampRawStops
import com.maptiler.maptilersdk.commands.colorramp.HasTransparentStart
import com.maptiler.maptilersdk.commands.colorramp.ResampleColorRamp
import com.maptiler.maptilersdk.commands.colorramp.ReverseColorRamp
import com.maptiler.maptilersdk.commands.colorramp.ScaleColorRamp
import com.maptiler.maptilersdk.commands.colorramp.ScaleColorRampInPlace
import com.maptiler.maptilersdk.commands.colorramp.SetColorRampStops
import com.maptiler.maptilersdk.commands.colorramp.SetColorRampStopsInPlace
import com.maptiler.maptilersdk.commands.colorramp.TransparentStartColorRamp
import com.maptiler.maptilersdk.helpers.JsonConfig
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonUnquotedLiteral
import java.util.UUID

/**
 * Bounds of a color ramp interval.
 */
data class MTColorRampBounds(
    val min: Double,
    val max: Double,
)

/**
 * A stop in a color ramp, pairing a value with an RGB[A] color.
 */
@Serializable
data class MTColorStop(
    val value: Double,
    val color: List<Int>,
) {
    init {
        require(color.size == 3 || color.size == 4) { "Color stop must contain 3 or 4 channels." }
        require(color.all { it in 0..255 }) { "Color channel values must be between 0 and 255." }
    }
}

/**
 * Array definition stop, encoded as [value, [r, g, b, a]].
 */
@Serializable(with = MTArrayColorRampStopSerializer::class)
data class MTArrayColorRampStop(
    val value: Double,
    val color: List<Int>,
) {
    init {
        require(color.size == 4) { "Array color ramp stops must provide RGBA values." }
        require(color.all { it in 0..255 }) { "Color channel values must be between 0 and 255." }
    }
}

internal object MTArrayColorRampStopSerializer : KSerializer<MTArrayColorRampStop> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("MTArrayColorRampStop", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: MTArrayColorRampStop,
    ) {
        val jsonEncoder =
            encoder as? JsonEncoder
                ?: error("MTArrayColorRampStopSerializer only supports JSON encoding")
        val array =
            JsonArray(
                listOf(
                    JsonPrimitive(value.value),
                    JsonArray(value.color.map { JsonPrimitive(it) }),
                ),
            )
        jsonEncoder.encodeJsonElement(array)
    }

    override fun deserialize(decoder: Decoder): MTArrayColorRampStop {
        throw UnsupportedOperationException("Deserialization of MTArrayColorRampStop is not supported")
    }
}

/**
 * Options to build a color ramp.
 */
@Serializable
data class MTColorRampOptions(
    val min: Double? = null,
    val max: Double? = null,
    val stops: List<MTColorStop>? = null,
)

/**
 * Options controlling cloning behavior for mutating operations.
 */
@Serializable
data class MTColorRampCloneOptions(
    val clone: Boolean? = null,
)

/**
 * Options for color interpolation.
 */
@Serializable
data class MTColorRampGetColorOptions(
    val smooth: Boolean? = null,
)

/**
 * Canvas strip rendering options.
 */
@Serializable
data class MTColorRampCanvasOptions(
    val horizontal: Boolean? = null,
    val size: Int? = null,
    val smooth: Boolean? = null,
)

/**
 * Resampling algorithms.
 */
enum class MTColorRampResamplingMethod(
    internal val jsName: String,
) {
    @SerialName("ease-in-square")
    EASE_IN_SQUARE("ease-in-square"),

    @SerialName("ease-out-square")
    EASE_OUT_SQUARE("ease-out-square"),

    @SerialName("ease-in-sqrt")
    EASE_IN_SQRT("ease-in-sqrt"),

    @SerialName("ease-out-sqrt")
    EASE_OUT_SQRT("ease-out-sqrt"),

    @SerialName("ease-in-exp")
    EASE_IN_EXP("ease-in-exp"),

    @SerialName("ease-out-exp")
    EASE_OUT_EXP("ease-out-exp"),
}

/**
 * Reference to a color ramp instance.
 */
@Serializable(with = MTColorRampSerializer::class)
class MTColorRamp internal constructor(
    internal val identifier: String,
    private val bridge: MTBridge,
) {
    companion object {
        internal fun newIdentifier(): String = "colorRamp_${UUID.randomUUID().toString().replace("-", "")}"
    }

    /**
     * Clone this color ramp.
     */
    suspend fun clone(): MTColorRamp {
        val newId = newIdentifier()
        bridge.execute(CloneColorRamp(identifier, newId))
        return MTColorRamp(newId, bridge)
    }

    /**
     * Returns the bounds of this color ramp.
     */
    suspend fun getBounds(): MTColorRampBounds {
        val returnTypeValue = bridge.execute(GetColorRampBounds(identifier))
        return when (returnTypeValue) {
            is MTBridgeReturnType.StringDoubleDict ->
                MTColorRampBounds(
                    min = returnTypeValue.value["min"] ?: 0.0,
                    max = returnTypeValue.value["max"] ?: 1.0,
                )
            is MTBridgeReturnType.StringValue -> {
                val decoded =
                    runCatching { JsonConfig.json.decodeFromString<Map<String, Double>>(returnTypeValue.value) }
                        .getOrDefault(emptyMap())
                MTColorRampBounds(
                    min = decoded["min"] ?: 0.0,
                    max = decoded["max"] ?: 1.0,
                )
            }
            else -> MTColorRampBounds(min = 0.0, max = 1.0)
        }
    }

    /**
     * Returns a data URL of the rendered strip of this color ramp.
     */
    suspend fun getCanvasStrip(options: MTColorRampCanvasOptions? = null): String? {
        val sanitized =
            options?.let {
                val clampedSize = it.size?.coerceIn(1, 2048)
                MTColorRampCanvasOptions(
                    horizontal = it.horizontal,
                    size = clampedSize,
                    smooth = it.smooth,
                )
            }
        val returnTypeValue = bridge.execute(GetColorRampCanvasStrip(identifier, sanitized))
        return (returnTypeValue as? MTBridgeReturnType.StringValue)?.value?.trim('"')
    }

    /**
     * Returns the RGBA color for a given value.
     */
    suspend fun getColor(
        value: Double,
        options: MTColorRampGetColorOptions? = null,
    ): List<Int> {
        val returnTypeValue = bridge.execute(GetColorRampColor(identifier, value, options))
        val encoded = (returnTypeValue as? MTBridgeReturnType.StringValue)?.value ?: return emptyList()
        return runCatching { JsonConfig.json.decodeFromString<List<Int>>(encoded) }.getOrDefault(emptyList())
    }

    /**
     * Returns the hex color for a given value.
     */
    suspend fun getColorHex(
        value: Double,
        options: MTColorRampGetColorOptions? = null,
    ): String? {
        val returnTypeValue = bridge.execute(GetColorRampColorHex(identifier, value, options))
        return (returnTypeValue as? MTBridgeReturnType.StringValue)?.value?.trim('"')
    }

    /**
     * Returns the RGBA color for a relative value between 0 and 1.
     */
    suspend fun getColorRelative(
        value: Double,
        options: MTColorRampGetColorOptions? = null,
    ): List<Int> {
        val returnTypeValue = bridge.execute(GetColorRampColorRelative(identifier, value, options))
        val encoded = (returnTypeValue as? MTBridgeReturnType.StringValue)?.value ?: return emptyList()
        return runCatching { JsonConfig.json.decodeFromString<List<Int>>(encoded) }.getOrDefault(emptyList())
    }

    /**
     * Returns the raw color stops backing this ramp.
     */
    suspend fun getRawColorStops(): List<MTColorStop> {
        val returnTypeValue = bridge.execute(GetColorRampRawStops(identifier))
        val encoded = (returnTypeValue as? MTBridgeReturnType.StringValue)?.value ?: return emptyList()
        return runCatching { JsonConfig.json.decodeFromString<List<MTColorStop>>(encoded) }.getOrDefault(emptyList())
    }

    /**
     * Reverse the ramp. When clone is false, the current ramp is mutated and returned.
     */
    suspend fun reverse(options: MTColorRampCloneOptions? = null): MTColorRamp {
        if (options?.clone == false) {
            bridge.execute(ReverseColorRamp(identifier, null, options))
            return this
        }
        val newId = newIdentifier()
        bridge.execute(ReverseColorRamp(identifier, newId, options))
        return MTColorRamp(newId, bridge)
    }

    /**
     * Scale the ramp to a new interval. When clone is false, the current ramp is mutated and returned.
     */
    suspend fun scale(
        min: Double,
        max: Double,
        options: MTColorRampCloneOptions? = null,
    ): MTColorRamp {
        val clampedMax = if (max == min) min + 1.0 else max
        if (options?.clone == false) {
            bridge.execute(ScaleColorRampInPlace(identifier, min, clampedMax, options))
            return this
        }
        val newId = newIdentifier()
        bridge.execute(ScaleColorRamp(identifier, newId, min, clampedMax, options))
        return MTColorRamp(newId, bridge)
    }

    /**
     * Replace the stops of this ramp. When clone is false, the current ramp is mutated and returned.
     */
    suspend fun setStops(
        stops: List<MTColorStop>,
        options: MTColorRampCloneOptions? = null,
    ): MTColorRamp {
        require(stops.isNotEmpty()) { "At least one color stop is required." }
        if (options?.clone == false) {
            bridge.execute(SetColorRampStopsInPlace(identifier, stops, options))
            return this
        }
        val newId = newIdentifier()
        bridge.execute(SetColorRampStops(identifier, newId, stops, options))
        return MTColorRamp(newId, bridge)
    }

    /**
     * Resample the ramp using a non-linear method.
     */
    suspend fun resample(
        method: MTColorRampResamplingMethod,
        samples: Int = 15,
    ): MTColorRamp {
        val safeSamples = maxOf(2, samples)
        val newId = newIdentifier()
        bridge.execute(ResampleColorRamp(identifier, newId, method, safeSamples))
        return MTColorRamp(newId, bridge)
    }

    /**
     * Returns a ramp that starts fully transparent.
     */
    suspend fun transparentStart(): MTColorRamp {
        val newId = newIdentifier()
        bridge.execute(TransparentStartColorRamp(identifier, newId))
        return MTColorRamp(newId, bridge)
    }

    /**
     * Indicates if the ramp starts with transparency.
     */
    suspend fun hasTransparentStart(): Boolean {
        val returnTypeValue = bridge.execute(HasTransparentStart(identifier))
        return when (returnTypeValue) {
            is MTBridgeReturnType.BoolValue -> returnTypeValue.value
            is MTBridgeReturnType.StringValue -> returnTypeValue.value.trim('"').equals("true", ignoreCase = true)
            else -> false
        }
    }
}

internal object MTColorRampSerializer : KSerializer<MTColorRamp> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("MTColorRamp", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: MTColorRamp,
    ) {
        val jsonEncoder =
            encoder as? JsonEncoder
                ?: error("MTColorRampSerializer only supports JSON encoding")
        jsonEncoder.encodeJsonElement(JsonUnquotedLiteral(value.identifier))
    }

    override fun deserialize(decoder: Decoder): MTColorRamp {
        throw UnsupportedOperationException("Deserialization of MTColorRamp is not supported")
    }
}
