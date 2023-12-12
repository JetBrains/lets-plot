/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertEquals

class NumericBreakFormatterTest {
    @Suppress("PrivatePropertyName")
    private val TYPE_S_MAX = 1e26 // see NumberFormat.TYPE_S_MAX

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
                min = -TYPE_S_MAX,
                max = 0.0,
                metricPrefix = true
            )
        )

        assertEquals(
            listOf("0", "25Y", "50Y", "75Y", "100Y"),
            formatRange(
                min = 0.0,
                max = TYPE_S_MAX,
                metricPrefix = true
            )
        )

        assertEquals(
            listOf("-100Y", "-50Y", "0", "50Y", "100Y"),
            formatRange(
                min = -TYPE_S_MAX,
                max = TYPE_S_MAX,
                metricPrefix = true
            )
        )
    }

    @Test
    fun formatExtremesTypeE() {
        assertEquals(
            listOf("-1.80·\\(10^{308}\\)", "-1.35·\\(10^{308}\\)", "-8.99·\\(10^{307}\\)", "-4.49·\\(10^{307}\\)", "0"),
            formatRange(
                min = -Double.MAX_VALUE,
                max = 0.0,
                metricPrefix = false
            )
        )

        assertEquals(
            listOf("0", "4.49·\\(10^{307}\\)", "8.99·\\(10^{307}\\)", "1.35·\\(10^{308}\\)", "1.80·\\(10^{308}\\)"),
            formatRange(
                min = 0.0,
                max = Double.MAX_VALUE,
                metricPrefix = false
            )
        )

        assertEquals(
            listOf("-8.99·\\(10^{307}\\)", "-4.49·\\(10^{307}\\)", "0", "4.49·\\(10^{307}\\)", "8.99·\\(10^{307}\\)"),
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