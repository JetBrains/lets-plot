/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.datetime.DateTimeFormatUtil.createInstantFormatter
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.time.interval.NiceTimeInterval
import org.jetbrains.letsPlot.core.commons.time.interval.TimeInterval
import org.jetbrains.letsPlot.core.plot.base.scale.OriginalDomainBreaksGenerator
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms

/**
 * Generates breaks at fixed time intervals (e.g., every 2 weeks, every 3 months).
 */
class DateTimeFixedBreaksGen(
    private val breakWidth: TimeInterval,
    private val providedFormatter: ((Any) -> String)? = null,
    private val minInterval: NiceTimeInterval?,
    private val maxInterval: NiceTimeInterval?,
    private val tz: TimeZone?
) : OriginalDomainBreaksGenerator {
    override val fixedBreakWidth: Boolean
        get() = true

    private val timeZone: TimeZone get() = tz ?: TimeZone.UTC

    override fun generateBreaks(domain: DoubleSpan, targetCount: Int): ScaleBreaks {
        val breaks = breakWidth.range(domain.lowerEnd, domain.upperEnd, tz)

        val formatter = providedFormatter
            ?: createInstantFormatter(breakWidth.tickFormatPattern, timeZone)

        return ScaleBreaks.Fixed.withTransform(
            domainValues = breaks,
            transform = Transforms.IDENTITY,
            formatter = formatter
        )
    }

    override fun defaultFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String {
        return DateTimeBreaksHelper(
            domain,
            targetCount,
            providedFormatter = null,
            minInterval = minInterval,
            maxInterval = maxInterval,
            tz = tz
        ).formatter
    }
}
