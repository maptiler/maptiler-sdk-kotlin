/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import android.content.Context
import com.maptiler.maptilersdk.logging.MTLogType
import com.maptiler.maptilersdk.logging.MTLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

internal object MTOfflineStorage {
    suspend fun saveMetadata(
        context: Context,
        packId: String,
        metadata: MTOfflinePackMetadata,
    ) = withContext(Dispatchers.IO) {
        val file = MTOfflineStoragePaths.getMetadataFile(context, packId)
        val json = metadata.toJson()
        writeAtomic(file, json.toByteArray())
    }

    suspend fun loadMetadata(
        context: Context,
        packId: String,
    ): MTOfflinePackMetadata? =
        withContext(Dispatchers.IO) {
            val file = MTOfflineStoragePaths.getMetadataFile(context, packId)
            if (!file.exists()) return@withContext null

            try {
                val json = file.readText()
                MTOfflinePackMetadata.fromJson(json)
            } catch (e: Exception) {
                MTLogger.log("Failed to load metadata for pack $packId: ${e.message}", MTLogType.ERROR)
                null
            }
        }

    suspend fun listMetadata(context: Context): List<MTOfflinePackMetadata> =
        withContext(Dispatchers.IO) {
            val root = MTOfflineStoragePaths.getRootDirectory(context)
            if (!root.exists()) return@withContext emptyList()

            val packs = mutableListOf<MTOfflinePackMetadata>()
            root.listFiles()?.forEach { packDir ->
                if (packDir.isDirectory) {
                    val metadata = loadMetadata(context, packDir.name)
                    if (metadata != null) {
                        packs.add(metadata)
                    } else {
                        // Handle corrupted metadata by cleaning up the invalid pack directory
                        MTLogger.log("Metadata corrupted or missing for pack ${packDir.name}, cleaning up directory.", MTLogType.WARNING)
                        deletePack(context, packDir.name)
                    }
                }
            }
            packs
        }

    suspend fun saveManifest(
        context: Context,
        packId: String,
        manifest: MTManifest,
    ) = withContext(Dispatchers.IO) {
        val file = MTOfflineStoragePaths.getManifestFile(context, packId)
        val json = manifest.toJson()
        writeAtomic(file, json.toByteArray())
    }

    suspend fun loadManifest(
        context: Context,
        packId: String,
    ): MTManifest? =
        withContext(Dispatchers.IO) {
            val file = MTOfflineStoragePaths.getManifestFile(context, packId)
            if (!file.exists()) return@withContext null

            try {
                val json = file.readText()
                MTManifest.fromJson(json)
            } catch (e: Exception) {
                MTLogger.log("Failed to load manifest for pack $packId: ${e.message}", MTLogType.ERROR)
                null
            }
        }

    suspend fun deletePack(
        context: Context,
        packId: String,
    ) = withContext(Dispatchers.IO) {
        val packDir = MTOfflineStoragePaths.getPackDirectory(context, packId)
        if (packDir.exists()) {
            try {
                packDir.deleteRecursively()
                MTLogger.log("Successfully deleted pack $packId", MTLogType.INFO)
            } catch (e: Exception) {
                MTLogger.log("Failed to delete pack $packId: ${e.message}", MTLogType.ERROR)
            }
        }
    }

    /**
     * Cleans up any stale temporary files.
     *
     * @param context Android context.
     * @param packId Optional pack ID to clean specific pack directory. If null, cleans the global temp directory.
     */
    suspend fun cleanStaleTempFiles(
        context: Context,
        packId: String? = null,
    ) = withContext(Dispatchers.IO) {
        try {
            if (packId != null) {
                val packDir = MTOfflineStoragePaths.getPackDirectory(context, packId)
                if (!packDir.exists()) return@withContext

                packDir.listFiles()?.forEach { file ->
                    val name = file.name
                    // Remove hidden files, UUID-style temp files, or .tmp files
                    if (name.startsWith(".") || name.endsWith(".tmp") || isUuid(name)) {
                        file.delete()
                    }
                }
            } else {
                val tempDir = MTOfflineStoragePaths.getTempDirectory(context)
                if (tempDir.exists()) {
                    tempDir.listFiles()?.forEach { file ->
                        file.deleteRecursively()
                    }
                }
            }
        } catch (e: Exception) {
            MTLogger.log("Error cleaning stale temp files: ${e.message}", MTLogType.ERROR)
        }
    }

