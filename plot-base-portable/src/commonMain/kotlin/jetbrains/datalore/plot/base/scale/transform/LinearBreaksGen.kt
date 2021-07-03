/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.scale.BreaksGenerator
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.base.scale.breaks.LinearBreaksHelper

class LinearBreaksGen(
    private val labelFormatter: ((Any) -> String)? = null
) : BreaksGenerator {
    override fun generateBreaks(domain: ClosedRange<Double>, targetCount: Int): ScaleBreaks {
        val helper = breaksHelper(domain, targetCount)
        val ticks = helper.breaks
        val labelFormatter = labelFormatter ?: helper.labelFormatter
        val labels = ArrayList<String>()
        for (tick in ticks) {
            labels.add(labelFormatter(tick))
        }
        return ScaleBreaks(ticks, ticks, labels)
    }

    private fun breaksHelper(
        domainAfterTransform: ClosedRange<Double>,
        targetCount: Int
    ): LinearBreaksHelper {
        return LinearBreaksHelper(
            domainAfterTransform.lowerEnd,
            domainAfterTransform.upperEnd,
            targetCount
        )
    }

    override fun labelFormatter(domain: ClosedRange<Double>, targetCount: Int): (Any) -> String {
        return labelFormatter ?: breaksHelper(domain, targetCount).labelFormatter
    }
}
