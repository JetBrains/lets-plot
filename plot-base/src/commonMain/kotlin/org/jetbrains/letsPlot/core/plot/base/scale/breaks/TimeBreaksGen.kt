/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.scale.OriginalDomainBreaksGenerator
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.DurationFormatter.DEFAULT_DURATION_FORMATTER

/**
 * Timescale doesn't need a time zone.
 */
class TimeBreaksGen(
    private val providedFormatter: ((Any) -> String)? = null,
) : OriginalDomainBreaksGenerator {
    override fun generateBreaks(domain: DoubleSpan, targetCount: Int): ScaleBreaks {
        val helper = TimeBreaksHelper(
            domain.lowerEnd,
            domain.upperEnd,
            targetCount,
            providedFormatter,
        )
        val ticks = helper.breaks
        val labels = helper.formatBreaks(ticks)
        return ScaleBreaks.ContinuousFlex.noTransform(
            ticks,
            formatter = DEFAULT_DURATION_FORMATTER,     // doesn't matter here since we have alternative labels
            alternativeLabels = labels
        )
    }

    override fun defaultFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String {
        return DEFAULT_DURATION_FORMATTER
    }
}
