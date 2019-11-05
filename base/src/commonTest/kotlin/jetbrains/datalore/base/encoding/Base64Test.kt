/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.encoding

import kotlin.test.Test
import kotlin.test.fail

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



    fun assertEquals(a: ByteArray, b: ByteArray) {
        val diff = a
            .zip(b)
            .mapIndexed { i, pair -> Pair(i, pair)}
            .filter { (_, pair) -> pair.first != pair.second }

        if (diff.isNotEmpty()) {
            fail(diff.joinToString { entry -> "[${entry.first}]: ${entry.second.first} != ${entry.second.second}" })
        }
    }
}