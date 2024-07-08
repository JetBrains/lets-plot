/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.transform

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.scale.BreaksGenerator
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.TimeBreaksHelper

class TimeBreaksGen : BreaksGenerator {
    override fun generateBreaks(domain: DoubleSpan, targetCount: Int): ScaleBreaks {
        val helper = breaksHelper(domain, targetCount)
        val ticks = helper.breaks
        val labels = helper.formatBreaks(ticks)
        return ScaleBreaks(ticks, ticks, labels)
    }

    override fun defaultFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String {
        return breaksHelper(domain, targetCount).formatter
    }

    private fun breaksHelper(
        domain: DoubleSpan,
        targetCount: Int
    ): TimeBreaksHelper {
        return TimeBreaksHelper(
            domain.lowerEnd,
            domain.upperEnd,
            targetCount
        )
    }
}
