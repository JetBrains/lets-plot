/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.base64

import java.util.*
import kotlin.collections.ArrayList

actual object BinaryUtil {
    actual fun encodeList(l: List<Double?>): String {
        val bytes = toBytes(l)
        //return BaseEncoding.base64().encode(bytes);
        return Base64.getEncoder().encodeToString(bytes)
    }

    actual fun decodeList(s: String): List<Double> {
        //byte[] bytes = BaseEncoding.base64().decode(s);
        //byte[] bytes = Base64.getDecoder().decode(s); // not emulated by GWT
        val bytes = JavaBase64.decode(s)
        return fromBytes(bytes)
    }

    private fun toBytes(l: List<Double?>): ByteArray {
        val data = ByteArray(l.size * 8)
        var pos = 0
        for (d in l) {
            d?.let {
                toBytes(
                    it,
                    data,
                    pos
                )
            } ?: toBytes(Double.NaN, data, pos)
            pos += 8
        }
        return data
    }

    private fun fromBytes(data: ByteArray): List<Double> {
        val length = data.size / 8
        val l = ArrayList<Double>(length)
        for (i in 0 until length) {
            val v = fromBytes(data, i * 8)
            l.add(v)
        }
        return l
    }

    private fun toBytes(d: Double, data: ByteArray, pos: Int): ByteArray {
        val v = java.lang.Double.doubleToLongBits(d)
        data[pos + 7] = v.toByte()
        data[pos + 6] = v.ushr(8).toByte()
        data[pos + 5] = v.ushr(16).toByte()
        data[pos + 4] = v.ushr(24).toByte()
        data[pos + 3] = v.ushr(32).toByte()
        data[pos + 2] = v.ushr(40).toByte()
        data[pos + 1] = v.ushr(48).toByte()
        data[pos] = v.ushr(56).toByte()
        return data
    }

    private fun fromBytes(data: ByteArray, pos: Int): Double {
        var v = data[pos].toLong()
        v = (v shl 8) + (data[pos + 1].toInt() and 0xff)
        v = (v shl 8) + (data[pos + 2].toInt() and 0xff)
        v = (v shl 8) + (data[pos + 3].toInt() and 0xff)
        v = (v shl 8) + (data[pos + 4].toInt() and 0xff)
        v = (v shl 8) + (data[pos + 5].toInt() and 0xff)
        v = (v shl 8) + (data[pos + 6].toInt() and 0xff)
        v = (v shl 8) + (data[pos + 7].toInt() and 0xff)
        return java.lang.Double.longBitsToDouble(v)
    }
}
