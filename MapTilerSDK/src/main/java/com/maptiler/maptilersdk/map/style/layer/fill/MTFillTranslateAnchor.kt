package com.maptiler.maptilersdk.map.style.layer.fill

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Enum controlling the frame of reference for fill translate.
 */
@Serializable
enum class MTFillTranslateAnchor {
    /**
     * The fill is translated relative to the map.
     */
    @SerialName("map")
    MAP,

    /**
     * The fill is translated relative to the viewport.
     */
    @SerialName("viewport")
    VIEWPORT,
}
