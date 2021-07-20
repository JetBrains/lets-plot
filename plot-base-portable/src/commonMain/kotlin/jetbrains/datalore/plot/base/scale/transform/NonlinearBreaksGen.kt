/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.scale.BreaksGenerator
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.base.scale.breaks.NumericBreakFormatter

internal class NonlinearBreaksGen(
    private val transform: ContinuousTransform,
    private val labelFormatter: ((Any) -> String)? = null
) : BreaksGenerator {

    private val linearBreaksGen = LinearBreaksGen(labelFormatter)

    override fun labelFormatter(domain: ClosedRange<Double>, targetCount: Int): (Any) -> String {
        // Note: this label formatter is not one used to format values on axis/legend
        return linearBreaksGen.labelFormatter(domain, targetCount)
    }

    override fun generateBreaks(domain: ClosedRange<Double>, targetCount: Int): ScaleBreaks {
        val transformedDomain = MapperUtil.map(domain) { transform.apply(it) }
        val scaleBreaks = linearBreaksGen.generateBreaks(transformedDomain, targetCount)
        val transformedBreakValues = scaleBreaks.domainValues

        // Transform back to data space.
        val breakValues = transform.applyInverse(transformedBreakValues).filterNotNull()

        // format each tick with its own formatter
        val labels = ArrayList<String>()
        var step = 0.0
        val maxI = breakValues.size - 1
        for (i in 0..maxI) {
            val domainValue = breakValues[i]
            if (step == 0.0) {
                if (i < maxI) {
                    step = breakValues[i + 1] - domainValue
                }
            } else {
                step = domainValue - breakValues[i - 1]
            }
            val formatter = labelFormatter ?: NumericBreakFormatter(domainValue, step, true)::apply
            labels.add(formatter(domainValue))
        }

        return ScaleBreaks(breakValues, breakValues, labels)
    }
}
