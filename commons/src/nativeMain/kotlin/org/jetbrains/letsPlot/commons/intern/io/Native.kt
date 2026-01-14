/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalForeignApi::class)

package org.jetbrains.letsPlot.commons.intern.io

import kotlinx.cinterop.*
import platform.posix.*
import kotlin.experimental.ExperimentalNativeApi

object Native {
    @OptIn(ExperimentalNativeApi::class)
    fun getOSName(): String {
        if (Platform.osFamily == OsFamily.MACOSX) {
            return "darwin" // test images are already named with 'darwin' suffix
        }
        return Platform.osFamily.name.lowercase()
    }

    fun getCurrentDir(): String {
        return memScoped {
            val bufferSize = 4096 * 8
            val buffer = allocArray<ByteVar>(bufferSize)
            if (getcwd(buffer, bufferSize.convert()) != null) {
                buffer.toKString()
            } else {
                "." // Default to current directory on error
            }
        }
    }

    fun writeToFile(path: String, data: ByteArray) {
        if (data.isEmpty()) {
            val file = fopen(path, "wb") ?: throw Error("Failed to open file for writing (empty): $path")
            fclose(file)
            return
        }

        val file: CPointer<FILE> = fopen(path, "wb") ?: throw Error("Failed to open file for writing: $path")
        try {
            val written = data.usePinned { pinned ->
                // fwrite(ptr, size_of_element, number_of_elements, stream)
                // It's common to use size 1 and number of elements as the total size.
                fwrite(pinned.addressOf(0), 1.toULong(), data.size.toULong(), file)
            }
            @Suppress("RemoveRedundantCallsOfConversionMethods")
            if (written.toLong() != data.size.toLong()) {
                val errorNum = ferror(file)
                throw Error("Failed to write all data to file: $path. Wrote $written of ${data.size} bytes. ferror=$errorNum")
            }
        } finally {
            fclose(file)
        }
    }
    fun readFromFile(path: String): ByteArray {
        val file: CPointer<FILE> = fopen(path, "rb") ?: throw Error("Failed to open file for reading: $path")
        try {
            fseek(file, 0, SEEK_END)
            @Suppress("RemoveRedundantCallsOfConversionMethods")
            val fileSize = ftell(file).toLong()
            if (fileSize < 0L) { // ftell returns -1 on error
                throw Error("Failed to determine file size: $path")
            }
            rewind(file)
            if (fileSize == 0L) {
                return ByteArray(0)
            }
            val buffer = ByteArray(fileSize.toInt())
            val readBytes = buffer.usePinned { pinned ->
                // fread(ptr, size_of_element, number_of_elements, stream)
                fread(pinned.addressOf(0), 1.toULong(), fileSize.toULong(), file)
            }
            @Suppress("RemoveRedundantCallsOfConversionMethods") // on Windows readBytes has type Int
            if (readBytes.toLong() != fileSize) {
                val errorNum = ferror(file)
                throw Error("Failed to read all data from file: $path. Read $readBytes of $fileSize bytes. ferror=$errorNum")
            }
            return buffer
        } finally {
            fclose(file)
        }
    }
}