/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.transform

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.scale.BreaksGenerator
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.DateTimeBreaksHelper

class DateTimeBreaksGen(
    private val providedFormatter: ((Any) -> String)? = null
) : BreaksGenerator {
    override fun generateBreaks(domain: DoubleSpan, targetCount: Int): ScaleBreaks {
        val helper = breaksHelper(domain, targetCount)
        val ticks = helper.breaks

        @Suppress("UNCHECKED_CAST")
        val formatter = (providedFormatter ?: helper.formatter) as (Any) -> String
        return ScaleBreaks(
            domainValues = ticks,
            transformedValues = ticks,
            formatter = formatter
        )
    }

    private fun breaksHelper(domain: DoubleSpan, targetCount: Int): DateTimeBreaksHelper {
        return DateTimeBreaksHelper(
            domain.lowerEnd,
            domain.upperEnd,
            targetCount
        )
    }

    override fun defaultFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String {
        val numFormatter = breaksHelper(domain, targetCount).formatter
        return { v: Any -> numFormatter(v as Number) }
    }
}
