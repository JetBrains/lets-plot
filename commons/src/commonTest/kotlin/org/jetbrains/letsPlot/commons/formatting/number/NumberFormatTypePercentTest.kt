/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatTypePercentTest {
    @Test
    fun percentage() {
        val f = NumberFormat(".0%")
        assertEquals("0%", f.apply(0))
        assertEquals("4%", f.apply(0.042))
        assertEquals("42%", f.apply(0.42))
        assertEquals("420%", f.apply(4.2))
        assertEquals("-4%", f.apply(-0.042))
        assertEquals("-42%", f.apply(-0.42))
        assertEquals("-420%", f.apply(-4.2))
    }

    @Test
    fun withPrecision() {
        assertEquals("23.4%", NumberFormat(".1%").apply(.234))
        assertEquals("23.40%", NumberFormat(".2%").apply(.234))
    }

    @Test
    fun withFill() {
        assertEquals("0000000000000004200%", NumberFormat("020.0%").apply(42))
        assertEquals("               4200%", NumberFormat("20.0%").apply(42))
    }

    @Test
    fun alignCenter() {
        assertEquals("         42%         ", NumberFormat("^21.0%").apply(.42))
        assertEquals("       42,200%       ", NumberFormat("^21,.0%").apply(422))
        assertEquals("      -42,200%       ", NumberFormat("^21,.0%").apply(-422))
    }
}