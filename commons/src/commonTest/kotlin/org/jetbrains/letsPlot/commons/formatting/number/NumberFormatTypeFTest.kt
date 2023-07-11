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
        assertEquals("0.000270", NumberFormat("f").apply(2.6985974025974023E-4))
        assertEquals("0.5", NumberFormat(".1f").apply(0.49))
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
        assertEquals(" 123,456.5", NumberFormat("10,.1f").apply(123456.49))
        assertEquals("1,234,567.45", NumberFormat("10,.2f").apply(1234567.449))
        assertEquals("12,345,678.445", NumberFormat("10,.3f").apply(12345678.4449))
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
}