/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatTypeNoneTest {
    @Test
    fun usesSignificantPrecisionAndTrimsInsignificantZeros() {
        //assertEquals("5", Format(".1").apply(4.9))
        assertEquals("0.5", NumberFormat(".1").apply(0.49))
        assertEquals("4.9", NumberFormat(".2").apply(4.9))
        assertEquals("0.49", NumberFormat(".2").apply(0.49))
        assertEquals("0.45", NumberFormat(".2").apply(0.449))
        assertEquals("4.9", NumberFormat(".3").apply(4.9))
        assertEquals("0.49", NumberFormat(".3").apply(0.49))
        assertEquals("0.449", NumberFormat(".3").apply(0.449))
        assertEquals("0.445", NumberFormat(".3").apply(0.4449))
        assertEquals("0.44445", NumberFormat(".5").apply(0.444449))
    }

    @Test
    fun doesNotTrimSignificantZeros() {
        assertEquals("10", NumberFormat(".5").apply(10))
        assertEquals("100", NumberFormat(".5").apply(100))
        assertEquals("1000", NumberFormat(".5").apply(1000))
        assertEquals("21010", NumberFormat(".5").apply(21010))
        assertEquals("1.1", NumberFormat(".5").apply(1.10001))
        assertEquals("1.1·\\(10^{6}\\)", NumberFormat(".5").apply(1.10001e6))
        assertEquals("1.10001", NumberFormat(".6").apply(1.10001))
        assertEquals("1.10001·\\(10^{6}\\)", NumberFormat(".6").apply(1.10001e6))
    }

    @Test
    fun alsoTrimsDecimalPointIfThereAreOnlyInsignificantZeros() {
        assertEquals("1", NumberFormat(".5").apply(1.00001))
        assertEquals("1·\\(10^{6}\\)", NumberFormat(".5").apply(1.00001e6))
        assertEquals("1.00001", NumberFormat(".6").apply(1.00001))
        assertEquals("1.00001·\\(10^{6}\\)", NumberFormat(".6").apply(1.00001e6))
    }

    @Test
    fun canOutputCurrency() {
        val f = NumberFormat("$")
        assertEquals("$0", f.apply(0))
        assertEquals("$0.042", f.apply(.042))
        assertEquals("$0.42", f.apply(.42))
        assertEquals("$4.2", f.apply(4.2))
        assertEquals("-$0.042", f.apply(-.042))
        assertEquals("-$0.42", f.apply(-.42))
        assertEquals("-$4.2", f.apply(-4.2))
    }


    @Test
    fun canFormatNegativeZeroAsZero() {
        assertEquals("0", NumberFormat("").apply(-0))
    }
}