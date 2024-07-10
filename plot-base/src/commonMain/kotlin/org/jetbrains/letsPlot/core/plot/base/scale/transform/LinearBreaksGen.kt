/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.transform

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.scale.BreaksGenerator
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.LinearBreaksHelper
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.NumericBreakFormatter
import kotlin.math.abs
import kotlin.math.max

internal class LinearBreaksGen(
    private val providedFormatter: ((Any) -> String)? = null,
    private val superscriptExponent: Boolean,
) : BreaksGenerator {

    override fun generateBreaks(domain: DoubleSpan, targetCount: Int): ScaleBreaks {
        val breaks = generateBreakValues(domain, targetCount)
        val formatter = providedFormatter ?: createFormatter(breaks)
        return ScaleBreaks(
            domainValues = breaks,
            transformedValues = breaks,
            formatter = formatter
        )
    }

    override fun defaultFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String {
        return createFormatter(generateBreakValues(domain, targetCount))
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
            allowMetricPrefix = true,
            superscriptExponent = superscriptExponent
        )
        return formatter::apply
    }

    companion object {
        internal fun generateBreakValues(domain: DoubleSpan, targetCount: Int): List<Double> {
            val helper = LinearBreaksHelper(
                domain.lowerEnd,
                domain.upperEnd,
                targetCount
            )
            return helper.breaks
        }
    }
}