    suspend fun calculatePackSize(
        context: Context,
        packId: String,
    ): Long =
        withContext(Dispatchers.IO) {
            val packDir = MTOfflineStoragePaths.getPackDirectory(context, packId)
            calculateDirectorySize(packDir)
        }

    fun isFileVerified(file: File): Boolean {
        return file.exists() && file.length() > 0
    }

    suspend fun isFileVerified(
        context: Context,
        packId: String,
        relativePath: String,
    ): Boolean =
        withContext(Dispatchers.IO) {
            val file = MTOfflineStoragePaths.getAbsoluteFile(context, packId, relativePath)
            isFileVerified(file)
        }

    /**
     * Checks if there is enough available space on the device for the given byte size.
     *
     * @param context Android context.
     * @param requiredBytes The estimated number of bytes required.
     * @return True if there is enough space, false otherwise.
     */
    suspend fun hasAvailableSpace(
        context: Context,
        requiredBytes: Long,
    ): Boolean =
        withContext(Dispatchers.IO) {
            val root = MTOfflineStoragePaths.getRootDirectory(context)
            // Ensure directory exists to get accurate stats
            secureCreateDirectory(root)
            val usableSpace = root.usableSpace
            val hasSpace = usableSpace >= requiredBytes

            if (!hasSpace) {
                MTLogger.log("Not enough available space. Required: $requiredBytes, Usable: $usableSpace", MTLogType.WARNING)
            }
            hasSpace
        }

    suspend fun moveFile(
        from: File,
        to: File,
    ) = withContext(Dispatchers.IO) {
        val destinationDir = to.parentFile
        if (destinationDir != null) {
            secureCreateDirectory(destinationDir)
        }

        try {
            Files.move(
                from.toPath(),
                to.toPath(),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE,
            )
        } catch (e: Exception) {
            // Fallback if atomic move is not supported or fails across volumes
            MTLogger.log("Atomic move failed, falling back to standard move: ${e.message}", MTLogType.WARNING)
            try {
                Files.move(
                    from.toPath(),
                    to.toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                )
            } catch (e2: Exception) {
                MTLogger.log("Fallback move failed: ${e2.message}", MTLogType.ERROR)
                throw MTOfflineStorageError.WriteFailed(e2)
            }
        }
    }

    suspend fun write(
        data: ByteArray,
        file: File,
    ) = writeAtomic(file, data)

    // MARK: - Private Helpers

    private suspend fun writeAtomic(
        file: File,
        data: ByteArray,
    ) {
        val tempFile = File(file.parent, "${file.name}.tmp")
        secureCreateDirectory(file.parentFile ?: return)

        try {
            withContext(Dispatchers.IO) {
                tempFile.writeBytes(data)
            }
            moveFile(tempFile, file)
        } catch (e: Exception) {
            MTLogger.log("Atomic write failed for ${file.name}: ${e.message}", MTLogType.ERROR)
            if (tempFile.exists()) tempFile.delete()
            if (e is MTOfflineStorageError) throw e
            throw MTOfflineStorageError.WriteFailed(e)
        }
    }

    private fun secureCreateDirectory(directory: File) {
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                val msg = "Failed to create directory: ${directory.absolutePath}"
                MTLogger.log(msg, MTLogType.ERROR)
                throw MTOfflineStorageError.WriteFailed(IOException(msg))
            }
        }
    }

    private fun calculateDirectorySize(directory: File): Long {
        if (!directory.exists()) return 0
        if (directory.isFile) return directory.length()

        var size: Long = 0
        directory.listFiles()?.forEach { file ->
            size +=
                if (file.isDirectory) {
                    calculateDirectorySize(file)
                } else {
                    file.length()
                }
        }
        return size
    }

    private fun isUuid(name: String): Boolean {
        return try {
            java.util.UUID.fromString(name)
            true
        } catch (e: Exception) {
            false
        }
    }
}
