package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

internal data class SetUseEuEndpoints(
    val useEuEndpoints: Boolean,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String = "${MTBridge.SDK_OBJECT}.config.useEuEndpoints = $useEuEndpoints;"
}
