package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import kotlinx.serialization.encodeToString

internal data class SetRTLTextPlugin(
    private val pluginUrl: String,
    private val deferred: Boolean = false,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val urlString = JsonConfig.json.encodeToString(pluginUrl)
        return "${MTBridge.MAP_OBJECT}.setRTLTextPlugin($urlString, null, $deferred);"
    }
}
