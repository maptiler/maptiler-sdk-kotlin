/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.colorramp

import com.maptiler.maptilersdk.map.workers.stylable.StylableWorker

/**
 * Built-in color ramps available from the JS SDK.
 */
enum class MTBuiltinColorRamp(
    internal val jsName: String,
) {
    NULL("NULL"),
    GRAY("GRAY"),
    JET("JET"),
    HSV("HSV"),
    HOT("HOT"),
    SPRING("SPRING"),
    SUMMER("SUMMER"),
    AUTOMN("AUTOMN"),
    WINTER("WINTER"),
    BONE("BONE"),
    COPPER("COPPER"),
    GREYS("GREYS"),
    YIGNBU("YIGNBU"),
    GREENS("GREENS"),
    YIORRD("YIORRD"),
    BLUERED("BLUERED"),
    RDBU("RDBU"),
    PICNIC("PICNIC"),
    RAINBOW("RAINBOW"),
    PORTLAND("PORTLAND"),
    BLACKBODY("BLACKBODY"),
    EARTH("EARTH"),
    ELECTRIC("ELECTRIC"),
    VIRIDIS("VIRIDIS"),
    INFERNO("INFERNO"),
    MAGMA("MAGMA"),
    PLASMA("PLASMA"),
    WARM("WARM"),
    COOL("COOL"),
    RAINBOW_SOFT("RAINBOW_SOFT"),
    BATHYMETRY("BATHYMETRY"),
    CDOM("CDOM"),
    CHLOROPHYLL("CHLOROPHYLL"),
    DENSITY("DENSITY"),
    FREESURFACE_BLUE("FREESURFACE_BLUE"),
    FREESURFACE_RED("FREESURFACE_RED"),
    OXYGEN("OXYGEN"),
    PAR("PAR"),
    PHASE("PHASE"),
    SALINITY("SALINITY"),
    TEMPERATURE("TEMPERATURE"),
    TURBIDITY("TURBIDITY"),
    VELOCITY_BLUE("VELOCITY_BLUE"),
    VELOCITY_GREEN("VELOCITY_GREEN"),
    CUBEHELIX("CUBEHELIX"),
    CIVIDIS("CIVIDIS"),
    TURBO("TURBO"),
    ROCKET("ROCKET"),
    MAKO("MAKO"),
}

/**
 * Entry point to access the built-in color ramp collection.
 */
