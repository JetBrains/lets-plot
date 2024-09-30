/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.ExponentFormat
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatTypeETest {
    @Test
    fun canOutputExponentNotation() {
        val f = NumberFormat("e")
        assertEquals("0.000000", f.apply(0))
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
    fun canFormatNegativeZeroAsZero() {
        assertEquals("0.000000", NumberFormat("1e").apply(-0))
        assertEquals("-1.000000e-12", NumberFormat("1e").apply(-1e-12))
    }

    @Test
    fun canOutputScientificExponentNotation() {
        val f = NumberFormat("e&P")
        assertEquals("0.000000", f.apply(0))
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
    fun compactFormatOfScientificNotation() {
        fun format(exponentFormat: ExponentFormat, limits: Pair<Int, Int>? = null): NumberFormat {
            val limitsStr = limits?.let { "{${it.first},${it.second}}" } ?: ""
            return NumberFormat("&${exponentFormat.symbol}$limitsStr")
        }

        //
        // Default limits
        //

        // 10^n

        assertEquals("1e-7", format(ExponentFormat.E).apply(0.0000001))
        assertEquals("\\(10^{-7}\\)", format(ExponentFormat.POW).apply(0.0000001))
        assertEquals("1·\\(10^{-7}\\)", format(ExponentFormat.POW_FULL).apply(0.0000001))

        assertEquals("0.000001", format(ExponentFormat.E).apply(0.000001))
        assertEquals("0.000001", format(ExponentFormat.POW).apply(0.000001))
        assertEquals("0.000001", format(ExponentFormat.POW_FULL).apply(0.000001))

        assertEquals("0.1", format(ExponentFormat.E).apply(0.1))
        assertEquals("0.1", format(ExponentFormat.POW).apply(0.1))
        assertEquals("0.1", format(ExponentFormat.POW_FULL).apply(0.1))

        assertEquals("1", format(ExponentFormat.E).apply(1))
        assertEquals("1", format(ExponentFormat.POW).apply(1))
        assertEquals("1", format(ExponentFormat.POW_FULL).apply(1))

        assertEquals("10", format(ExponentFormat.E).apply(10))
        assertEquals("10", format(ExponentFormat.POW).apply(10))
        assertEquals("10", format(ExponentFormat.POW_FULL).apply(10))

        assertEquals("100000", format(ExponentFormat.E).apply(100000))
        assertEquals("100000", format(ExponentFormat.POW).apply(100000))
        assertEquals("100000", format(ExponentFormat.POW_FULL).apply(100000))

        assertEquals("1e+6", format(ExponentFormat.E).apply(1000000))
        assertEquals("\\(10^{6}\\)", format(ExponentFormat.POW).apply(1000000))
        assertEquals("1·\\(10^{6}\\)", format(ExponentFormat.POW_FULL).apply(1000000))

        // 2*10^n

        assertEquals("2e-7", format(ExponentFormat.E).apply(0.0000002))
        assertEquals("2·\\(10^{-7}\\)", format(ExponentFormat.POW).apply(0.0000002))
        assertEquals("2·\\(10^{-7}\\)", format(ExponentFormat.POW_FULL).apply(0.0000002))

        assertEquals("0.000002", format(ExponentFormat.E).apply(0.000002))
        assertEquals("0.000002", format(ExponentFormat.POW).apply(0.000002))
        assertEquals("0.000002", format(ExponentFormat.POW_FULL).apply(0.000002))

        assertEquals("200000", format(ExponentFormat.E).apply(200000))
        assertEquals("200000", format(ExponentFormat.POW).apply(200000))
        assertEquals("200000", format(ExponentFormat.POW_FULL).apply(200000))

        assertEquals("2e+6", format(ExponentFormat.E).apply(2000000))
        assertEquals("2·\\(10^{6}\\)", format(ExponentFormat.POW).apply(2000000))
        assertEquals("2·\\(10^{6}\\)", format(ExponentFormat.POW_FULL).apply(2000000))

        // Negative numbers

        assertEquals("-1e+6", format(ExponentFormat.E).apply(-1000000))
        assertEquals("-\\(10^{6}\\)", format(ExponentFormat.POW).apply(-1000000))
        assertEquals("-1·\\(10^{6}\\)", format(ExponentFormat.POW_FULL).apply(-1000000))

        assertEquals("-1e-7", format(ExponentFormat.E).apply(-0.0000001))
        assertEquals("-\\(10^{-7}\\)", format(ExponentFormat.POW).apply(-0.0000001))
        assertEquals("-1·\\(10^{-7}\\)", format(ExponentFormat.POW_FULL).apply(-0.0000001))

        assertEquals("-2e+6", format(ExponentFormat.E).apply(-2000000))
        assertEquals("-2·\\(10^{6}\\)", format(ExponentFormat.POW).apply(-2000000))
        assertEquals("-2·\\(10^{6}\\)", format(ExponentFormat.POW_FULL).apply(-2000000))

        assertEquals("-2e-7", format(ExponentFormat.E).apply(-0.0000002))
        assertEquals("-2·\\(10^{-7}\\)", format(ExponentFormat.POW).apply(-0.0000002))
        assertEquals("-2·\\(10^{-7}\\)", format(ExponentFormat.POW_FULL).apply(-0.0000002))

        //
        // Limits: (-2, 3)
        //

        // 10^n

        assertEquals("1e-2", format(ExponentFormat.E, -2 to 3).apply(0.01))
        assertEquals("\\(10^{-2}\\)", format(ExponentFormat.POW, -2 to 3).apply(0.01))
        assertEquals("1·\\(10^{-2}\\)", format(ExponentFormat.POW_FULL, -2 to 3).apply(0.01))

        assertEquals("0.1", format(ExponentFormat.E, -2 to 3).apply(0.1))
        assertEquals("0.1", format(ExponentFormat.POW, -2 to 3).apply(0.1))
        assertEquals("0.1", format(ExponentFormat.POW_FULL, -2 to 3).apply(0.1))

        assertEquals("1", format(ExponentFormat.E, -2 to 3).apply(1))
        assertEquals("1", format(ExponentFormat.POW, -2 to 3).apply(1))
        assertEquals("1", format(ExponentFormat.POW_FULL, -2 to 3).apply(1))

        assertEquals("10", format(ExponentFormat.E, -2 to 3).apply(10))
        assertEquals("10", format(ExponentFormat.POW, -2 to 3).apply(10))
        assertEquals("10", format(ExponentFormat.POW_FULL, -2 to 3).apply(10))

        assertEquals("100", format(ExponentFormat.E, -2 to 3).apply(100))
        assertEquals("100", format(ExponentFormat.POW, -2 to 3).apply(100))
        assertEquals("100", format(ExponentFormat.POW_FULL, -2 to 3).apply(100))

        assertEquals("1e+3", format(ExponentFormat.E, -2 to 3).apply(1000))
        assertEquals("\\(10^{3}\\)", format(ExponentFormat.POW, -2 to 3).apply(1000))
        assertEquals("1·\\(10^{3}\\)", format(ExponentFormat.POW_FULL, -2 to 3).apply(1000))

        // 2*10^n

        assertEquals("2e-2", format(ExponentFormat.E, -2 to 3).apply(0.02))
        assertEquals("2·\\(10^{-2}\\)", format(ExponentFormat.POW, -2 to 3).apply(0.02))
        assertEquals("2·\\(10^{-2}\\)", format(ExponentFormat.POW_FULL, -2 to 3).apply(0.02))

        assertEquals("0.2", format(ExponentFormat.E, -2 to 3).apply(0.2))
        assertEquals("0.2", format(ExponentFormat.POW, -2 to 3).apply(0.2))
        assertEquals("0.2", format(ExponentFormat.POW_FULL, -2 to 3).apply(0.2))

        assertEquals("200", format(ExponentFormat.E, -2 to 3).apply(200))
        assertEquals("200", format(ExponentFormat.POW, -2 to 3).apply(200))
        assertEquals("200", format(ExponentFormat.POW_FULL, -2 to 3).apply(200))

        assertEquals("2e+3", format(ExponentFormat.E, -2 to 3).apply(2000))
        assertEquals("2·\\(10^{3}\\)", format(ExponentFormat.POW, -2 to 3).apply(2000))
        assertEquals("2·\\(10^{3}\\)", format(ExponentFormat.POW_FULL, -2 to 3).apply(2000))

        //
        // Limits: (0, 0)
        //

        assertEquals("1e-1", format(ExponentFormat.E, 0 to 0).apply(0.1))
        assertEquals("\\(10^{-1}\\)", format(ExponentFormat.POW, 0 to 0).apply(0.1))
        assertEquals("1·\\(10^{-1}\\)", format(ExponentFormat.POW_FULL, 0 to 0).apply(0.1))

        assertEquals("1e+0", format(ExponentFormat.E, 0 to 0).apply(1))
        assertEquals("\\(10^{0}\\)", format(ExponentFormat.POW, 0 to 0).apply(1))
        assertEquals("1·\\(10^{0}\\)", format(ExponentFormat.POW_FULL, 0 to 0).apply(1))

        assertEquals("1e+1", format(ExponentFormat.E, 0 to 0).apply(10))
        assertEquals("\\(10^{1}\\)", format(ExponentFormat.POW, 0 to 0).apply(10))
        assertEquals("1·\\(10^{1}\\)", format(ExponentFormat.POW_FULL, 0 to 0).apply(10))

        assertEquals("2e-1", format(ExponentFormat.E, 0 to 0).apply(0.2))
        assertEquals("2·\\(10^{-1}\\)", format(ExponentFormat.POW, 0 to 0).apply(0.2))
        assertEquals("2·\\(10^{-1}\\)", format(ExponentFormat.POW_FULL, 0 to 0).apply(0.2))

        assertEquals("2e+0", format(ExponentFormat.E, 0 to 0).apply(2))
        assertEquals("2·\\(10^{0}\\)", format(ExponentFormat.POW, 0 to 0).apply(2))
        assertEquals("2·\\(10^{0}\\)", format(ExponentFormat.POW_FULL, 0 to 0).apply(2))

        assertEquals("2e+1", format(ExponentFormat.E, 0 to 0).apply(20))
        assertEquals("2·\\(10^{1}\\)", format(ExponentFormat.POW, 0 to 0).apply(20))
        assertEquals("2·\\(10^{1}\\)", format(ExponentFormat.POW_FULL, 0 to 0).apply(20))
    }
}