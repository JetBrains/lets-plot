/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatTypeGTest {
    private fun format(spec: String): NumberFormat = NumberFormat(spec, false)

    @Test
    fun gToE() {
        // Not yet enough digits to use exponential notation
        assertEquals("0.000001", format("g").apply(1.0e-6))
        assertEquals("123456", format("g").apply(123456))

        // Enough digits to use exponential notation
        assertEquals("1e-7", format("g").apply(1.0e-7))
        assertEquals("1.23457e+6", format("g").apply(1234567))

        // Rounding
        assertEquals("1.23457e+8", format("g").apply(123456789))
        assertEquals("1.456e-7", format("g").apply(1.456e-7))

        // Rounding with precision
        assertEquals("1.23e+8", format(".3g").apply(123456789))
        assertEquals("1.23e-7", format(".3g").apply(1.23456789e-7))
        assertEquals("1.2346e-7", format(".5g").apply(1.23456789e-7))
    }

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
        val f = format(",.12g")
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