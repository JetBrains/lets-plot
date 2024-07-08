/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.transform

import org.jetbrains.letsPlot.core.plot.base.scale.BreaksGenerator
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.DateTimeBreaksHelper
import org.jetbrains.letsPlot.commons.interval.DoubleSpan

class DateTimeBreaksGen(
    private val labelFormatter: ((Any) -> String)? = null
) : BreaksGenerator {
    override fun generateBreaks(domain: DoubleSpan, targetCount: Int): ScaleBreaks {
        val helper = breaksHelper(domain, targetCount)
        val ticks = helper.breaks
        val labelFormatter = labelFormatter ?: helper.formatter
        val labels = ArrayList<String>()
        for (tick in ticks) {
            labels.add(labelFormatter(tick))
        }
        return ScaleBreaks(ticks, ticks, labels)
    }

    private fun breaksHelper(
        domain: DoubleSpan,
        targetCount: Int
    ): DateTimeBreaksHelper {
        return DateTimeBreaksHelper(
            domain.lowerEnd,
            domain.upperEnd,
            targetCount
        )
    }

    override fun labelFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String {
        return labelFormatter ?: defaultFormatter(domain, targetCount)
    }

    override fun defaultFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String {
        val numFormatter = breaksHelper(domain, targetCount).formatter
        return { v: Any -> numFormatter(v as Number) }
    }
}
