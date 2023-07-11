/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.scale.BreaksGenerator
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.base.scale.breaks.TimeBreaksHelper

class TimeBreaksGen : BreaksGenerator {
    override fun generateBreaks(domain: DoubleSpan, targetCount: Int): ScaleBreaks {
        val helper = breaksHelper(domain, targetCount)
        val ticks = helper.breaks
        val labels = helper.formatBreaks(ticks)
        return ScaleBreaks(ticks, ticks, labels)
    }

    override fun labelFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String {
        return breaksHelper(domain, targetCount).formatter
    }

    override fun defaultFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String {
        return breaksHelper(domain, targetCount).formatter
    }

    private fun breaksHelper(
        domainAfterTransform: DoubleSpan,
        targetCount: Int
    ): TimeBreaksHelper {
        return TimeBreaksHelper(
            domainAfterTransform.lowerEnd,
            domainAfterTransform.upperEnd,
            targetCount
        )
    }
}
