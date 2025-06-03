/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.encoding

import java.util.zip.Deflater

actual fun deflate(input: ByteArray): ByteArray {
    val deflater = java.util.zip.Deflater()
    deflater.setLevel(Deflater.BEST_COMPRESSION)
    deflater.setInput(input)
    deflater.finish()
    val output = java.io.ByteArrayOutputStream()
    val buffer = ByteArray(1024)
    while (!deflater.finished()) {
        val count = deflater.deflate(buffer)
        output.write(buffer, 0, count)
    }
    deflater.end()
    return output.toByteArray()
}

actual fun inflate(input: ByteArray, expectedSize: Int): ByteArray {
    val inflater = java.util.zip.Inflater()
    inflater.setInput(input)
    val output = ByteArray(expectedSize)
    val count = inflater.inflate(output)
    inflater.end()
    return output.copyOf(count)
}
