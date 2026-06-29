/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import android.content.Context
import com.maptiler.maptilersdk.logging.MTLogType
import com.maptiler.maptilersdk.logging.MTLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

/**
 * A lightweight HTTP server for serving offline map assets.
 */
internal object MTOfflineHTTPServer {
    private var serverSocket: ServerSocket? = null
    private var port: Int = 18080
    private var isRunning = false
    private val serverExecutor = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val requestExecutor = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
    private val scope = CoroutineScope(SupervisorJob() + serverExecutor)

    /**
     * Starts the server on the specified port.
     *
     * @param context Android context.
     * @param preferredPort The port to listen on. Defaults to 18080.
     */
    fun start(
        context: Context,
        preferredPort: Int = 18080,
    ) {
        synchronized(this) {
            if (isRunning) return
            isRunning = true
        }

        scope.launch {
            try {
                serverSocket =
                    try {
                        ServerSocket(preferredPort)
                    } catch (e: Exception) {
                        MTLogger.log("Port $preferredPort is in use, falling back to any available port", MTLogType.INFO)
                        ServerSocket(0)
                    }

                port = serverSocket?.localPort ?: 0
                MTLogger.log("Offline server ready on port $port", MTLogType.INFO)

                val router = MTOfflineRouter(context.applicationContext)

                while (isRunning) {
                    val clientSocket = serverSocket?.accept() ?: break
                    launch(requestExecutor) {
                        handleClient(clientSocket, router)
                    }
                }
            } catch (e: Exception) {
                if (isRunning) {
                    MTLogger.log("Offline server error: ${e.message}", MTLogType.ERROR)
                }
            } finally {
                stop()
            }
        }
    }

    /**
     * Stops the server and releases the port.
     */
    fun stop() {
        synchronized(this) {
            if (!isRunning) return
            isRunning = false
        }
        try {
            serverSocket?.close()
        } catch (e: Exception) {
            // Ignore
        }
        serverSocket = null
        MTLogger.log("Offline server stopped", MTLogType.INFO)
    }

    /**
     * Returns the base URL string for the server.
     */
    fun baseURLString(): String {
        return "http://127.0.0.1:$port"
    }

    private fun handleClient(
        socket: Socket,
        router: MTOfflineRouter,
    ) {
        try {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val firstLine = reader.readLine() ?: return
            val parts = firstLine.split(" ")
            if (parts.size < 2) return

            val method = parts[0]
            val path = parts[1]

            if (method != "GET") {
                sendResponse(socket, 405, "Method Not Allowed".toByteArray(), "text/plain")
                return
            }

            if (path == "/health" || path == "/health/") {
                sendResponse(socket, 200, "OK".toByteArray(), "text/plain")
                return
            }

            val resolved = router.resolve(path)
            if (resolved == null) {
                sendResponse(socket, 404, "Not Found".toByteArray(), "text/plain")
                return
            }

            if (resolved.file.name == "style.json") {
                handleStyleJSON(socket, resolved, router.context)
                return
            }

            if (resolved.file.exists()) {
                sendResponse(socket, 200, resolved.file.readBytes(), resolved.mimeType)
            } else {
                sendResponse(socket, 404, "Not Found".toByteArray(), "text/plain")
            }
        } catch (e: Exception) {
            // Ignore socket errors
        } finally {
            try {
                socket.close()
            } catch (e: Exception) {
            }
        }
    }

    private fun handleStyleJSON(
        socket: Socket,
        resolved: MTOfflineRouter.ResolvedResource,
        context: Context,
    ) {
        try {
            val fileData = resolved.file.readText()
            val jsonObject = com.maptiler.maptilersdk.helpers.JsonConfig.json.decodeFromString<JsonObject>(fileData)

            val pathParts = resolved.file.parentFile?.name ?: ""
            val packId = pathParts

            var downloadedMaxZoom: Int? = null
            // Load manifest to get metadata
            val manifestFile = MTOfflineStoragePaths.getManifestFile(context, packId)
            if (manifestFile.exists()) {
                try {
                    val manifestJson = manifestFile.readText()
                    val manifest = com.maptiler.maptilersdk.helpers.JsonConfig.json.decodeFromString<JsonObject>(manifestJson)
                    downloadedMaxZoom = manifest["metadata"]?.jsonObject?.get("maxZoom")?.jsonPrimitive?.content?.toIntOrNull()
                } catch (e: Exception) {
                }
            }

            val processor = MTStyleProcessor(baseURLString(), packId)
            val transformed = processor.transform(jsonObject, downloadedMaxZoom)
            val transformedData = com.maptiler.maptilersdk.helpers.JsonConfig.json.encodeToString(JsonObject.serializer(), transformed)

            sendResponse(socket, 200, transformedData.toByteArray(), "application/json")
        } catch (e: Exception) {
            sendResponse(socket, 500, "Internal Server Error".toByteArray(), "text/plain")
        }
    }

    private fun sendResponse(
        socket: Socket,
        statusCode: Int,
        body: ByteArray,
        mimeType: String,
    ) {
        val statusText =
            when (statusCode) {
                200 -> "OK"
                404 -> "Not Found"
                405 -> "Method Not Allowed"
                else -> "Internal Server Error"
            }

        val out = BufferedOutputStream(socket.getOutputStream())
        val header = StringBuilder()
        header.append("HTTP/1.1 $statusCode $statusText\r\n")
        header.append("Content-Type: $mimeType\r\n")
        header.append("Content-Length: ${body.size}\r\n")
        header.append("Access-Control-Allow-Origin: *\r\n")
        header.append("Connection: close\r\n")
        header.append("\r\n")

        out.write(header.toString().toByteArray())
        out.write(body)
        out.flush()
    }

    /**
     * Checks if the server is currently running.
     */
    fun isRunning(): Boolean = synchronized(this) { isRunning }
}
