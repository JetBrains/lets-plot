/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatTypeNoneTest {
    private val format = { spec: String -> NumberFormat(spec, false) }

    @Test
    fun usesSignificantPrecisionAndTrimsInsignificantZeros() {
        //assertEquals("5", Format(".1").apply(4.9))
        assertEquals("0.5", format(".1").apply(0.49))
        assertEquals("4.9", format(".2").apply(4.9))
        assertEquals("0.49", format(".2").apply(0.49))
        assertEquals("0.45", format(".2").apply(0.449))
        assertEquals("4.9", format(".3").apply(4.9))
        assertEquals("0.49", format(".3").apply(0.49))
        assertEquals("0.449", format(".3").apply(0.449))
        assertEquals("0.445", format(".3").apply(0.4449))
        assertEquals("0.44445", format(".5").apply(0.444449))
    }

    @Test
    fun doesNotTrimSignificantZeros() {
        assertEquals("10", format(".5").apply(10))
        assertEquals("100", format(".5").apply(100))
        assertEquals("1000", format(".5").apply(1000))
        assertEquals("21010", format(".5").apply(21010))
        assertEquals("1.1", format(".5").apply(1.10001))
        assertEquals("1.1e+6", format(".5").apply(1.10001e6))
        assertEquals("1.10001", format(".6").apply(1.10001))
        assertEquals("1.10001e+6", format(".6").apply(1.10001e6))
    }

    @Test
    fun alsoTrimsDecimalPointIfThereAreOnlyInsignificantZeros() {
        assertEquals("1", format(".5").apply(1.00001))
        assertEquals("1e+6", format(".5").apply(1.00001e6))
        assertEquals("1.00001", format(".6").apply(1.00001))
        assertEquals("1.00001e+6", format(".6").apply(1.00001e6))
    }

    @Test
    fun canOutputCurrency() {
        val f = format("$")
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
        assertEquals("0", format("").apply(-0))
    }
}