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
        assertEquals("0.000000e+0", format("e", 0))
        assertEquals("4.200000e+1", f.apply(42))
        assertEquals("4.200000e+7", f.apply(42000000))
        assertEquals("4.200000e+8", f.apply(420000000))
        assertEquals("-4.000000e+0", f.apply(-4))
        assertEquals("-4.200000e+1", f.apply(-42))
        assertEquals("-4.200000e+6", f.apply(-4200000))
        assertEquals("-4.200000e+7", f.apply(-42000000))

        assertEquals("4e+1", NumberFormat(".0e").apply(42))
        assertEquals("4.200e+1", NumberFormat(".3e").apply(42))
    }

    @Test
    fun canFormatNegative() {
        assertEquals("-1.000000e-12", NumberFormat("1e").apply(-1e-12))
    }

    @Test
    fun canOutputScientificExponentNotation() {
        val f = NumberFormat("e&P")
        //assertEquals("0.000000", f.apply(0))
        assertEquals("1.500000", f.apply(1.5e0))
        assertEquals("1.500000·10", f.apply(1.5e1))
        assertEquals("1.500000·\\(10^{-1}\\)", f.apply(1.5e-1))
        assertEquals("1.500000·\\(10^{2}\\)", f.apply(1.5e2))
        assertEquals("1.500000·\\(10^{-2}\\)", f.apply(1.5e-2))
        assertEquals("1.500000·\\(10^{16}\\)", f.apply(1.5e16))
        assertEquals("1.500000·\\(10^{-16}\\)", f.apply(1.5e-16))
        assertEquals("-1.500000·\\(10^{16}\\)", f.apply(-1.5e16))
        assertEquals("-1.500000·\\(10^{-16}\\)", f.apply(-1.5e-16))
    }

    @Test
    fun trim() {
        assertEquals("1.000000", NumberFormat("e&P").apply(1.0))
        assertEquals("1", NumberFormat("~e&P").apply(1.0))
        assertEquals("10", NumberFormat("~e&P").apply(10.0))
        assertEquals("\\(10^{2}\\)", NumberFormat("~e&P").apply(100.0))
        assertEquals("\\(10^{-1}\\)", NumberFormat("~e&P").apply(0.1))
    }

    @Test
    fun rounding() {
        assertEquals("1.234568e+0", NumberFormat("e").apply(1.23456789))
    }

    @Test
    fun round_9_9999999eMINUS9_precision_6() {
        assertEquals("1.000000e-8", NumberFormat(".6e").apply(9.9999999e-9))
    }

    @Test
    fun round_9_9eMINUS9_precision_0() {
        assertEquals("1e-8", NumberFormat(".0e").apply(9.9e-9))
    }

}
