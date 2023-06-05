/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.util.pngj

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
