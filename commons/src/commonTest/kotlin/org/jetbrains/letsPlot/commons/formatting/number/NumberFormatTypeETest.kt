/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatTypeETest {

    @Test
    fun canOutputExponentNotation() {
        val f = NumberFormat("e")
        assertEquals("0.000000", f.apply(0))
        assertEquals("4.200000·10", f.apply(42))
        assertEquals("4.200000·\\(10^{7}\\)", f.apply(42000000))
        assertEquals("4.200000·\\(10^{8}\\)", f.apply(420000000))
        assertEquals("-4.000000·1", f.apply(-4))
        assertEquals("-4.200000·10", f.apply(-42))
        assertEquals("-4.200000·\\(10^{6}\\)", f.apply(-4200000))
        assertEquals("-4.200000·\\(10^{7}\\)", f.apply(-42000000))

        assertEquals("4·10", NumberFormat(".0e").apply(42))
        assertEquals("4.200·10", NumberFormat(".3e").apply(42))
    }

    @Test
    fun canFormatNegativeZeroAsZero() {
        assertEquals("0.000000", NumberFormat("1e").apply(-0))
        assertEquals("-1.000000·\\(10^{-12}\\)", NumberFormat("1e").apply(-1e-12))
    }
}