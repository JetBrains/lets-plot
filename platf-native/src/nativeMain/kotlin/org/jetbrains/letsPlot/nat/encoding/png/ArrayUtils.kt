/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 *
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 *
 * THE FOLLOWING IS THE COPYRIGHT OF THE ORIGINAL DOCUMENT:
 *
 * Copyright (c) 2009-2012, Hernán J. González.
 * Licensed under the Apache License, Version 2.0.
 *
 * The original PNGJ library is written in Java and can be found here: [PNGJ](https://github.com/leonbloy/pngj).
 */

package org.jetbrains.letsPlot.nat.encoding.png

internal fun arraycopy(
    src: ByteArray,
    srcPos: Int,
    dest: ByteArray,
    destPos: Int,
    length: Int
) {
    src.copyInto(dest, destPos, srcPos, srcPos + length)
}

internal fun arraycopy(
    src: DoubleArray,
    srcPos: Int,
    dest: DoubleArray,
    destPos: Int,
    length: Int
) {
    src.copyInto(dest, destPos, srcPos, srcPos + length)
}

internal fun fill(
    src: ByteArray,
    value: Byte
) {
    src.fill(value)
}

internal fun fill(
    src: IntArray,
    value: Int
) {
    src.fill(value)
}

internal fun fill(
    src: DoubleArray,
    value: Double
) {
    src.fill(value)
}
