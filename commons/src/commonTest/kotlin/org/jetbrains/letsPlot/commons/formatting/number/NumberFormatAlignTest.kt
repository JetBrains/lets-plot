/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatAlignTest {
    private fun format(spec: String): NumberFormat = NumberFormat(spec)

    @Test
    fun alignLeft() {
        assertEquals("0", format("<1,d").apply(0))
        assertEquals("0 ", format("<2,d").apply(0))
        assertEquals("0  ", format("<3,d").apply(0))
        assertEquals("0    ", format("<5,d").apply(0))
        assertEquals("0       ", format("<8,d").apply(0))
        assertEquals("0            ", format("<13,d").apply(0))
        assertEquals("0                    ", format("<21,d").apply(0))
        assertEquals("1,000                ", format("<21,d").apply(1000))
        assertEquals("1,000,000,000,000,000,000,000", format("<21,d").apply(1e21))
        assertEquals("0                    ", format("<21,d").apply(1e-21))
        assertEquals("1,000,000,000,000,000,000,000", format("<21,d&P").apply(1e21))
        assertEquals("0                    ", format("<21,d&P").apply(1e-21))
        assertEquals("1,000,000,000,000,000,000,000", format("<21,d&F").apply(1e21))
        assertEquals("0                    ", format("<21,d&F").apply(1e-21))
    }

    @Test
    fun alignRight() {
        assertEquals("0", format(">1,d").apply(0))
        assertEquals(" 0", format(">2,d").apply(0))
        assertEquals("  0", format(">3,d").apply(0))
        assertEquals("    0", format(">5,d").apply(0))
        assertEquals("       0", format(">8,d").apply(0))
        assertEquals("            0", format(">13,d").apply(0))
        assertEquals("                    0", format(">21,d").apply(0))
        assertEquals("                1,000", format(">21,d").apply(1000))
        assertEquals("1,000,000,000,000,000,000,000", format(">21,d").apply(1e21))
        assertEquals("                    0", format(">21,d").apply(1e-21))
        assertEquals("1,000,000,000,000,000,000,000", format(">21,d&P").apply(1e21))
        assertEquals("                    0", format(">21,d&P").apply(1e-21))
        assertEquals("1,000,000,000,000,000,000,000", format(">21,d&F").apply(1e21))
        assertEquals("                    0", format(">21,d&F").apply(1e-21))
    }

    @Test
    fun alignCenter() {
        assertEquals("0", format("^1,d").apply(0))
        assertEquals("0 ", format("^2,d").apply(0))
        assertEquals(" 0 ", format("^3,d").apply(0))
        assertEquals("  0  ", format("^5,d").apply(0))
        assertEquals("   0    ", format("^8,d").apply(0))
        assertEquals("      0      ", format("^13,d").apply(0))
        assertEquals("          0          ", format("^21,d").apply(0))
        assertEquals("        1,000        ", format("^21,d").apply(1000))
        assertEquals("1,000,000,000,000,000,000,000", format("^21,d").apply(1e21))
        assertEquals("          0          ", format("^21,d").apply(1e-21))
        assertEquals("1,000,000,000,000,000,000,000", format("^21,d&P").apply(1e21))
        assertEquals("          0          ", format("^21,d&P").apply(1e-21))
        assertEquals("1,000,000,000,000,000,000,000", format("^21,d&F").apply(1e21))
        assertEquals("          0          ", format("^21,d&F").apply(1e-21))
    }

    @Test
    fun overflowTest() {
        assertEquals("1,000,000,000,000,000,000,000", format(">21,d").apply(1e21))
    }
}