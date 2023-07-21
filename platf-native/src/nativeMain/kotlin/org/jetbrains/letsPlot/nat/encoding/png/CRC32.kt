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

import kotlinx.cinterop.*
import platform.zlib.crc32
import platform.zlib.uLong

class CRC32: Checksum {
    private var crc: uLong = 0U
    override fun update(b: ByteArray, off: Int, len: Int) {
        memScoped {
            val buffer = allocArray<ByteVar>(len)
            for (i in off until off + len) {
                buffer[i - off] = b[i]
            }
            crc = crc32(crc, buffer.reinterpret(), len.convert())
        }
    }
    override val value: Long
        get() = crc.toLong()

    override fun reset() {
        crc = 0U
    }
}