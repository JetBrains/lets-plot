/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.ExponentFormat.Companion.DEF_EXPONENT_FORMAT
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LinearFixedBreaksGenTest {

    @Test
    fun breaksAtIntegerIntervals() {
        val gen = LinearFixedBreaksGen(
            breakWidth = 2.0,
            expFormat = DEF_EXPONENT_FORMAT
        )
        val domain = DoubleSpan(0.0, 10.0)
        val scaleBreaks = gen.generateBreaks(domain, targetCount = 10)

        val breaks = scaleBreaks.domainValues.map { it as Double }
        assertEquals(listOf(0.0, 2.0, 4.0, 6.0, 8.0, 10.0), breaks)

        val expectedLabels = listOf("0", "2", "4", "6", "8", "10")
        assertEquals(expectedLabels, scaleBreaks.labels)
    }

    @Test
    fun breaksAtFractionalIntervals() {
        val gen = LinearFixedBreaksGen(
            breakWidth = 0.25,
            expFormat = DEF_EXPONENT_FORMAT
        )
        val domain = DoubleSpan(0.0, 1.0)
        val scaleBreaks = gen.generateBreaks(domain, targetCount = 10)

        val breaks = scaleBreaks.domainValues.map { it as Double }
        assertEquals(5, breaks.size)
        assertEquals(0.0, breaks[0], 1e-10)
        assertEquals(0.25, breaks[1], 1e-10)
        assertEquals(0.5, breaks[2], 1e-10)
        assertEquals(0.75, breaks[3], 1e-10)
        assertEquals(1.0, breaks[4], 1e-10)

        val expectedLabels = listOf("0", "0.25", "0.5", "0.75", "1")
        assertEquals(expectedLabels, scaleBreaks.labels)
    }

    @Test
    fun breaksNotAlignedToStart() {
        val gen = LinearFixedBreaksGen(
            breakWidth = 5.0,
            expFormat = DEF_EXPONENT_FORMAT
        )
        val domain = DoubleSpan(3.0, 22.0)
        val scaleBreaks = gen.generateBreaks(domain, targetCount = 10)

        val breaks = scaleBreaks.domainValues.map { it as Double }
        assertEquals(listOf(5.0, 10.0, 15.0, 20.0), breaks)
    }

    @Test
    fun breaksAreMarkedAsFixed() {
        val gen = LinearFixedBreaksGen(
            breakWidth = 1.0,
            expFormat = DEF_EXPONENT_FORMAT
        )
        val domain = DoubleSpan(0.0, 5.0)
        val scaleBreaks = gen.generateBreaks(domain, targetCount = 10)

        assertTrue(scaleBreaks.fixed, "Breaks should be marked as fixed")
        assertTrue(gen.fixedBreakWidth, "Generator should report fixed break width")
    }

    @Test
    fun usesProvidedFormatter() {
        val customFormatter: (Any) -> String = { "custom" }
        val gen = LinearFixedBreaksGen(
            breakWidth = 1.0,
            providedFormatter = customFormatter,
            expFormat = DEF_EXPONENT_FORMAT
        )
        val domain = DoubleSpan(0.0, 3.0)
        val scaleBreaks = gen.generateBreaks(domain, targetCount = 10)

        assertTrue(scaleBreaks.labels.all { it == "custom" })
    }

    @Test
    fun negativeRange() {
        val gen = LinearFixedBreaksGen(
            breakWidth = 5.0,
            expFormat = DEF_EXPONENT_FORMAT
        )
        val domain = DoubleSpan(-20.0, -5.0)
        val scaleBreaks = gen.generateBreaks(domain, targetCount = 10)

        val breaks = scaleBreaks.domainValues.map { it as Double }
        assertEquals(listOf(-20.0, -15.0, -10.0, -5.0), breaks)

        val expectedLabels = listOf("-20", "-15", "-10", "-5")
        assertEquals(expectedLabels, scaleBreaks.labels)
    }
}
