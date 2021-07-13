/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.scale.BreaksGenerator
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.base.scale.breaks.LinearBreaksHelper

internal class LinearBreaksGen(
    private val formatter: ((Any) -> String)? = null
) : BreaksGenerator {
    override fun generateBreaks(domain: ClosedRange<Double>, targetCount: Int): ScaleBreaks {
        val helper = breaksHelper(domain, targetCount)
        val breaks = helper.breaks
        val labelFormatter = formatter ?: helper.formatter
        val labels = breaks.map { labelFormatter(it) }
        return ScaleBreaks(breaks, breaks, labels)
    }

    override fun labelFormatter(domain: ClosedRange<Double>, targetCount: Int): (Any) -> String {
        return formatter ?: breaksHelper(domain, targetCount).formatter
    }

    companion object {
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
    }
}
