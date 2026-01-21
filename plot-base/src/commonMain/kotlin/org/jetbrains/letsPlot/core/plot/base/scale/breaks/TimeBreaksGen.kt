/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.ExponentFormat.Companion.DEF_EXPONENT_FORMAT
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.DAY
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.HOUR
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.MINUTE
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.SECOND
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.WEEK
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.scale.OriginalDomainBreaksGenerator
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.DurationFormatter.DEFAULT_DURATION_FORMATTER
import kotlin.math.abs

/**
 * Timescale doesn't need a time zone.
 */
class TimeBreaksGen(
    private val providedFormatter: ((Any) -> String)? = null,
) : OriginalDomainBreaksGenerator {
    override fun generateBreaks(domain: DoubleSpan, targetCount: Int): ScaleBreaks {
        require(targetCount > 0) { "'count' must be positive: $targetCount" }
        val targetStep = domain.length / targetCount
        val breaks: List<Double> = when {
            targetStep < 1000 -> LinearBreaksHelper(
                domain.lowerEnd,
                domain.upperEnd,
                targetCount,
                providedFormatter = DUMMY_FORMATTER,
                expFormat = DEF_EXPONENT_FORMAT,
            ).breaks

            else -> computeNiceTicks(
                domain,
                targetStep
            )
        }

        val labels = DurationFormatter.formatBreaks(
            breaks,
            breakWidth = targetStep,
            span = domain.length,
            providedFormatter = providedFormatter
        )

        return ScaleBreaks.ContinuousFlex.noTransform(
            breaks,
            formatter = DEFAULT_DURATION_FORMATTER,     // doesn't matter here since we have alternative labels
            alternativeLabels = labels
        )
    }

    override fun defaultFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String {
        return DEFAULT_DURATION_FORMATTER
    }

    companion object {
        private fun computeNiceTicks(
            domain: DoubleSpan,
            targetStep: Double
        ): List<Double> {
            val niceTickInterval: Long = listOf(
                1 * SECOND.totalMillis, 5 * SECOND.totalMillis, 15 * SECOND.totalMillis, 30 * SECOND.totalMillis,
                1 * MINUTE.totalMillis, 5 * MINUTE.totalMillis, 15 * MINUTE.totalMillis, 30 * MINUTE.totalMillis,
                1 * HOUR.totalMillis, 3 * HOUR.totalMillis, 6 * HOUR.totalMillis, 12 * HOUR.totalMillis,
                1 * DAY.totalMillis, 2 * DAY.totalMillis,
                1 * WEEK.totalMillis,
                4 * WEEK.totalMillis, // ~1 months
                12 * WEEK.totalMillis, // ~3 months
                48 * WEEK.totalMillis, // ~1 years
            ).minByOrNull { abs(it - targetStep.toLong()) } ?: SECOND.totalMillis

            return LinearBreaksHelper.generateBreaks(
                domain,
                niceTickInterval.toDouble()
            )
        }
    }
}
