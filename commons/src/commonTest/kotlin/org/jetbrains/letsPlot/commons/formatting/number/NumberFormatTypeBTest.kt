/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatTypeBTest {
    @Test
    fun binary() {
        assertEquals("1010", NumberFormat("b").apply(10))
    }

    @Test
    fun binaryWithPrefix() {
        assertEquals("0b1010", NumberFormat("#b").apply(10))
    }
}