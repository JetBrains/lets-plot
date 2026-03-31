/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.time.interval.NiceTimeInterval
import org.jetbrains.letsPlot.core.plot.base.scale.OriginalDomainBreaksGenerator
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks

class DateTimeBreaksGen constructor(
    private val providedFormatter: ((Any) -> String)? = null,
    private val minInterval: NiceTimeInterval?,
    private val maxInterval: NiceTimeInterval?,
    private val tz: TimeZone?
) : OriginalDomainBreaksGenerator {
    override fun generateBreaks(domain: DoubleSpan, targetCount: Int): ScaleBreaks {
        val helper = DateTimeBreaksHelper(
            domain,
            targetCount,
            providedFormatter,
            minInterval = minInterval,
            maxInterval = maxInterval,
            tz = tz
        )

        return ScaleBreaks.ContinuousFlex.noTransform(
            domainValues = helper.breaks,
            formatter = helper.formatter
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
