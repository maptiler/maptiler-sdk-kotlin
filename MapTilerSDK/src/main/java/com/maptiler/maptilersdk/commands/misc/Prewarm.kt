package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

internal class Prewarm : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String = "${MTBridge.SDK_OBJECT}.prewarm();"
}
