/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.ExponentFormat.Companion.DEF_EXPONENT_FORMAT
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.DAY
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.HOUR
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.MINUTE
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.SECOND
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.WEEK
import kotlin.math.abs
import kotlin.math.ceil

/**
 * The timescale represents time intervals: days, hours, minutes, seconds, etc.
 * Thus, unlike a date-time scale, it doesn't need a time zone.
 */
internal class TimeBreaksHelper(
    rangeStart: Double,
    rangeEnd: Double,
    count: Int,
    private val providedFormatter: ((Any) -> String)?,
) : BreaksHelperBase(rangeStart, rangeEnd, count) {

    override val breaks: List<Double>

    init {
        val ticks: List<Double> = when {
            targetStep < 1000 -> LinearBreaksHelper(
                rangeStart, rangeEnd, count,
                providedFormatter = DUMMY_FORMATTER,
                expFormat = DEF_EXPONENT_FORMAT,
            ).breaks

            else -> computeNiceTicks()
        }

        breaks = when (isReversed) {
            true -> ticks.reversed()
            false -> ticks
        }
    }

    fun formatBreaks(ticks: List<Double>): List<String> {
        return DurationFormatter.formatBreaks(
            breaks = ticks,
            breakWidth = targetStep,
            span = span,
            providedFormatter = providedFormatter,
        )
    }

    private fun computeNiceTicks(): List<Double> {
        val niceTickInterval = listOf(
            1 * SECOND.totalMillis, 5 * SECOND.totalMillis, 15 * SECOND.totalMillis, 30 * SECOND.totalMillis,
            1 * MINUTE.totalMillis, 5 * MINUTE.totalMillis, 15 * MINUTE.totalMillis, 30 * MINUTE.totalMillis,
            1 * HOUR.totalMillis, 3 * HOUR.totalMillis, 6 * HOUR.totalMillis, 12 * HOUR.totalMillis,
            1 * DAY.totalMillis, 2 * DAY.totalMillis,
            1 * WEEK.totalMillis,
            4 * WEEK.totalMillis, // ~1 months
            12 * WEEK.totalMillis, // ~3 months
            48 * WEEK.totalMillis, // ~1 years
        ).minByOrNull { abs(it - targetStep.toLong()) } ?: SECOND.totalMillis

        return makeBreaks(normalStart, normalEnd, niceTickInterval)
    }

    companion object {

        fun makeBreaks(
            start: Double,
            end: Double,
            step: Long
        ): List<Double> {
            var tick = ceil(start / step) * step
            val result = ArrayList<Double>()
            while (tick <= end) {
                result.add(tick)
                tick += step
            }
            return result
        }
    }
}
