package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.bridge.MTCommand

/**
 * A command used to load and evaluate a module's core bundle into the map's runtime engine.
 *
 * @property bundleString The raw string content of the module's bundle.
 */
internal data class LoadModuleBundle(
    val bundleString: String,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String = bundleString
}
