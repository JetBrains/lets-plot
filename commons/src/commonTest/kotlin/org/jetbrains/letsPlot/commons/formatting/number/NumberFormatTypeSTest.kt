/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatTypeSTest {
    @Test
    fun withDefaultPrecision() {
        val f = NumberFormat("s")
        assertEquals("0.00000", f.apply(0))
        assertEquals("1.00000", f.apply(1))
        assertEquals("10.0000", f.apply(10))
        assertEquals("100.000", f.apply(100))
        assertEquals("999.500", f.apply(999.5))
        assertEquals("999.500k", f.apply(999500))
        assertEquals("1.00000k", f.apply(1000))
        assertEquals("100.000", f.apply(100))
        assertEquals("1.40000k", f.apply(1400))
        assertEquals("1.50050k", f.apply(1500.5))
        assertEquals("10.0000µ", f.apply(.00001))
        assertEquals("1.00000µ", f.apply(.000001))
    }

    @Test
    fun withPrecision() {
        val f1 = NumberFormat(".3s")
        assertEquals("1.99M", f1.apply(1992000.5))
        assertEquals("1.99k", f1.apply(1992.5))
        assertEquals("2.00k", f1.apply(1999.5))
        assertEquals("1.00k", f1.apply(999.5))
        assertEquals("1.00µ", f1.apply(.000001))
        assertEquals("10.0", f1.apply(10))
        assertEquals("0.00", f1.apply(0))
        assertEquals("100", f1.apply(100))
        assertEquals("1.00", f1.apply(1))
        assertEquals("1.00M", f1.apply(999500))
        assertEquals("1.00k", f1.apply(1000))
        assertEquals("1.50k", f1.apply(1500.5))
        assertEquals("146M", f1.apply(145500000))
        assertEquals("146M", f1.apply(145999999.999999347))
        assertEquals("100Y", f1.apply(1e26))
        assertEquals("10.0m", f1.apply(.009995))
        val f2 = NumberFormat(".4s")
        assertEquals("999.5", f2.apply(999.5))
        assertEquals("999.5k", f2.apply(999500))
        assertEquals("9.995m", f2.apply(.009995))
    }

    @Test
    fun verySmallWithYocto() {
        val f = NumberFormat(".8s")
        assertEquals("0.0000013y", f.apply(1.29e-30)) // Note: rounded!
        assertEquals("0.0000129y", f.apply(1.29e-29))
        assertEquals("0.0001290y", f.apply(1.29e-28))
        assertEquals("0.0012900y", f.apply(1.29e-27))
        assertEquals("0.0129000y", f.apply(1.29e-26))
        assertEquals("0.1290000y", f.apply(1.29e-25))
        assertEquals("1.2900000y", f.apply(1.29e-24))
        assertEquals("12.900000y", f.apply(1.29e-23))
        assertEquals("129.00000y", f.apply(1.29e-22))
        assertEquals("1.2900000z", f.apply(1.29e-21))
        assertEquals("-0.0000013y", f.apply(-1.29e-30)) // Note: rounded!
        assertEquals("-0.0000129y", f.apply(-1.29e-29))
        assertEquals("-0.0001290y", f.apply(-1.29e-28))
        assertEquals("-0.0012900y", f.apply(-1.29e-27))
        assertEquals("-0.0129000y", f.apply(-1.29e-26))
        assertEquals("-0.1290000y", f.apply(-1.29e-25))
        assertEquals("-1.2900000y", f.apply(-1.29e-24))
        assertEquals("-12.900000y", f.apply(-1.29e-23))
        assertEquals("-129.00000y", f.apply(-1.29e-22))
        assertEquals("-1.2900000z", f.apply(-1.29e-21))
    }

    @Test
    fun veryLargeWithYotta() {
        val f = NumberFormat(".8s")
        assertEquals("1230.0000Y", f.apply(1.23e+27))
        assertEquals("1.2300000Z", f.apply(1.23e+21))
        assertEquals("12.300000Z", f.apply(1.23e+22))
        assertEquals("123.00000Z", f.apply(1.23e+23))
        assertEquals("1.2300000Y", f.apply(1.23e+24))
        assertEquals("12.300000Y", f.apply(1.23e+25))
        assertEquals("123.00000Y", f.apply(1.23e+26))
        assertEquals("12300.000Y", f.apply(1.23e+28))
        assertEquals("123000.00Y", f.apply(1.23e+29))
        assertEquals("1230000.0Y", f.apply(1.23e+30))
        assertEquals("-1.2300000Z", f.apply(-1.23e+21))
        assertEquals("-12.300000Z", f.apply(-1.23e+22))
        assertEquals("-123.00000Z", f.apply(-1.23e+23))
        assertEquals("-1.2300000Y", f.apply(-1.23e+24))
        assertEquals("-12.300000Y", f.apply(-1.23e+25))
        assertEquals("-123.00000Y", f.apply(-1.23e+26))
        assertEquals("-1230.0000Y", f.apply(-1.23e+27))
        assertEquals("-12300.000Y", f.apply(-1.23e+28))
        assertEquals("-123000.00Y", f.apply(-1.23e+29))
        assertEquals("-1230000.0Y", f.apply(-1.23e+30))
    }

    @Test
    fun withCurrency() {
        val f1 = NumberFormat("$.2s")
        assertEquals("$0.0", f1.apply(0))
        assertEquals("$250k", f1.apply(2.5e5))
        assertEquals("-$250M", f1.apply(-2.5e8))
        assertEquals("$250G", f1.apply(2.5e11))
        val f2 = NumberFormat("$.3s")
        assertEquals("$0.00", f2.apply(0))
        assertEquals("$1.00", f2.apply(1))
        assertEquals("$10.0", f2.apply(10))
        assertEquals("$100", f2.apply(100))
        assertEquals("$1.00k", f2.apply(999.5))
        assertEquals("$1.00M", f2.apply(999500))
        assertEquals("$1.00k", f2.apply(1000))
        assertEquals("$1.50k", f2.apply(1500.5))
        assertEquals("$146M", f2.apply(145500000))
        assertEquals("$146M", f2.apply(145999999.999999347))
        assertEquals("$100Y", f2.apply(1e26))
        assertEquals("$1.00µ", f2.apply(.000001))
        assertEquals("$10.0m", f2.apply(.009995))
        val f3 = NumberFormat("$.4s")
        assertEquals("$999.5", f3.apply(999.5))
        assertEquals("$999.5k", f3.apply(999500))
        assertEquals("$9.995m", f3.apply(.009995))
    }

    @Test
    fun consistentForSmallAndLargeNumbers() {
        val f1 = NumberFormat(".0s")
        assertEquals("20µ", f1.apply(1.6e-5))
        assertEquals("10µ", f1.apply(1e-5))
        assertEquals("100µ", f1.apply(1e-4))
        assertEquals("1m", f1.apply(1e-3))
        assertEquals("10m", f1.apply(1e-2))
        assertEquals("100m", f1.apply(1e-1))
        assertEquals("1", f1.apply(1e+0))
        assertEquals("10", f1.apply(1e+1))
        assertEquals("100", f1.apply(1e+2))
        assertEquals("1k", f1.apply(1e+3))
        assertEquals("10k", f1.apply(1e+4))
        assertEquals("100k", f1.apply(1e+5))
        val f2 = NumberFormat(".4s")
        assertEquals("10.00µ", f2.apply(1e-5))
        assertEquals("100.0µ", f2.apply(1e-4))
        assertEquals("1.000m", f2.apply(1e-3))
        assertEquals("10.00m", f2.apply(1e-2))
        assertEquals("100.0m", f2.apply(1e-1))
        assertEquals("1.000", f2.apply(1e+0))
        assertEquals("10.00", f2.apply(1e+1))
        assertEquals("100.0", f2.apply(1e+2))
        assertEquals("1.000k", f2.apply(1e+3))
        assertEquals("10.00k", f2.apply(1e+4))
        assertEquals("100.0k", f2.apply(1e+5))
    }

    @Test
    fun withGroupAndZeroFill() {
        val f = NumberFormat("020,s")
        assertEquals("000,000,000,042.0000", f.apply(42))
        assertEquals("00,000,000,042.0000T", f.apply(42e12))
    }

    @Test
    fun groupForVeryLargeNumber() {
        val f = NumberFormat(",s")
        assertEquals("42,000,000Y", f.apply(42e30))
    }
}