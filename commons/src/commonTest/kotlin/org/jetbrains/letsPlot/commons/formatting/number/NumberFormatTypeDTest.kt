/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatTypeDTest {
    private fun format(spec: String): NumberFormat = NumberFormat(spec)

    @Test
    fun alwaysUsesZeroPrecision() {
        val f = format(".2d")
        assertEquals("0", f.apply(0))
        assertEquals("42", f.apply(42))
        assertEquals("-4", f.apply(-4.2))
    }

    @Test
    fun roundsNonIntegers() {
        assertEquals("4", format("d").apply(4.2))
    }

    @Test
    fun format49_9() {
        assertEquals("50", format("d").apply(49.9))
    }

    @Test
    fun groupThousands() {
        assertEquals("0", format("01,d").apply(0))
        assertEquals("0", format("01,d").apply(0))
        assertEquals("00", format("02,d").apply(0))
        assertEquals("000", format("03,d").apply(0))
        assertEquals("0,000", format("04,d").apply(0))
        assertEquals("0,000", format("05,d").apply(0))
        assertEquals("00,000", format("06,d").apply(0))
        assertEquals("0,000,000", format("08,d").apply(0))
        assertEquals("0,000,000,000", format("013,d").apply(0))
        assertEquals("0,000,000,000,000,000", format("021,d").apply(0))
        assertEquals("-0,042,000,000", format("013,d").apply(-42000000))
        assertEquals("1,000,000,000,000,000,000,000", format("012,d").apply(1e21))
        assertEquals("1,000,000,000,000,000,000,000", format("013,d").apply(1e21))
        assertEquals("1,000,000,000,000,000,000,000", format("014,d").apply(1e21))
        assertEquals("1,000,000,000,000,000,000,000", format("015,d").apply(1e21))
    }

    @Test
    fun groupThousandsAndZeroFillWithOverflow() {
        assertEquals("1", format("01,d").apply(1))
        assertEquals("1", format("01,d").apply(1))
        assertEquals("12", format("02,d").apply(12))
        assertEquals("123", format("03,d").apply(123))
        assertEquals("12,345", format("05,d").apply(12345))
        assertEquals("12,345,678", format("08,d").apply(12345678))
        assertEquals("1,234,567,890,123", format("013,d").apply(1234567890123))
    }

    @Test
    fun groupThousandsAndSpaceFill() {
        assertEquals("0", format("1,d").apply(0))
        assertEquals("0", format("1,d").apply(0))
        assertEquals(" 0", format("2,d").apply(0))
        assertEquals("  0", format("3,d").apply(0))
        assertEquals("    0", format("5,d").apply(0))
        assertEquals("       0", format("8,d").apply(0))
        assertEquals("            0", format("13,d").apply(0))
        assertEquals("                    0", format("21,d").apply(0))
    }

    @Test
    fun groupThousandsAndSpaceFillWithOverflow() {
        assertEquals("1", format("1,d").apply(1))
        assertEquals("12", format("2,d").apply(12))
        assertEquals("123", format("3,d").apply(123))
        assertEquals("12,345", format("5,d").apply(12345))
        assertEquals("12,345,678", format("8,d").apply(12345678))
        assertEquals("1,234,567,890,123", format("13,d").apply(1234567890123))
    }

    @Test
    fun padAfterSignWithCurrency() {
        assertEquals("+$0", format("=+$1,d").apply(0))
        assertEquals("+$0", format("=+$1,d").apply(0))
        assertEquals("+$0", format("=+$2,d").apply(0))
        assertEquals("+$0", format("=+$3,d").apply(0))
        assertEquals("+$  0", format("=+$5,d").apply(0))
        assertEquals("+$     0", format("=+$8,d").apply(0))
        assertEquals("+$          0", format("=+$13,d").apply(0))
        assertEquals("+$                  0", format("=+$21,d").apply(0))
        assertEquals("+\$1,000,000,000,000,000,000,000", format("=+$21,d").apply(1e21))
    }

    @Test
    fun aSpaceCanDenotePositiveNumbers() {
        assertEquals("-1", format(" 1,d").apply(-1))
        assertEquals(" 0", format(" 1,d").apply(0))
        assertEquals(" 0", format(" 2,d").apply(0))
        assertEquals("  0", format(" 3,d").apply(0))
        assertEquals("    0", format(" 5,d").apply(0))
        assertEquals("       0", format(" 8,d").apply(0))
        assertEquals("            0", format(" 13,d").apply(0))
        assertEquals("                    0", format(" 21,d").apply(0))
        assertEquals(" 1,000,000,000,000,000,000,000", format(" 21,d").apply(1e21))
    }

    @Test
    fun formatNegativeZeroAsZero() {
        assertEquals("0", format("1d").apply(-0))
        assertEquals("0", format("1d").apply(-1e-12))
    }
}