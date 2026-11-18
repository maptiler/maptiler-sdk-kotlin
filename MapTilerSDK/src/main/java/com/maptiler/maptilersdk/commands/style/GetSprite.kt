package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

internal class GetSprite : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String = "JSON.stringify(${MTBridge.MAP_OBJECT}.getSprite());"
}
