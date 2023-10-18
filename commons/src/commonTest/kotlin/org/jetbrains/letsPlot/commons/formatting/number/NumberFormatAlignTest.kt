/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatAlignTest {
    @Test
    fun alignLeft() {
        assertEquals("0", NumberFormat("<1,d").apply(0))
        assertEquals("0 ", NumberFormat("<2,d").apply(0))
        assertEquals("0  ", NumberFormat("<3,d").apply(0))
        assertEquals("0    ", NumberFormat("<5,d").apply(0))
        assertEquals("0       ", NumberFormat("<8,d").apply(0))
        assertEquals("0            ", NumberFormat("<13,d").apply(0))
        assertEquals("0                    ", NumberFormat("<21,d").apply(0))
    }

    @Test
    fun alignRight() {
        assertEquals("0", NumberFormat(">1,d").apply(0))
        assertEquals(" 0", NumberFormat(">2,d").apply(0))
        assertEquals("  0", NumberFormat(">3,d").apply(0))
        assertEquals("    0", NumberFormat(">5,d").apply(0))
        assertEquals("       0", NumberFormat(">8,d").apply(0))
        assertEquals("            0", NumberFormat(">13,d").apply(0))
        assertEquals("                    0", NumberFormat(">21,d").apply(0))
        assertEquals("                1,000", NumberFormat(">21,d").apply(1000))
        assertEquals("                1·\\(10^{21}\\)", NumberFormat(">21,d").apply(1e21))
    }

    @Test
    fun alignCenter() {
        assertEquals("0", NumberFormat("^1,d").apply(0))
        assertEquals("0 ", NumberFormat("^2,d").apply(0))
        assertEquals(" 0 ", NumberFormat("^3,d").apply(0))
        assertEquals("  0  ", NumberFormat("^5,d").apply(0))
        assertEquals("   0    ", NumberFormat("^8,d").apply(0))
        assertEquals("      0      ", NumberFormat("^13,d").apply(0))
        assertEquals("          0          ", NumberFormat("^21,d").apply(0))
        assertEquals("        1,000        ", NumberFormat("^21,d").apply(1000))
        assertEquals("        1·\\(10^{21}\\)        ", NumberFormat("^21,d").apply(1e21))
    }
}