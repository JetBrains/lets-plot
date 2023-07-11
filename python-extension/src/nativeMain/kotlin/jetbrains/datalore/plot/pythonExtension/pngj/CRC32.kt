/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.pythonExtension.pngj

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