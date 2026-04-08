package org.jetbrains.letsPlot.commons.encoding

import js.buffer.toArrayBuffer
import js.typedarrays.toByteArray

actual fun deflate(input: ByteArray): ByteArray {
    return pako.deflate(input.toArrayBuffer()).toByteArray()
}

actual fun inflate(input: ByteArray, expectedSize: Int): ByteArray {
    return pako.inflate(input.toArrayBuffer()).toByteArray()
}