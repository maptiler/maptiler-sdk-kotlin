/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.bridge

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

internal interface WebViewManagerDelegate {
    fun onNavigationFinished(url: String)
}

@SuppressLint("SetJavaScriptEnabled")
internal class WebViewManager(
    private val context: Context,
) {
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

    var delegate: WebViewManagerDelegate? = null
    private var webView: WebView? = null

    fun getAttachableWebView(): WebView {
        if (webView == null) {
            webView =
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.allowFileAccess = false
                    settings.allowContentAccess = false

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

                    loadUrl("file:///android_asset/${Constants.JSResources.MAPTILER_MAP}.${Constants.JSResources.HTML_EXTENSION}")
                }
        }

        return webView!!
    }
}
