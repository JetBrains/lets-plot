/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalForeignApi::class)

package org.jetbrains.letsPlot.commons.encoding

import kotlinx.cinterop.*
import platform.zlib.*

actual fun deflate(input: ByteArray): ByteArray {
    memScoped {
        val inputSize = input.size.toULong() // Use ULong for sourceLen
        if (inputSize == 0uL) { // Handle empty input
            return ByteArray(0)
        }
        val inputPtr = input.refTo(0).getPointer(this)

        // Calculate maximum possible output size.
        // compressBound is the correct function for this.
        val maxOutputSize = compressBound(inputSize).toLong() // Convert to Long for ByteArray size
        val output = ByteArray(maxOutputSize.toInt()) // Allocate the buffer
        val outputPtr = output.refTo(0).getPointer(this)

        // destLen is an input/output parameter.
        // On input, it's the size of the output buffer.
        // On output, it's the actual compressed size.
        val destLen = alloc<uLongfVar>() // zlib uses uLongf for lengths
        destLen.value = maxOutputSize.toULong() // Initialize with the TOTAL size of the output buffer

        // Choose a valid compression level.
        // Z_DEFAULT_COMPRESSION is a good general choice.
        // 0 means no compression, which can lead to output > input.
        // 1-9 are actual compression levels (1=fastest, 9=best compression).
        val compressionLevel = Z_DEFAULT_COMPRESSION // Or a value from 1 to 9

        val result = compress2(
            dest = outputPtr.reinterpret(),     // Pointer to the output buffer
            destLen = destLen.ptr,              // Pointer to the variable holding output buffer size/compressed size
            source = inputPtr.reinterpret(),    // Pointer to the input buffer
            sourceLen = inputSize,              // Length of the input data
            level = compressionLevel            // Compression level
        )

        check(result == Z_OK) { "Zlib compression failed: $result. Input size: $inputSize, Max output: $maxOutputSize, Level: $compressionLevel" }

        // The actual compressed size is now in destLen.value
        return output.copyOf(destLen.value.toInt())
    }
}

actual fun inflate(input: ByteArray, expectedSize: Int): ByteArray {
    memScoped {
        val inputPtr = input.refTo(0).getPointer(this)
        val output = ByteArray(expectedSize)
        val outputPtr = output.refTo(0).getPointer(this)
        val outputSize = alloc<platform.posix.size_tVar>()
        outputSize.value = expectedSize.toULong()

        val result = uncompress(
            dest = outputPtr.reinterpret(),
            destLen = outputSize.ptr,
            source = inputPtr.reinterpret(),
            sourceLen = input.size.toULong()
        )

        check(result == Z_OK) { "Zlib decompression failed: $result" }
        return output.copyOf(outputSize.value.toInt())
    }
}