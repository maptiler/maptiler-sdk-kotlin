/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.logging

import android.util.Log
import com.maptiler.maptilersdk.MTConfig

class OSLogger : MTLoggable {
    override fun log(
        message: String,
        type: MTLogType,
    ) {
        val logLevel = MTConfig.logLevel

        when (type) {
            MTLogType.INFO ->
                if (logLevel != MTLogLevel.None) {
                    Log.i("$type", message)
                }
            MTLogType.WARNING ->
                if (logLevel != MTLogLevel.None) {
                    Log.w("$type", message)
                }
            MTLogType.ERROR ->
                if (logLevel == MTLogLevel.Debug() || logLevel == MTLogLevel.Debug(verbose = true)) {
                    Log.e("$type", message)
                }
            MTLogType.CRITICAL_ERROR ->
                if (logLevel == MTLogLevel.Debug() || logLevel == MTLogLevel.Debug(verbose = true)) {
                    Log.wtf("$type", message)
                }
            MTLogType.EVENT ->
                if (logLevel != MTLogLevel.None && logLevel == MTLogLevel.Debug(verbose = true)) {
                    Log.i("$type", message)
                }
        }
    }
}
