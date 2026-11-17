package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

internal class IsLanguageUpdated : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String = "${MTBridge.MAP_OBJECT}.isLanguageUpdated();"
}
