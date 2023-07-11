/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.encoding

import kotlin.test.Test
import kotlin.test.assertEquals

class Base64Test {

    @Test
    fun emptyString() {
        assertEquals(
            byteArrayOf(),
            Base64.decode("")
        )
    }

    @Test
    fun binaryData() {
        assertEquals(
            byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f),
            Base64.decode("AQIDBAUGBwgJCgsMDQ4P")
        )
    }

    @Test
    fun newLineDelimiter() {
        assertEquals(
            byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f),
            Base64.decode("AQIDBAUGBw\ngJCgsMDQ4P")
        )
    }

    @Test
    fun encode() {
        assertEquals(
            "AQIDBA==",
            Base64.encode(
                byteArrayOf(0x01, 0x02, 0x03, 0x04)
            )
        )
    }
    @Test
    fun emptyBuffer() {
        val str = ""
        val data = "".toByteArray()
        assertEquals(str, Base64.encode(data))
        assertEquals(data, Base64.decode(str))
    }

    @Test
    fun f() {
        val str = "Zg=="
        val data = "f".toByteArray()
        assertEquals(str, Base64.encode(data))
        assertEquals(data, Base64.decode(str))
    }

    @Test
    fun fo() {
        val str = "Zm8="
        val data = "fo".toByteArray()
        assertEquals(str, Base64.encode(data))
        assertEquals(data, Base64.decode(str))
    }

    @Test
    fun foo() {
        val str = "Zm9v"
        val data = "foo".toByteArray()
        assertEquals(str, Base64.encode(data))
        assertEquals(data, Base64.decode(str))
    }

    @Test
    fun foob() {
        val str = "Zm9vYg=="
        val data = "foob".toByteArray()
        assertEquals(str, Base64.encode(data))
        assertEquals(data, Base64.decode(str))
    }

    @Test
    fun fooba() {
        val str = "Zm9vYmE="
        val data = "fooba".toByteArray()
        assertEquals(str, Base64.encode(data))
        assertEquals(data, Base64.decode(str))
    }

    @Test
    fun foobar() {
        val data = "foobar".toByteArray()
        val str = "Zm9vYmFy"
        assertEquals(str, Base64.encode(data))
        assertEquals(data, Base64.decode(str))
    }

    @Test
    fun bunchEncode() {
        assertEquals("AA==", Base64.encode(byteArray(0)))
        assertEquals("AAA=", Base64.encode(byteArray(0, 0)))
        assertEquals("AAAA", Base64.encode(byteArray(0, 0, 0)))
        assertEquals("/+8=", Base64.encode(byteArray(0xff, 0xef)))
    }

    @Test
    fun bunchDecode() {
        assertEquals(byteArray(0), Base64.decode("AA=="))
        assertEquals(byteArray(0, 0), Base64.decode("AAA="))
        assertEquals(byteArray(0, 0, 0), Base64.decode("AAAA"))
        assertEquals(byteArray(0xff, 0xef), Base64.decode("/+8="))
    }

    @Test
    fun skipNonBase64Symbols() {
        val str = "Zm9v\nYmFy"
        assertEquals("foobar".toByteArray(), Base64.decode(str))
    }

    private fun byteArray(vararg a : Int): ByteArray = a.toList().map(Int::toByte).toByteArray()
    private fun assertEquals(expected: ByteArray, actual: ByteArray) {
        assertEquals(expected.toList(), actual.toList())
    }

    private fun String.toByteArray(): ByteArray {
        val array = ByteArray(length)
        forEachIndexed { i, char -> array[i] = char.code.toByte() }
        return array
    }
}
