/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.intern.datetime.Duration
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.scale.OriginalDomainBreaksGenerator
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.DurationFormatter.DEFAULT_DURATION_FORMATTER
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms

/**
 * Generates breaks at fixed time intervals for time delta scales (e.g., every 6 hours, every 30 minutes).
 * Unlike DateTimeFixedBreaksGen, this doesn't use timezone since it represents durations, not absolute time.
 */
class TimeFixedBreaksGen(
    private val breakWidth: Duration,
    private val providedFormatter: ((Any) -> String)? = null
) : OriginalDomainBreaksGenerator {
    override val fixedBreakWidth: Boolean
        get() = true

    override fun generateBreaks(domain: DoubleSpan, targetCount: Int): ScaleBreaks {
        val breaks = TimeBreaksHelper.makeBreaks(
            start = domain.lowerEnd,
            end = domain.upperEnd,
            step = breakWidth.totalMillis,
        )
        val labels = DurationFormatter.formatBreaks(
            breaks,
            breakWidth = breakWidth.totalMillis.toDouble(),
            span = domain.length,
            providedFormatter = providedFormatter
        )

        return ScaleBreaks.Fixed.withTransform(
            domainValues = breaks,
            transform = Transforms.IDENTITY,
            formatter = DEFAULT_DURATION_FORMATTER,      // doesn't matter here since we have alternative labels
            alternativeLabels = labels
        )
    }

    override fun defaultFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String {
        return DEFAULT_DURATION_FORMATTER
    }
}
