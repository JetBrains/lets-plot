/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.ExponentNotationType
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatTypeGTest {
    @Test
    fun gToE() {
        // Not yet enough digits to use exponential notation
        assertEquals("0.00000100000", format("g").apply(1.0e-6))
        assertEquals("123456", format("g").apply(123456))
        assertEquals("0.100000", format("g{-2,3}").apply(0.1))
        assertEquals("123.000", format("g{-2,3}").apply(123))

        // Enough digits to use exponential notation
        assertEquals("1.00000e-7", format("g").apply(1.0e-7))
        assertEquals("1.00000e-7", format("g&E").apply(1.0e-7))
        assertEquals("\\(10^{-7}\\)", format("~g&P").apply(1.0e-7))
        assertEquals("1·\\(10^{-7}\\)", format("~g&F").apply(1.0e-7))
        assertEquals("1.23457e+6", format("g").apply(1234567))
        assertEquals("1.00000e-2", format("g{-2,3}").apply(0.01))
        assertEquals("1.23400e+3", format("g{-2,3}").apply(1234))

        // Rounding
        assertEquals("1.23457e+8", format("g").apply(123456789))
        assertEquals("1.45600e-7", format("g").apply(1.456e-7))

        // Rounding with precision
        assertEquals("1.23e+8", format(".3g").apply(123456789))
        assertEquals("1.23e-7", format(".3g").apply(1.23456789e-7))
        assertEquals("1.2346e-7", format(".5g").apply(1.23456789e-7))
    }

    @Test
    fun canOutputGeneralNotation() {
        assertEquals("0.000269860", format("g").apply(2.6985974025974023E-4))
        assertEquals("0.05", format(".1g").apply(0.049))
        assertEquals("0.5", format(".1g").apply(0.49))
        assertEquals("0.45", format(".2g").apply(0.449))
        assertEquals("0.445", format(".3g").apply(0.4449))
        assertEquals("0.44445", format(".5g").apply(0.444449))
        assertEquals("1e+2", format(".1g").apply(100))
        assertEquals("1.0e+2", format(".2g").apply(100))
        assertEquals("100", format(".3g").apply(100))
        assertEquals("100.00", format(".5g").apply(100))
        assertEquals("100.20", format(".5g").apply(100.2))
        assertEquals("0.0020", format(".2g").apply(0.002))
    }

    @Test
    fun canGroupThousandsWithGeneralNotation() {
        val format = format(",.12g")::apply
        val formatTruncated = format(",.12~g")::apply
        
        0.let {
            assertEquals("0", formatTruncated(it))
            assertEquals("0.00000000000", format(it))
        }
        
        42.let {
            assertEquals("42", formatTruncated(it))
            assertEquals("42.0000000000", format(it))
        }

        42_000_000.let {
            assertEquals("42,000,000", formatTruncated(it))
            assertEquals("42,000,000.0000", format(it))
        }

        420_000_000.let {
            assertEquals("420,000,000", formatTruncated(it))
            assertEquals("420,000,000.000", format(it))
        }

        (-4).let {
            assertEquals("-4", formatTruncated(it))
            assertEquals("-4.00000000000", format(it))
        }

        (-42).let {
            assertEquals("-42", formatTruncated(it))
            assertEquals("-42.0000000000", format(it))
        }

        (-4_200_000).let {
            assertEquals("-4,200,000", formatTruncated(it))
            assertEquals("-4,200,000.00000", format(it))
        }

        (-42_000_000).let {
            assertEquals("-42,000,000", formatTruncated(it))
            assertEquals("-42,000,000.0000", format(it))
        }
    }

    @Test
    fun compactFormatOfScientificNotation() {
        fun format(expType: ExponentNotationType, limits: Pair<Int, Int>? = null): NumberFormat {
            val limitsStr = limits?.let { "{${it.first},${it.second}}" } ?: ""
            return NumberFormat("~&${expType.symbol}$limitsStr")
        }

        //
        // Default limits
        //

        // 10^n

        assertEquals("1e-7", format(ExponentNotationType.E).apply(0.0000001))
        assertEquals("\\(10^{-7}\\)", format(ExponentNotationType.POW).apply(0.0000001))
        assertEquals("1·\\(10^{-7}\\)", format(ExponentNotationType.POW_FULL).apply(0.0000001))

        assertEquals("0.000001", format(ExponentNotationType.E).apply(0.000001))
        assertEquals("0.000001", format(ExponentNotationType.POW).apply(0.000001))
        assertEquals("0.000001", format(ExponentNotationType.POW_FULL).apply(0.000001))

        assertEquals("0.1", format(ExponentNotationType.E).apply(0.1))
        assertEquals("0.1", format(ExponentNotationType.POW).apply(0.1))
        assertEquals("0.1", format(ExponentNotationType.POW_FULL).apply(0.1))

        assertEquals("1", format(ExponentNotationType.E).apply(1))
        assertEquals("1", format(ExponentNotationType.POW).apply(1))
        assertEquals("1", format(ExponentNotationType.POW_FULL).apply(1))

        assertEquals("10", format(ExponentNotationType.E).apply(10))
        assertEquals("10", format(ExponentNotationType.POW).apply(10))
        assertEquals("10", format(ExponentNotationType.POW_FULL).apply(10))

        assertEquals("100000", format(ExponentNotationType.E).apply(100000))
        assertEquals("100000", format(ExponentNotationType.POW).apply(100000))
        assertEquals("100000", format(ExponentNotationType.POW_FULL).apply(100000))

        assertEquals("1e+6", format(ExponentNotationType.E).apply(1000000))
        assertEquals("\\(10^{6}\\)", format(ExponentNotationType.POW).apply(1000000))
        assertEquals("1·\\(10^{6}\\)", format(ExponentNotationType.POW_FULL).apply(1000000))

        // 2*10^n

        assertEquals("2e-7", format(ExponentNotationType.E).apply(0.0000002))
        assertEquals("2·\\(10^{-7}\\)", format(ExponentNotationType.POW).apply(0.0000002))
        assertEquals("2·\\(10^{-7}\\)", format(ExponentNotationType.POW_FULL).apply(0.0000002))

        assertEquals("0.000002", format(ExponentNotationType.E).apply(0.000002))
        assertEquals("0.000002", format(ExponentNotationType.POW).apply(0.000002))
        assertEquals("0.000002", format(ExponentNotationType.POW_FULL).apply(0.000002))

        assertEquals("200000", format(ExponentNotationType.E).apply(200000))
        assertEquals("200000", format(ExponentNotationType.POW).apply(200000))
        assertEquals("200000", format(ExponentNotationType.POW_FULL).apply(200000))

        assertEquals("2e+6", format(ExponentNotationType.E).apply(2000000))
        assertEquals("2·\\(10^{6}\\)", format(ExponentNotationType.POW).apply(2000000))
        assertEquals("2·\\(10^{6}\\)", format(ExponentNotationType.POW_FULL).apply(2000000))

        // Negative numbers

        assertEquals("-1e+6", format(ExponentNotationType.E).apply(-1000000))
        assertEquals("-\\(10^{6}\\)", format(ExponentNotationType.POW).apply(-1000000))
        assertEquals("-1·\\(10^{6}\\)", format(ExponentNotationType.POW_FULL).apply(-1000000))

        assertEquals("-1e-7", format(ExponentNotationType.E).apply(-0.0000001))
        assertEquals("-\\(10^{-7}\\)", format(ExponentNotationType.POW).apply(-0.0000001))
        assertEquals("-1·\\(10^{-7}\\)", format(ExponentNotationType.POW_FULL).apply(-0.0000001))

        assertEquals("-2e+6", format(ExponentNotationType.E).apply(-2000000))
        assertEquals("-2·\\(10^{6}\\)", format(ExponentNotationType.POW).apply(-2000000))
        assertEquals("-2·\\(10^{6}\\)", format(ExponentNotationType.POW_FULL).apply(-2000000))

        assertEquals("-2e-7", format(ExponentNotationType.E).apply(-0.0000002))
        assertEquals("-2·\\(10^{-7}\\)", format(ExponentNotationType.POW).apply(-0.0000002))
        assertEquals("-2·\\(10^{-7}\\)", format(ExponentNotationType.POW_FULL).apply(-0.0000002))

        //
        // Limits: (-2, 3)
        //

        // 10^n

        assertEquals("1e-2", format(ExponentNotationType.E, -2 to 3).apply(0.01))
        assertEquals("\\(10^{-2}\\)", format(ExponentNotationType.POW, -2 to 3).apply(0.01))
        assertEquals("1·\\(10^{-2}\\)", format(ExponentNotationType.POW_FULL, -2 to 3).apply(0.01))

        assertEquals("0.1", format(ExponentNotationType.E, -2 to 3).apply(0.1))
        assertEquals("0.1", format(ExponentNotationType.POW, -2 to 3).apply(0.1))
        assertEquals("0.1", format(ExponentNotationType.POW_FULL, -2 to 3).apply(0.1))

        assertEquals("1", format(ExponentNotationType.E, -2 to 3).apply(1))
        assertEquals("1", format(ExponentNotationType.POW, -2 to 3).apply(1))
        assertEquals("1", format(ExponentNotationType.POW_FULL, -2 to 3).apply(1))

        assertEquals("10", format(ExponentNotationType.E, -2 to 3).apply(10))
        assertEquals("10", format(ExponentNotationType.POW, -2 to 3).apply(10))
        assertEquals("10", format(ExponentNotationType.POW_FULL, -2 to 3).apply(10))

        assertEquals("100", format(ExponentNotationType.E, -2 to 3).apply(100))
        assertEquals("100", format(ExponentNotationType.POW, -2 to 3).apply(100))
        assertEquals("100", format(ExponentNotationType.POW_FULL, -2 to 3).apply(100))

        assertEquals("1e+3", format(ExponentNotationType.E, -2 to 3).apply(1000))
        assertEquals("\\(10^{3}\\)", format(ExponentNotationType.POW, -2 to 3).apply(1000))
        assertEquals("1·\\(10^{3}\\)", format(ExponentNotationType.POW_FULL, -2 to 3).apply(1000))

        // 2*10^n

        assertEquals("2e-2", format(ExponentNotationType.E, -2 to 3).apply(0.02))
        assertEquals("2·\\(10^{-2}\\)", format(ExponentNotationType.POW, -2 to 3).apply(0.02))
        assertEquals("2·\\(10^{-2}\\)", format(ExponentNotationType.POW_FULL, -2 to 3).apply(0.02))

        assertEquals("0.2", format(ExponentNotationType.E, -2 to 3).apply(0.2))
        assertEquals("0.2", format(ExponentNotationType.POW, -2 to 3).apply(0.2))
        assertEquals("0.2", format(ExponentNotationType.POW_FULL, -2 to 3).apply(0.2))

        assertEquals("200", format(ExponentNotationType.E, -2 to 3).apply(200))
        assertEquals("200", format(ExponentNotationType.POW, -2 to 3).apply(200))
        assertEquals("200", format(ExponentNotationType.POW_FULL, -2 to 3).apply(200))

        assertEquals("2e+3", format(ExponentNotationType.E, -2 to 3).apply(2000))
        assertEquals("2·\\(10^{3}\\)", format(ExponentNotationType.POW, -2 to 3).apply(2000))
        assertEquals("2·\\(10^{3}\\)", format(ExponentNotationType.POW_FULL, -2 to 3).apply(2000))

        //
        // Limits: (0, 0)
        //

        assertEquals("1e-1", format(ExponentNotationType.E, 0 to 0).apply(0.1))
        assertEquals("\\(10^{-1}\\)", format(ExponentNotationType.POW, 0 to 0).apply(0.1))
        assertEquals("1·\\(10^{-1}\\)", format(ExponentNotationType.POW_FULL, 0 to 0).apply(0.1))

        assertEquals("1e+0", format(ExponentNotationType.E, 0 to 0).apply(1))
        assertEquals("\\(10^{0}\\)", format(ExponentNotationType.POW, 0 to 0).apply(1))
        assertEquals("1·\\(10^{0}\\)", format(ExponentNotationType.POW_FULL, 0 to 0).apply(1))

        assertEquals("1e+1", format(ExponentNotationType.E, 0 to 0).apply(10))
        assertEquals("\\(10^{1}\\)", format(ExponentNotationType.POW, 0 to 0).apply(10))
        assertEquals("1·\\(10^{1}\\)", format(ExponentNotationType.POW_FULL, 0 to 0).apply(10))

        assertEquals("2e-1", format(ExponentNotationType.E, 0 to 0).apply(0.2))
        assertEquals("2·\\(10^{-1}\\)", format(ExponentNotationType.POW, 0 to 0).apply(0.2))
        assertEquals("2·\\(10^{-1}\\)", format(ExponentNotationType.POW_FULL, 0 to 0).apply(0.2))

        assertEquals("2e+0", format(ExponentNotationType.E, 0 to 0).apply(2))
        assertEquals("2·\\(10^{0}\\)", format(ExponentNotationType.POW, 0 to 0).apply(2))
        assertEquals("2·\\(10^{0}\\)", format(ExponentNotationType.POW_FULL, 0 to 0).apply(2))

        assertEquals("2e+1", format(ExponentNotationType.E, 0 to 0).apply(20))
        assertEquals("2·\\(10^{1}\\)", format(ExponentNotationType.POW, 0 to 0).apply(20))
        assertEquals("2·\\(10^{1}\\)", format(ExponentNotationType.POW_FULL, 0 to 0).apply(20))
    }


    @Test
    fun minExpPrecision() {
        assertEquals("6e-10", format(6e-10, ".0g{-10,}"))
        assertEquals("0.0000000006", format(6e-10, ".0g{-11,}"))
        assertEquals("0.0000000006", format(6e-10, ".1g{-11,}"))
        assertEquals("0.00000000060", format(6e-10, ".2g{-11,}"))

        assertEquals("1", format(1.000000006, ".0g"))
        assertEquals("1", format(1.000000006, ".0g{-10,}")) // "1e-9" ?
        assertEquals("1", format(1.000000006, ".0g{-11,}")) // "1e-10" ?
    }

    @Test
    fun decimalWithWholePartWithDifferentPrecision() {
        val number = 1.0006
        assertEquals("1.00060", format(number, "g"))
        assertEquals("1", format(number, ".0g"))
        assertEquals("1", format(number, ".1g"))
        assertEquals("1.0", format(number, ".2g"))
        assertEquals("1.00", format(number, ".3g"))
        assertEquals("1.001", format(number, ".4g"))
        assertEquals("1.0006", format(number, ".5g"))
        assertEquals("1.00060", format(number, ".6g"))
    }

    @Test
    fun decimalWithTwoDigitsWholePartWithDifferentPrecision() {
        val number = 21.0006
        assertEquals("21.0006", format(number, "g"))
        assertEquals("2e+1", format(number, ".0g"))
        assertEquals("2e+1", format(number, ".1g"))
        assertEquals("21", format(number, ".2g"))
        assertEquals("21.0", format(number, ".3g"))
        assertEquals("21.00", format(number, ".4g"))
        assertEquals("21.001", format(number, ".5g"))
        assertEquals("21.0006", format(number, ".6g"))
    }

    @Test
    fun p0() {
        val format = format(".0g")::apply
        assertEquals("0", format(0.0))
        assertEquals("5e-13", format(0.000_000_000_000_5))
        assertEquals("6e-13", format(0.000_000_000_000_55))
        assertEquals("6e-13", format(0.000_000_000_000_555))
        assertEquals("0.005", format(0.005))
        assertEquals("0.05", format(0.05))
        assertEquals("5", format(5.0))
        assertEquals("5e+1", format(50.0))
        assertEquals("6e+1", format(55.0))
        assertEquals("5e+2", format(500.0))
        assertEquals("5e+2", format(505.0))
        assertEquals("6e+2", format(550.0))
        assertEquals("6e+2", format(555.0))
    }

    @Test
    fun p1() {
        val format = format(".1g")::apply
        assertEquals("0", format(0.0))
        assertEquals("5e-13", format(0.000_000_000_000_5))
        assertEquals("6e-13", format(0.000_000_000_000_55))
        assertEquals("6e-13", format(0.000_000_000_000_555))
        assertEquals("0.005", format(0.005))
        assertEquals("0.05", format(0.05))
        assertEquals("5", format(5.0))
        assertEquals("5e+1", format(50.0))
        assertEquals("6e+1", format(55.0))
        assertEquals("5e+2", format(500.0))
        assertEquals("5e+2", format(505.0))
        assertEquals("6e+2", format(550.0))
        assertEquals("6e+2", format(555.0))
    }

    @Test
    fun decimalWithoutWholePartWithDifferentPrecision() {
        val number = 0.000006
        assertEquals("0.00000600000", format(number, "g"))
        assertEquals("0.000006", format(number, ".0g"))
        assertEquals("0.000006", format(number, ".1g"))
        assertEquals("0.0000060", format(number, ".2g"))
        assertEquals("0.00000600", format(number, ".3g"))
        assertEquals("0.000006000", format(number, ".4g"))
        assertEquals("0.0000060000", format(number, ".5g"))
        assertEquals("0.00000600000", format(number, ".6g"))
    }

    @Test
    fun zeroWithPrecision() {
        assertEquals("0.00000", format(0.0, "g"))
        assertEquals("0", format(0.0, ".0g"))
        assertEquals("0", format(0.0, ".1g"))
        assertEquals("0.0", format(0.0, ".2g"))
        assertEquals("0.00", format(0.0, ".3g"))
        assertEquals("0.000", format(0.0, ".4g"))
        assertEquals("0.0000", format(0.0, ".5g"))
    }

    @Test
    fun round_9_9999999eMINUS9() {
        assertEquals("1e-8", format(9.9999999e-9, "~g"))
    }
}