/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatTypeDTest {
    @Test
    fun alwaysUsesZeroPrecision() {
        val f = NumberFormat(".2d")
        assertEquals("0", f.apply(0))
        assertEquals("42", f.apply(42))
        assertEquals("-4", f.apply(-4.2))
    }

    @Test
    fun roundsNonIntegers() {
        assertEquals("4", NumberFormat("d").apply(4.2))
    }

    @Test
    fun groupThousands() {
        assertEquals("0", NumberFormat("01,d").apply(0))
        assertEquals("0", NumberFormat("01,d").apply(0))
        assertEquals("00", NumberFormat("02,d").apply(0))
        assertEquals("000", NumberFormat("03,d").apply(0))
        assertEquals("0,000", NumberFormat("04,d").apply(0))
        assertEquals("0,000", NumberFormat("05,d").apply(0))
        assertEquals("00,000", NumberFormat("06,d").apply(0))
        assertEquals("0,000,000", NumberFormat("08,d").apply(0))
        assertEquals("0,000,000,000", NumberFormat("013,d").apply(0))
        assertEquals("0,000,000,000,000,000", NumberFormat("021,d").apply(0))
        assertEquals("-0,042,000,000", NumberFormat("013,d").apply(-42000000))
        assertEquals("0,000,001·\\(10^{21}\\)", NumberFormat("012,d").apply(1e21))
        assertEquals("0,000,001·\\(10^{21}\\)", NumberFormat("013,d").apply(1e21))
        assertEquals("00,000,001·\\(10^{21}\\)", NumberFormat("014,d").apply(1e21))
        assertEquals("000,000,001·\\(10^{21}\\)", NumberFormat("015,d").apply(1e21))
    }

    @Test
    fun groupThousandsAndZeroFillWithOverflow() {
        assertEquals("1", NumberFormat("01,d").apply(1))
        assertEquals("1", NumberFormat("01,d").apply(1))
        assertEquals("12", NumberFormat("02,d").apply(12))
        assertEquals("123", NumberFormat("03,d").apply(123))
        assertEquals("12,345", NumberFormat("05,d").apply(12345))
        assertEquals("12,345,678", NumberFormat("08,d").apply(12345678))
        assertEquals("1,234,567,890,123", NumberFormat("013,d").apply(1234567890123))
    }

    @Test
    fun groupThousandsAndSpaceFill() {
        assertEquals("0", NumberFormat("1,d").apply(0))
        assertEquals("0", NumberFormat("1,d").apply(0))
        assertEquals(" 0", NumberFormat("2,d").apply(0))
        assertEquals("  0", NumberFormat("3,d").apply(0))
        assertEquals("    0", NumberFormat("5,d").apply(0))
        assertEquals("       0", NumberFormat("8,d").apply(0))
        assertEquals("            0", NumberFormat("13,d").apply(0))
        assertEquals("                    0", NumberFormat("21,d").apply(0))
    }

    @Test
    fun groupThousandsAndSpaceFillWithOverflow() {
        assertEquals("1", NumberFormat("1,d").apply(1))
        assertEquals("12", NumberFormat("2,d").apply(12))
        assertEquals("123", NumberFormat("3,d").apply(123))
        assertEquals("12,345", NumberFormat("5,d").apply(12345))
        assertEquals("12,345,678", NumberFormat("8,d").apply(12345678))
        assertEquals("1,234,567,890,123", NumberFormat("13,d").apply(1234567890123))
    }

    @Test
    fun padAfterSignWithCurrency() {
        assertEquals("+$0", NumberFormat("=+$1,d").apply(0))
        assertEquals("+$0", NumberFormat("=+$1,d").apply(0))
        assertEquals("+$0", NumberFormat("=+$2,d").apply(0))
        assertEquals("+$0", NumberFormat("=+$3,d").apply(0))
        assertEquals("+$  0", NumberFormat("=+$5,d").apply(0))
        assertEquals("+$     0", NumberFormat("=+$8,d").apply(0))
        assertEquals("+$          0", NumberFormat("=+$13,d").apply(0))
        assertEquals("+$                  0", NumberFormat("=+$21,d").apply(0))
        assertEquals("+$              1·\\(10^{21}\\)", NumberFormat("=+$21,d").apply(1e21))
    }

    @Test
    fun aSpaceCanDenotePositiveNumbers() {
        assertEquals("-1", NumberFormat(" 1,d").apply(-1))
        assertEquals(" 0", NumberFormat(" 1,d").apply(0))
        assertEquals(" 0", NumberFormat(" 2,d").apply(0))
        assertEquals("  0", NumberFormat(" 3,d").apply(0))
        assertEquals("    0", NumberFormat(" 5,d").apply(0))
        assertEquals("       0", NumberFormat(" 8,d").apply(0))
        assertEquals("            0", NumberFormat(" 13,d").apply(0))
        assertEquals("                    0", NumberFormat(" 21,d").apply(0))
        assertEquals("                1·\\(10^{21}\\)", NumberFormat(" 21,d").apply(1e21))
    }

    @Test
    fun formatNegativeZeroAsZero() {
        assertEquals("0", NumberFormat("1d").apply(-0))
        assertEquals("0", NumberFormat("1d").apply(-1e-12))
    }
}