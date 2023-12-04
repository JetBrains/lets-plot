/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatTypeGTest {
    private fun format(spec: String): NumberFormat = NumberFormat(spec)

    @Test
    fun canOutputGeneralNotation() {
        assertEquals("0.00026986", format("g").apply(2.6985974025974023E-4))
        assertEquals("0.05", format(".1g").apply(0.049))
        assertEquals("0.5", format(".1g").apply(0.49))
        assertEquals("0.45", format(".2g").apply(0.449))
        assertEquals("0.445", format(".3g").apply(0.4449))
        assertEquals("0.44445", format(".5g").apply(0.444449))
        assertEquals("1e+2", format(".1g").apply(100))
        assertEquals("1e+2", format(".2g").apply(100))
        assertEquals("100", format(".3g").apply(100))
        assertEquals("100", format(".5g").apply(100))
        assertEquals("100.2", format(".5g").apply(100.2))
        assertEquals("0.002", format(".2g").apply(0.002))
    }

    @Test
    fun canGroupThousandsWithGeneralNotation() {
        val f = NumberFormat(",.12g")
        assertEquals("0", f.apply(0))
        assertEquals("42", f.apply(42))
        assertEquals("42,000,000", f.apply(42000000))
        assertEquals("420,000,000", f.apply(420000000))
        assertEquals("-4", f.apply(-4))
        assertEquals("-42", f.apply(-42))
        assertEquals("-4,200,000", f.apply(-4200000))
        assertEquals("-42,000,000", f.apply(-42000000))
    }
}