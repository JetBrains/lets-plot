/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.encoding

import org.khronos.webgl.Int8Array
import org.khronos.webgl.Uint8Array

// Define the bindings to the external JS library
@JsModule("pako")
@JsNonModule
external object Pako {
    fun deflate(input: Uint8Array): Uint8Array
    fun inflate(input: Uint8Array): Uint8Array
}

actual fun deflate(input: ByteArray): ByteArray {
    val inputInt8 = input.unsafeCast<Int8Array>()
    val inputUint8 = Uint8Array(inputInt8.buffer, inputInt8.byteOffset, inputInt8.length)

    val outputUint8 = Pako.deflate(inputUint8)

    return Int8Array(outputUint8.buffer, outputUint8.byteOffset, outputUint8.length).unsafeCast<ByteArray>()
}

actual fun inflate(input: ByteArray, expectedSize: Int): ByteArray {
    val inputInt8 = input.unsafeCast<Int8Array>()
    val inputUint8 = Uint8Array(inputInt8.buffer, inputInt8.byteOffset, inputInt8.length)

    val outputUint8 = Pako.inflate(inputUint8)

    return Int8Array(outputUint8.buffer, outputUint8.byteOffset, outputUint8.length).unsafeCast<ByteArray>()
}
