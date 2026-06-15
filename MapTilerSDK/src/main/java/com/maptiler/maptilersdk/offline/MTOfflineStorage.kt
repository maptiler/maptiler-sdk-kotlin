/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import android.content.Context
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
                null
            }
        }

    suspend fun listMetadata(context: Context): List<MTOfflinePackMetadata> =
        withContext(Dispatchers.IO) {
            val root = MTOfflineStoragePaths.getRootDirectory(context)
            if (!root.exists()) return@withContext emptyList()

            root.listFiles()?.mapNotNull { packDir ->
                if (packDir.isDirectory) {
                    loadMetadata(context, packDir.name)
                } else {
                    null
                }
            } ?: emptyList()
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
                null
            }
        }

    suspend fun deletePack(
        context: Context,
        packId: String,
    ) = withContext(Dispatchers.IO) {
        val packDir = MTOfflineStoragePaths.getPackDirectory(context, packId)
        if (packDir.exists()) {
            packDir.deleteRecursively()
        }
    }

    suspend fun cleanStaleTempFiles(
        context: Context,
        packId: String,
    ) = withContext(Dispatchers.IO) {
        val packDir = MTOfflineStoragePaths.getPackDirectory(context, packId)
        if (!packDir.exists()) return@withContext

        packDir.listFiles()?.forEach { file ->
            if (file.name.endsWith(".tmp")) {
                file.delete()
            }
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

    suspend fun isFileVerified(
        context: Context,
        packId: String,
        relativePath: String,
    ): Boolean =
        withContext(Dispatchers.IO) {
            val file = MTOfflineStoragePaths.getAbsoluteFile(context, packId, relativePath)
            file.exists() && file.length() > 0
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
            try {
                Files.move(
                    from.toPath(),
                    to.toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                )
            } catch (e2: Exception) {
                throw MTOfflineStorageError.WriteFailed(e2)
            }
        }
    }

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
            if (tempFile.exists()) tempFile.delete()
            if (e is MTOfflineStorageError) throw e
            throw MTOfflineStorageError.WriteFailed(e)
        }
    }

    private fun secureCreateDirectory(directory: File) {
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw MTOfflineStorageError.WriteFailed(IOException("Failed to create directory: ${directory.absolutePath}"))
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
}