class MTColorRampCollection internal constructor(
    private val worker: StylableWorker,
) {
    private suspend fun create(type: MTBuiltinColorRamp): MTColorRamp = worker.colorRampFromCollection(type)

    suspend fun nullRamp(): MTColorRamp = create(MTBuiltinColorRamp.NULL)

    suspend fun gray(): MTColorRamp = create(MTBuiltinColorRamp.GRAY)

    suspend fun jet(): MTColorRamp = create(MTBuiltinColorRamp.JET)

    suspend fun hsv(): MTColorRamp = create(MTBuiltinColorRamp.HSV)

    suspend fun hot(): MTColorRamp = create(MTBuiltinColorRamp.HOT)

    suspend fun spring(): MTColorRamp = create(MTBuiltinColorRamp.SPRING)

    suspend fun summer(): MTColorRamp = create(MTBuiltinColorRamp.SUMMER)

    suspend fun automn(): MTColorRamp = create(MTBuiltinColorRamp.AUTOMN)

    suspend fun winter(): MTColorRamp = create(MTBuiltinColorRamp.WINTER)

    suspend fun bone(): MTColorRamp = create(MTBuiltinColorRamp.BONE)

    suspend fun copper(): MTColorRamp = create(MTBuiltinColorRamp.COPPER)

    suspend fun greys(): MTColorRamp = create(MTBuiltinColorRamp.GREYS)

    suspend fun yignbu(): MTColorRamp = create(MTBuiltinColorRamp.YIGNBU)

    suspend fun greens(): MTColorRamp = create(MTBuiltinColorRamp.GREENS)

    suspend fun yiorrd(): MTColorRamp = create(MTBuiltinColorRamp.YIORRD)

    suspend fun bluered(): MTColorRamp = create(MTBuiltinColorRamp.BLUERED)

    suspend fun rdbu(): MTColorRamp = create(MTBuiltinColorRamp.RDBU)

    suspend fun picnic(): MTColorRamp = create(MTBuiltinColorRamp.PICNIC)

    suspend fun rainbow(): MTColorRamp = create(MTBuiltinColorRamp.RAINBOW)

    suspend fun portland(): MTColorRamp = create(MTBuiltinColorRamp.PORTLAND)

    suspend fun blackbody(): MTColorRamp = create(MTBuiltinColorRamp.BLACKBODY)

    suspend fun earth(): MTColorRamp = create(MTBuiltinColorRamp.EARTH)

    suspend fun electric(): MTColorRamp = create(MTBuiltinColorRamp.ELECTRIC)

    suspend fun viridis(): MTColorRamp = create(MTBuiltinColorRamp.VIRIDIS)

    suspend fun inferno(): MTColorRamp = create(MTBuiltinColorRamp.INFERNO)

    suspend fun magma(): MTColorRamp = create(MTBuiltinColorRamp.MAGMA)

    suspend fun plasma(): MTColorRamp = create(MTBuiltinColorRamp.PLASMA)

    suspend fun warm(): MTColorRamp = create(MTBuiltinColorRamp.WARM)

    suspend fun cool(): MTColorRamp = create(MTBuiltinColorRamp.COOL)

    suspend fun rainbowSoft(): MTColorRamp = create(MTBuiltinColorRamp.RAINBOW_SOFT)

    suspend fun bathymetry(): MTColorRamp = create(MTBuiltinColorRamp.BATHYMETRY)

    suspend fun cdom(): MTColorRamp = create(MTBuiltinColorRamp.CDOM)

    suspend fun chlorophyll(): MTColorRamp = create(MTBuiltinColorRamp.CHLOROPHYLL)

    suspend fun density(): MTColorRamp = create(MTBuiltinColorRamp.DENSITY)

    suspend fun freeSurfaceBlue(): MTColorRamp = create(MTBuiltinColorRamp.FREESURFACE_BLUE)

    suspend fun freeSurfaceRed(): MTColorRamp = create(MTBuiltinColorRamp.FREESURFACE_RED)

    suspend fun oxygen(): MTColorRamp = create(MTBuiltinColorRamp.OXYGEN)

    suspend fun par(): MTColorRamp = create(MTBuiltinColorRamp.PAR)

    suspend fun phase(): MTColorRamp = create(MTBuiltinColorRamp.PHASE)

    suspend fun salinity(): MTColorRamp = create(MTBuiltinColorRamp.SALINITY)

    suspend fun temperature(): MTColorRamp = create(MTBuiltinColorRamp.TEMPERATURE)

    suspend fun turbidity(): MTColorRamp = create(MTBuiltinColorRamp.TURBIDITY)

    suspend fun velocityBlue(): MTColorRamp = create(MTBuiltinColorRamp.VELOCITY_BLUE)

    suspend fun velocityGreen(): MTColorRamp = create(MTBuiltinColorRamp.VELOCITY_GREEN)

    suspend fun cubehelix(): MTColorRamp = create(MTBuiltinColorRamp.CUBEHELIX)

    suspend fun cividis(): MTColorRamp = create(MTBuiltinColorRamp.CIVIDIS)

    suspend fun turbo(): MTColorRamp = create(MTBuiltinColorRamp.TURBO)

    suspend fun rocket(): MTColorRamp = create(MTBuiltinColorRamp.ROCKET)

    suspend fun mako(): MTColorRamp = create(MTBuiltinColorRamp.MAKO)
}
