/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.bridge

import android.content.Context
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
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
    private val context: Context,
) : MTCommandExecutable {
    object Constants {
        object Error {
            const val HANDLER = "errorHandler"
            const val MESSAGE = "message"
            const val UNKNOWN = "Unknown Error"
        }

        object Map {
            const val HANDLER = "mapHandler"
            const val EVENT = "event"
            const val DATA = "data"
        }

        object JSResources {
            const val MAPTILER_MAP = "MapTilerMap"
            const val MAPTILER_SDK = "maptiler-sdk.umd.min"
            const val MAPTILER_STYLESHEET = "maptiler-sdk"

            const val HTML_EXTENSION = "html"
            const val JS_EXTENSION = "js"
            const val CSS_EXTENSION = "css"
        }
    }

    private var _webView: WebView? = null
    internal val webView: WebView
        get() {
            if (_webView == null) {
                initWebViewIfNeeded(context)
            }

            return _webView!!
        }

    var delegate: WebViewExecutorDelegate? = null

    private fun initWebViewIfNeeded(context: Context) {
        if (_webView == null) {
            _webView =
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.allowFileAccess = true
                    settings.domStorageEnabled = true
                    settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                    settings.useWideViewPort = true
                    settings.loadWithOverviewMode = true

                    isVerticalScrollBarEnabled = false

                    webChromeClient = WebChromeClient()
                    webViewClient =
                        object : WebViewClient() {
                            override fun onPageFinished(
                                view: WebView?,
                                url: String?,
                            ) {
                                url?.let { delegate?.onNavigationFinished(it) }
                            }
                        }

                    layoutParams =
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )

                    loadUrl("file:///android_asset/${Constants.JSResources.MAPTILER_MAP}.${Constants.JSResources.HTML_EXTENSION}")
                }
        }
    }

    internal fun setWebView(webView: WebView) {
        this._webView = webView
    }

    internal fun getAttachableWebView(): WebView? = webView

    override suspend fun execute(command: MTCommand): MTBridgeReturnType =
        withContext(Dispatchers.Main) {
            val webView = webView ?: throw MTError.BridgeNotLoaded
            val isVerbose = MTConfig.logLevel == MTLogLevel.Debug(true)

            val deferred = CompletableDeferred<MTBridgeReturnType>()

            if (command.isPrimitiveReturnType) {
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
            } else {
                webView.evaluateJavascript(command.toJS(), null)
                deferred.complete(MTBridgeReturnType.UnsupportedType)
            }

            return@withContext deferred.await()
        }

    internal fun destroy() {
        _webView?.destroy()
        _webView = null
    }

    internal fun addJSInterface(jsInterface: MTJavaScriptInterface) {
        webView.addJavascriptInterface(jsInterface, "Android")
    }

    internal fun reload() {
        webView.reload()
    }
}
