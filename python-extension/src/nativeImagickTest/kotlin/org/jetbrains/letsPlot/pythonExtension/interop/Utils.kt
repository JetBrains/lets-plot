/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.pythonExtension.interop

import kotlinx.cinterop.*
import platform.posix.*
import kotlin.experimental.ExperimentalNativeApi

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

// TODO: never used - need to test. Should be used to store test images in PNG instead of BMP format.
fun writeToFile(path: String, data: ByteArray) {
    memScoped {
        // O_WRONLY: write only, O_CREAT: create if not exists, O_TRUNC: truncate if exists
        val fd = open(path, O_WRONLY or O_CREAT or O_TRUNC or O_BINARY, 0b110_100_100) // 0644 in octal

        if (fd == -1) {
            perror("open")
            throw Error("Failed to open file: $path")
        }

        val written = data.usePinned { pinned ->
            write(fd, pinned.addressOf(0), data.size.convert())
        }

        @Suppress("RemoveRedundantCallsOfConversionMethods") // On Windows `written` is Int
        if (written.toLong() != data.size.toLong()) {
            perror("write")
            close(fd)
            throw Error("Failed to write all data")
        }

        close(fd)
    }
}

fun readFromFile(path: String): ByteArray {
    memScoped {
        // O_RDONLY: read only
        val fd = open(path, O_RDONLY or O_BINARY)

        if (fd == -1) {
            perror("open")
            throw Error("Failed to open file: $path")
        }

        val fileSize = lseek(fd, 0, SEEK_END)
        @Suppress("RemoveRedundantCallsOfConversionMethods") // On Windows `fileSize` is Int
        if (fileSize.toLong() == -1L) {
            perror("lseek")
            close(fd)
            throw Error("Failed to get file size: $path")
        }

        lseek(fd, 0, SEEK_SET) // Reset file pointer to the beginning

        val buffer = ByteArray(fileSize.toInt())
        val readBytes = buffer.usePinned { pinned ->
            read(fd, pinned.addressOf(0), fileSize.convert())
        }

        if (readBytes != fileSize) {
            perror("read")
            close(fd)
            throw Error("Failed to read all data from file: $path")
        }

        close(fd)
        return buffer
    }
}
