/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertEquals

class NumericBreakFormatterTest {
    @Test
    fun formatZero() {
        val formatter = NumericBreakFormatter(0.0, 0.0, true)
        assertEquals("0.0", formatter.apply(0))
    }

    @Test
    fun formatExtremesTypeS() {
        assertEquals(
            listOf("-100Y", "-75Y", "-50Y", "-25Y", "0"),
            formatRange(
                min = -NumberFormat.TYPE_S_MAX,
                max = 0.0,
                metricPrefix = true
            )
        )

        assertEquals(
            listOf("0", "25Y", "50Y", "75Y", "100Y"),
            formatRange(
                min = 0.0,
                max = NumberFormat.TYPE_S_MAX,
                metricPrefix = true
            )
        )

        assertEquals(
            listOf("-100Y", "-50Y", "0", "50Y", "100Y"),
            formatRange(
                min = -NumberFormat.TYPE_S_MAX,
                max = NumberFormat.TYPE_S_MAX,
                metricPrefix = true
            )
        )
    }

    @Test
    fun formatExtremesTypeE() {
        assertEquals(
            listOf("-1.80e+308", "-1.35e+308", "-8.99e+307", "-4.49e+307", "0"),
            formatRange(
                min = -Double.MAX_VALUE,
                max = 0.0,
                metricPrefix = false
            )
        )

        assertEquals(
            listOf("0", "4.49e+307", "8.99e+307", "1.35e+308", "1.80e+308"),
            formatRange(
                min = 0.0,
                max = Double.MAX_VALUE,
                metricPrefix = false
            )
        )

        assertEquals(
            listOf("-8.99e+307", "-4.49e+307", "0", "4.49e+307", "8.99e+307"),
            formatRange(
                min = -Double.MAX_VALUE / 2,
                max = Double.MAX_VALUE / 2,
                metricPrefix = false
            )
        )
    }

    private fun formatRange(min: Double, max: Double, metricPrefix: Boolean): List<String> {
        val n = 5
        val step = (max - min) / (n - 1)
        val values = List(n) { i -> min(min + i * step, Double.MAX_VALUE) }
        val formatterStep = (max - min) / 100
        val formatters = values.map {
            NumericBreakFormatter(it, formatterStep, metricPrefix)
        }
        return values.mapIndexed { i, v -> formatters[i].apply(v) }
    }
}