/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.ExponentFormat.Companion.DEF_EXPONENT_FORMAT
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NonlinearFixedBreaksGenTest {

    @Test
    fun log10WithBreakWidthOneGeneratesEveryPowerOfTen() {
        val gen = NonlinearFixedBreaksGen(
            breakWidth = 1.0,  // Every power of 10 in log10 space
            transform = Transforms.LOG10,
            expFormat = DEF_EXPONENT_FORMAT
        )
        val domain = DoubleSpan(1.0, 10000.0)

        val scaleBreaks = gen.generateBreaks(domain, targetCount = 10)

        val breaks = scaleBreaks.domainValues.map { it as Double }
        assertEquals(5, breaks.size, "Expected 5 breaks: 1, 10, 100, 1000, 10000")
        assertEquals(1.0, breaks[0], 1e-10)
        assertEquals(10.0, breaks[1], 1e-10)
        assertEquals(100.0, breaks[2], 1e-10)
        assertEquals(1000.0, breaks[3], 1e-10)
        assertEquals(10000.0, breaks[4], 1e-10)

        val expectedLabels = listOf("1", "10", "100", "1,000", "10,000")
        assertEquals(expectedLabels, scaleBreaks.labels)
    }

    @Test
    fun log10WithBreakWidthTwoGeneratesEveryOtherPowerOfTen() {
        val gen = NonlinearFixedBreaksGen(
            breakWidth = 2.0,  // Every 2nd power of 10 (1, 100, 10000)
            transform = Transforms.LOG10,
            expFormat = DEF_EXPONENT_FORMAT
        )
        val domain = DoubleSpan(1.0, 10000.0)

        val scaleBreaks = gen.generateBreaks(domain, targetCount = 10)

        val breaks = scaleBreaks.domainValues.map { it as Double }
        assertEquals(3, breaks.size, "Expected 3 breaks: 1, 100, 10000")
        assertEquals(1.0, breaks[0], 1e-10)
        assertEquals(100.0, breaks[1], 1e-10)
        assertEquals(10000.0, breaks[2], 1e-10)

        val expectedLabels = listOf("1", "100", "10,000")
        assertEquals(expectedLabels, scaleBreaks.labels)
    }

    @Test
    fun log2WithBreakWidthOneGeneratesEveryPowerOfTwo() {
        val gen = NonlinearFixedBreaksGen(
            breakWidth = 1.0,  // Every power of 2 in log2 space
            transform = Transforms.LOG2,
            expFormat = DEF_EXPONENT_FORMAT
        )
        val domain = DoubleSpan(1.0, 16.0)

        val scaleBreaks = gen.generateBreaks(domain, targetCount = 10)

        val breaks = scaleBreaks.domainValues.map { it as Double }
        assertEquals(5, breaks.size, "Expected 5 breaks: 1, 2, 4, 8, 16")
        assertEquals(1.0, breaks[0], 1e-10)
        assertEquals(2.0, breaks[1], 1e-10)
        assertEquals(4.0, breaks[2], 1e-10)
        assertEquals(8.0, breaks[3], 1e-10)
        assertEquals(16.0, breaks[4], 1e-10)

        val expectedLabels = listOf("1", "2", "4", "8", "16")
        assertEquals(expectedLabels, scaleBreaks.labels)
    }

    @Test
    fun log2WithBreakWidthTwoGeneratesEveryOtherPowerOfTwo() {
        val gen = NonlinearFixedBreaksGen(
            breakWidth = 2.0,  // Every 2nd power of 2 (1, 4, 16)
            transform = Transforms.LOG2,
            expFormat = DEF_EXPONENT_FORMAT
        )
        val domain = DoubleSpan(1.0, 16.0)

        val scaleBreaks = gen.generateBreaks(domain, targetCount = 10)

        val breaks = scaleBreaks.domainValues.map { it as Double }
        assertEquals(3, breaks.size, "Expected 3 breaks: 1, 4, 16")
        assertEquals(1.0, breaks[0], 1e-10)
        assertEquals(4.0, breaks[1], 1e-10)
        assertEquals(16.0, breaks[2], 1e-10)

        val expectedLabels = listOf("1", "4", "16")
        assertEquals(expectedLabels, scaleBreaks.labels)
    }

    @Test
    fun breaksAreMarkedAsFixed() {
        val gen = NonlinearFixedBreaksGen(
            breakWidth = 1.0,
            transform = Transforms.LOG10,
            expFormat = DEF_EXPONENT_FORMAT
        )
        val domain = DoubleSpan(1.0, 1000.0)

        val scaleBreaks = gen.generateBreaks(domain, targetCount = 10)

        assertTrue(scaleBreaks.fixed, "Breaks should be marked as fixed")
        assertTrue(gen.fixedBreakWidth, "Generator should report fixed break width")
    }

    @Test
    fun usesProvidedFormatter() {
        val customFormatter: (Any) -> String = { "custom" }
        val gen = NonlinearFixedBreaksGen(
            breakWidth = 1.0,
            transform = Transforms.LOG10,
            providedFormatter = customFormatter,
            expFormat = DEF_EXPONENT_FORMAT
        )
        val domain = DoubleSpan(1.0, 1000.0)
        val scaleBreaks = gen.generateBreaks(domain, targetCount = 10)
        assertTrue(scaleBreaks.labels.all { it == "custom" })
    }
}
