/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.bridge

import android.content.Context
import android.webkit.WebView
import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.logging.MTLogLevel
import com.maptiler.maptilersdk.logging.MTLogType
import com.maptiler.maptilersdk.logging.MTLogger
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal interface WebViewExecutorDelegate {
    fun onNavigationFinished(url: String)
}

internal class WebViewExecutor(
    context: Context,
) : WebViewManagerDelegate,
    MTCommandExecutable {
    private val exceptionKey = "WKJavaScriptExceptionMessage"
    private var webView: WebView? = null
    val webViewManager: WebViewManager =
        WebViewManager(context).apply {
            delegate = this@WebViewExecutor
        }

    var delegate: WebViewExecutorDelegate? = null

    init {
        webView = webViewManager.getAttachableWebView()
        webView?.isVerticalScrollBarEnabled = false
    }

    fun getWebView(): WebView? = webView

    override suspend fun execute(command: MTCommand): MTBridgeReturnType =
        withContext(Dispatchers.Main) {
            val webView = webView ?: throw MTError.BridgeNotLoaded
            val isVerbose = MTConfig.logLevel == MTLogLevel.Debug(true)

            val deferred = CompletableDeferred<MTBridgeReturnType>()

            webView.evaluateJavascript(command.toJS()) { result ->
                try {
                    if (result == null || result == "null") {
                        if (isVerbose) {
                            MTLogger.log("$command completed with unsupported return type.", MTLogType.WARNING)
                        }

                        deferred.complete(MTBridgeReturnType.UnsupportedType)
                    } else {
                        try {
                            val parsedResult = MTBridgeReturnType.from(result)
                            deferred.complete(parsedResult)
                        } catch (e: Exception) {
                            deferred.completeExceptionally(MTError.InvalidResultType(result))
                        }
                    }
                } catch (e: Exception) {
                    if (isVerbose) {
                        MTLogger.log("Bridging error occurred for $command: ${e.message}", MTLogType.ERROR)
                    }

                    deferred.completeExceptionally(MTError.Unknown(e.message ?: "Unknown error"))
                }
            }

            return@withContext deferred.await()
        }

    override fun onNavigationFinished(url: String) {
        delegate?.onNavigationFinished(url)
    }
}
