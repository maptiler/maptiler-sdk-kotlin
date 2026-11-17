package com.maptiler.maptilersdk.commands.navigation

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

internal class GetVerticalFieldOfView : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String = "${MTBridge.MAP_OBJECT}.getVerticalFieldOfView();"
}
