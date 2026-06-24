/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import com.maptiler.maptilersdk.MTConfig
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 * Shared OkHttp client specifically configured for offline map downloads.
 */
internal object MTOfflineHttpClient {
    /**
     * Interceptor to add the custom User-Agent to all requests.
     */
    private class UserAgentInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val requestWithUserAgent =
                originalRequest.newBuilder()
                    .header("User-Agent", MTConfig.CUSTOM_USER_AGENT)
                    .build()
            return chain.proceed(requestWithUserAgent)
        }
    }

    /**
     * The configured OkHttpClient instance for offline downloads.
     * Features optimized connection pooling for many small files and appropriate timeouts.
     */
    val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectionPool(ConnectionPool(10, 5, TimeUnit.MINUTES))
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(UserAgentInterceptor())
            .build()
    }
}
