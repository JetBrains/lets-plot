/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.enums

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EnumsTest {

    @Test
    fun enumParsing() {
        assertEquals(TestEnum.A, Enums.valueOf<TestEnum>("aaa"))
    }

    @Test
    fun illegalArgument() {
        assertFailsWith<IllegalArgumentException> {
            Enums.valueOf<TestEnum>("A")
        }
    }

    internal enum class TestEnum {
        A {
            override fun toString(): String {
                return "aaa"
            }
        },

        B {
            override fun toString(): String {
                return "bbb"
            }
        }
    }

}
