/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatTypeFTest : NumberFormatTest {
    @Test
    fun canOutputSmallNumberAndGroupThousands() {
        assertEquals("0.00", format(",.2f").apply(9.104303610446121E-38))
    }

    @Test
    fun edgeCases() {
        assertEquals("2.0", format(",.1f").apply(1.9999999999999998))
    }

    @Test
    fun canOutputFixedPointNotation() {
        assertEquals("0.000270", format("f").apply(2.6985974025974023E-4))
        assertEquals("0.5", format(".1f").apply(0.49))
        assertEquals("0.45", format(".2f").apply(0.449))
        assertEquals("0.445", format(".3f").apply(0.4449))
        assertEquals("0.44445", format(".5f").apply(0.444449))
        assertEquals("100.0", format(".1f").apply(100))
        assertEquals("100.00", format(".2f").apply(100))
        assertEquals("100.000", format(".3f").apply(100))
        assertEquals("100.00000", format(".5f").apply(100))
    }

    @Test
    fun canOutputCurrencyWithCommaGroupingAndSign() {
        val f = format("+$,.2f")
        assertEquals("+$0.00", f.apply(0))
        assertEquals("+$0.43", f.apply(0.429))
        assertEquals("-$0.43", f.apply(-0.429))
        assertEquals("-$1.00", f.apply(-1))
        assertEquals("+$10,000.00", f.apply(1e4))
    }

    @Test
    fun canGroupThousandsSpaceFillAndRoundToSignificantDigits() {
        assertEquals(" 123,456.5", format("10,.1f").apply(123456.49))
        assertEquals("1,234,567.45", format("10,.2f").apply(1234567.449))
        assertEquals("12,345,678.445", format("10,.3f").apply(12345678.4449))
        assertEquals("123,456,789.44445", format("10,.5f").apply(123456789.444449))
        assertEquals(" 123,456.0", format("10,.1f").apply(123456))
        assertEquals("1,234,567.00", format("10,.2f").apply(1234567))
        assertEquals("12,345,678.000", format("10,.3f").apply(12345678))
        assertEquals("123,456,789.00000", format("10,.5f").apply(123456789))
    }

    @Test
    fun canDisplayIntegersInFixedPointNotation() {
        assertEquals("42.000000", format("f").apply(42))
        assertEquals("42.000000", format("f").apply(42))
    }

    @Test
    fun canFormatNegativeZerosAsZeros() {
        assertEquals("0.000000", format("f").apply(-0))
        assertEquals("0.000000", format("f").apply(-1e-12))
    }
}