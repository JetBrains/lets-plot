/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.scale.BreaksGenerator
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.base.scale.breaks.LinearBreaksHelper

class LinearBreaksGen : BreaksGenerator {
    override fun generateBreaks(domainAfterTransform: ClosedRange<Double>, targetCount: Int): ScaleBreaks {
        val helper = breaksHelper(domainAfterTransform, targetCount)
        val ticks = helper.breaks
        val labelFormatter = helper.labelFormatter
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
        val helper = LinearBreaksHelper(
            domainAfterTransform.lowerEndpoint(),
            domainAfterTransform.upperEndpoint(),
            targetCount
        )
        return helper
    }

    override fun labelFormatter(domainAfterTransform: ClosedRange<Double>, targetCount: Int): (Any) -> String {
        return breaksHelper(domainAfterTransform, targetCount).labelFormatter
    }
}
