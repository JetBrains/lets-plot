/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.DAY
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.HOUR
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.MINUTE
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TimeBreaksGenTest {

    @Test
    fun generatesBreaksForDaysRange() {
        val gen = TimeBreaksGen()
        val domain = DoubleSpan(0.0, 5 * DAY.totalMillis.toDouble())

        val scaleBreaks = gen.generateBreaks(domain, targetCount = 5)

        assertTrue(scaleBreaks.domainValues.size in 4..6, "Expected 4..6 breaks, got ${scaleBreaks.domainValues.size}")
        assertTrue(scaleBreaks.transformedValues.first() >= domain.lowerEnd)
        assertTrue(scaleBreaks.transformedValues.last() <= domain.upperEnd)
    }

    @Test
    fun generatesBreaksForHoursRange() {
        val gen = TimeBreaksGen()
        val domain = DoubleSpan(0.0, 12 * HOUR.totalMillis.toDouble())

        val scaleBreaks = gen.generateBreaks(domain, targetCount = 6)

        assertTrue(scaleBreaks.domainValues.size in 11..13, "Expected 11..13 breaks, got ${scaleBreaks.domainValues.size}")
        assertTrue(scaleBreaks.transformedValues.first() >= domain.lowerEnd)
        assertTrue(scaleBreaks.transformedValues.last() <= domain.upperEnd)
    }

    @Test
    fun generatesBreaksForMinutesRange() {
        val gen = TimeBreaksGen()
        val domain = DoubleSpan(0.0, 30 * MINUTE.totalMillis.toDouble())

        val scaleBreaks = gen.generateBreaks(domain, targetCount = 6)

        assertTrue(scaleBreaks.domainValues.size in 5..7, "Expected 5..7 breaks, got ${scaleBreaks.domainValues.size}")
        assertTrue(scaleBreaks.transformedValues.first() >= domain.lowerEnd)
        assertTrue(scaleBreaks.transformedValues.last() <= domain.upperEnd)
    }

    @Test
    fun labelsMatchBreaksCount() {
        val gen = TimeBreaksGen()
        val domain = DoubleSpan(0.0, 6 * HOUR.totalMillis.toDouble())

        val scaleBreaks = gen.generateBreaks(domain, targetCount = 6)

        assertEquals(scaleBreaks.domainValues.size, scaleBreaks.labels.size)
    }

    @Test
    fun usesProvidedFormatter() {
        val customFormatter: (Any) -> String = { "custom" }
        val gen = TimeBreaksGen(providedFormatter = customFormatter)
        val domain = DoubleSpan(0.0, 6 * HOUR.totalMillis.toDouble())

        val scaleBreaks = gen.generateBreaks(domain, targetCount = 6)

        assertTrue(scaleBreaks.labels.all { it == "custom" })
    }
}
