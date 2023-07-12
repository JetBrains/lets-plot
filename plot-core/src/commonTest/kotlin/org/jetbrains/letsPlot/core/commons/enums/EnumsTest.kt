/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.enums

import org.jetbrains.letsPlot.core.commons.enums.Enums
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
