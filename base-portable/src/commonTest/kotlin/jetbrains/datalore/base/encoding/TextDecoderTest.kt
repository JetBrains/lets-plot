/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.encoding

import kotlin.test.Test
import kotlin.test.assertEquals


class TextDecoderTest {

    private val privetMirUtf8 =
        arrayOf(208, 159, 209, 128, 208, 184, 208, 178, 208, 181, 209, 130, 32, 208, 188, 208, 184, 209, 128)
            .map(Int::toByte)
            .toByteArray()

    @Test
    fun helloWorld() {
        val str = TextDecoder.decode(privetMirUtf8)
        assertEquals("Привет мир", str)
    }
}