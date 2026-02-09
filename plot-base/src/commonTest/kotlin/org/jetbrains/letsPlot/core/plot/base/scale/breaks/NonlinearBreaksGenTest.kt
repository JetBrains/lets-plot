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

class NonlinearBreaksGenTest {

    @Test
    fun log10GeneratesBreaksAtPowersOfTen() {
        val gen = NonlinearBreaksGen(
            transform = Transforms.LOG10,
            expFormat = DEF_EXPONENT_FORMAT
        )
        val domain = DoubleSpan(1.0, 10000.0)

        val scaleBreaks = gen.generateBreaks(domain, targetCount = 5)

        val breaks = scaleBreaks.domainValues.map { it as Double }
        assertTrue(breaks.contains(1.0), "Should contain 1")
        assertTrue(breaks.contains(10.0), "Should contain 10")
        assertTrue(breaks.contains(100.0), "Should contain 100")
        assertTrue(breaks.contains(1000.0), "Should contain 1000")
        assertTrue(breaks.contains(10000.0), "Should contain 10000")

        val expectedLabels = listOf("1", "10", "100", "1,000", "10,000")
        assertEquals(expectedLabels, scaleBreaks.labels)
    }

    @Test
    fun log2GeneratesBreaksAtPowersOfTwo() {
        val gen = NonlinearBreaksGen(
            transform = Transforms.LOG2,
            expFormat = DEF_EXPONENT_FORMAT
        )
        val domain = DoubleSpan(1.0, 16.0)

        val scaleBreaks = gen.generateBreaks(domain, targetCount = 5)

        val breaks = scaleBreaks.domainValues.map { it as Double }
        assertTrue(breaks.contains(1.0), "Should contain 1")
        assertTrue(breaks.contains(2.0), "Should contain 2")
        assertTrue(breaks.contains(4.0), "Should contain 4")
        assertTrue(breaks.contains(8.0), "Should contain 8")
        assertTrue(breaks.contains(16.0), "Should contain 16")

        val expectedLabels = listOf("1", "2", "4", "8", "16")
        assertEquals(expectedLabels, scaleBreaks.labels)
    }

    @Test
    fun sqrtGeneratesBreaks() {
        val gen = NonlinearBreaksGen(
            transform = Transforms.SQRT,
            expFormat = DEF_EXPONENT_FORMAT
        )
        val domain = DoubleSpan(0.0, 100.0)

        val scaleBreaks = gen.generateBreaks(domain, targetCount = 5)

        val breaks = scaleBreaks.domainValues.map { it as Double }
        assertTrue(breaks.isNotEmpty())
        assertTrue(breaks.first() >= 0.0)
        assertTrue(breaks.last() <= 100.0)
    }

    @Test
    fun usesProvidedFormatter() {
        val customFormatter: (Any) -> String = { "custom" }
        val gen = NonlinearBreaksGen(
            transform = Transforms.LOG10,
            providedFormatter = customFormatter,
            expFormat = DEF_EXPONENT_FORMAT
        )
        val domain = DoubleSpan(1.0, 1000.0)
        val scaleBreaks = gen.generateBreaks(domain, targetCount = 5)
        assertTrue(scaleBreaks.labels.all { it == "custom" })
    }
}
