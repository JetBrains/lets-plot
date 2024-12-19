/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatTypeFTest {
    @Test
    fun canOutputSmallNumberAndGroupThousands() {
        assertEquals("0.00", NumberFormat(",.2f").apply(9.104303610446121E-38))
    }

    @Test
    fun edgeCases() {
        assertEquals("2.0", NumberFormat(",.1f").apply(1.9999999999999998))
    }

    @Test
    fun canOutputFixedPointNotation() {
        assertEquals("1.000", NumberFormat(".3f").apply(0.999500))
        assertEquals("0.5", NumberFormat(".1f").apply(0.49))
        assertEquals("0.000270", NumberFormat("f").apply(2.6985974025974023E-4))
        assertEquals("0.45", NumberFormat(".2f").apply(0.449))
        assertEquals("0.445", NumberFormat(".3f").apply(0.4449))
        assertEquals("0.44445", NumberFormat(".5f").apply(0.444449))
        assertEquals("100.0", NumberFormat(".1f").apply(100))
        assertEquals("100.00", NumberFormat(".2f").apply(100))
        assertEquals("100.000", NumberFormat(".3f").apply(100))
        assertEquals("100.00000", NumberFormat(".5f").apply(100))
    }

    @Test
    fun canOutputCurrencyWithCommaGroupingAndSign() {
        val f = NumberFormat("+$,.2f")
        assertEquals("+$0.00", f.apply(0))
        assertEquals("+$0.43", f.apply(0.429))
        assertEquals("-$0.43", f.apply(-0.429))
        assertEquals("-$1.00", f.apply(-1))
        assertEquals("+$10,000.00", f.apply(1e4))
    }

    @Test
    fun canGroupThousandsSpaceFillAndRoundToSignificantDigits() {
        assertEquals("12,345,678.445", NumberFormat("10,.3f").apply(12345678.4449))
        assertEquals(" 123,456.5", NumberFormat("10,.1f").apply(123456.49))
        assertEquals("1,234,567.45", NumberFormat("10,.2f").apply(1234567.449))
        assertEquals("123,456,789.44445", NumberFormat("10,.5f").apply(123456789.444449))
        assertEquals(" 123,456.0", NumberFormat("10,.1f").apply(123456))
        assertEquals("1,234,567.00", NumberFormat("10,.2f").apply(1234567))
        assertEquals("12,345,678.000", NumberFormat("10,.3f").apply(12345678))
        assertEquals("123,456,789.00000", NumberFormat("10,.5f").apply(123456789))
    }

    @Test
    fun canDisplayIntegersInFixedPointNotation() {
        assertEquals("42.000000", NumberFormat("f").apply(42))
        assertEquals("42.000000", NumberFormat("f").apply(42))
    }

    @Test
    fun canFormatNegativeZerosAsZeros() {
        assertEquals("0.000000", NumberFormat("f").apply(-0))
        assertEquals("0.000000", NumberFormat("f").apply(-1e-12))
    }

    @Test
    fun carryToWholePart() {
        assertEquals("9.00", NumberFormat(".2f").apply(8.999))
        assertEquals("10.00", NumberFormat(".2f").apply(9.999))
        assertEquals("10.0", NumberFormat(".1f").apply(9.999))
        assertEquals("1000000.000", NumberFormat(".3f").apply(999999.9999))
    }

    @Test
    fun verySmallNumberWithCarry() {
        val number = 9.995e-17

        assertEquals("0.00000000000000009995", format(".20f", number))
        assertEquals("0.0000000000000001000", format(".19f", number))
        assertEquals("0.000000000000000100", format(".18f", number))
        assertEquals("0.00000000000000010", format(".17f", number))
        assertEquals("0.0000000000000001", format(".16f", number))
        assertEquals("0.000000000000000", format(".15f", number))
        assertEquals("0.000000", format("f", number))
    }

    @Test
    fun verySmallNumberWithoutCarry() {
        val number = 1.234e-5

        assertEquals("0.0000", format(".4f", number))
        assertEquals("0.00001234", format(".8f", number))
        assertEquals("0.0000123", format(".7f", number))
        assertEquals("0.000012", format(".6f", number))
        assertEquals("0.00001", format(".5f", number))
        assertEquals("0.000", format(".3f", number))
        assertEquals("0.000012", format("f", number))
    }

    @Test
    fun veryBigNumber() {
        val number = 9.995e17

        assertEquals("999500000000000000.000000", format("f", number))
        assertEquals("999500000000000000.00000000000000000000", format(".20f", number))
        assertEquals("999500000000000000", format(".0f", number))
    }

}