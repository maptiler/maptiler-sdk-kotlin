package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

internal data class SetMaxParallelImageRequests(
    private val numRequests: Int,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String = "${MTBridge.MAP_OBJECT}._setMaxParallelImageRequests($numRequests);"
}
