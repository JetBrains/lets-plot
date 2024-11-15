/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.ExponentNotationType
import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.ExponentFormat
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertEquals

class NumericBreakFormatterTest {
    @Suppress("PrivatePropertyName")
    private val TYPE_S_MAX = 1e26 // see NumberFormat.TYPE_S_MAX

    @Test
    fun formatZero() {
        val formatter = NumericBreakFormatter(0.0, 0.0, true, expFormat = ExponentFormat(ExponentNotationType.E))
        assertEquals("0", formatter.apply(0))
    }

    // TODO
    /*
    @Test
    fun formatExtremesTypeG() {
        assertEquals(
            listOf("-1.80e+308", "-1.35e+308", "-8.99e+307", "-4.49e+307", "0"),
            formatRange(min = -Double.MAX_VALUE, max = 0.0)
        )

        assertEquals(
            listOf("0", "4.49e+307", "8.99e+307", "1.35e+308", "1.80e+308"),
            formatRange(min = 0.0, max = Double.MAX_VALUE)
        )

        assertEquals(
            listOf("-8.99e+307", "-4.49e+307", "0", "4.49e+307", "8.99e+307"),
            formatRange(min = -Double.MAX_VALUE / 2, max = Double.MAX_VALUE / 2)
        )
    }
    */

    private fun formatRange(min: Double, max: Double): List<String> {
        val n = 5
        val step = (max - min) / (n - 1)
        val values = List(n) { i -> min(min + i * step, Double.MAX_VALUE) }
        val formatterStep = (max - min) / 100
        val formatters = values.map {
            NumericBreakFormatter(it, formatterStep, false, expFormat = ExponentFormat(ExponentNotationType.E))
        }
        return values.mapIndexed { i, v -> formatters[i].apply(v) }
    }
}