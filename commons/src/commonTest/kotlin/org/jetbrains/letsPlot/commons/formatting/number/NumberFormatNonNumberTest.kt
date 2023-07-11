/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatNonNumberTest {
    @Test
    fun nonNumber() {
        val f = NumberFormat("d")
        assertEquals("NaN", f.apply(Double.NaN))
        assertEquals("+Infinity", f.apply(Double.POSITIVE_INFINITY))
        assertEquals("-Infinity", f.apply(Double.NEGATIVE_INFINITY))
    }
}