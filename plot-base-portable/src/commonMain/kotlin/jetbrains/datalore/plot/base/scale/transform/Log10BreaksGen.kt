/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.scale.BreaksGenerator
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.base.scale.breaks.NumericBreakFormatter
import kotlin.math.log10
import kotlin.math.pow

class Log10BreaksGen(
    private val labelFormatter: ((Any) -> String)? = null
) : BreaksGenerator {

    private val myLinearBreaksGen = LinearBreaksGen(labelFormatter)

    override fun labelFormatter(domain: ClosedRange<Double>, targetCount: Int): (Any) -> String {
        // Note: this label formatter is not one used to format values on axis/legend
        return myLinearBreaksGen.labelFormatter(domain, targetCount)
    }

    override fun generateBreaks(domain: ClosedRange<Double>, targetCount: Int): ScaleBreaks {
        val domainLog10 = MapperUtil.map(domain) { log10(it as Double) }
        val linearBreaks = myLinearBreaksGen.generateBreaks(domainLog10, targetCount)
        val domainLog10Values = linearBreaks.domainValues

        // Transform back to data space.
        val domainValues = domainLog10Values.map {
            10.0.pow(it)
        }

        // format each tick with its own formatter
        val labels = ArrayList<String>()
        var step = 0.0
        val maxI = domainValues.size - 1
        for (i in 0..maxI) {
            val domainValue = domainValues[i]
            if (step == 0.0) {
                if (i < maxI) {
                    step = domainValues[i + 1] - domainValue
                }
            } else {
                step = domainValue - domainValues[i - 1]
            }
            val formatter = labelFormatter ?: NumericBreakFormatter(domainValue, step, true)::apply
            labels.add(formatter(domainValue))
        }

        return ScaleBreaks(domainValues, domainValues, labels)
    }
}
