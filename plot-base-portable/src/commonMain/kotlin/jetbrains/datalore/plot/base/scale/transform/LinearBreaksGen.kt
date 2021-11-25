/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.scale.BreaksGenerator
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.base.scale.breaks.LinearBreaksHelper
import jetbrains.datalore.plot.base.scale.breaks.NumericBreakFormatter
import kotlin.math.abs
import kotlin.math.max

internal class LinearBreaksGen(
    private val formatter: ((Any) -> String)? = null
) : BreaksGenerator {

    override fun generateBreaks(domain: ClosedRange<Double>, targetCount: Int): ScaleBreaks {
        val breaks = generateBreakValues(domain, targetCount)
        val fmt = formatter ?: createFormatter(breaks)
        val labels = breaks.map { fmt(it) }
        return ScaleBreaks(breaks, breaks, labels)
    }

    override fun labelFormatter(domain: ClosedRange<Double>, targetCount: Int): (Any) -> String {
        return formatter ?: defaultFormatter(domain, targetCount)
    }

    override fun defaultFormatter(domain: ClosedRange<Double>, targetCount: Int): (Any) -> String {
        return createFormatter(generateBreakValues(domain, targetCount))
    }

    companion object {
        internal fun generateBreakValues(domain: ClosedRange<Double>, targetCount: Int): List<Double> {
            val helper = LinearBreaksHelper(
                domain.lowerEnd,
                domain.upperEnd,
                targetCount
            )
            return helper.breaks
        }

        private fun createFormatter(breakValues: List<Double>): (Any) -> String {
            val (referenceValue, step) = when {
                breakValues.isEmpty() -> Pair(0.0, 0.5)
                else -> {
                    val v = max(abs(breakValues.first()), abs(breakValues.last()))
                    val s = when {
                        breakValues.size == 1 -> v / 10
                        else -> abs(breakValues[1] - breakValues[0])
                    }
                    Pair(v, s)
                }
            }

            val formatter = NumericBreakFormatter(
                referenceValue,
                step,
                allowMetricPrefix = true
            )
            return formatter::apply
        }
    }
}
