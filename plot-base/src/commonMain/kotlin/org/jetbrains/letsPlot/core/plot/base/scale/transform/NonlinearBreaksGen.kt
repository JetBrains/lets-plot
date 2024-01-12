/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.transform

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.scale.BreaksGenerator
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleUtil
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.NumericBreakFormatter
import kotlin.math.*

internal class NonlinearBreaksGen(
    private val transform: ContinuousTransform,
    private val formatter: ((Any) -> String)? = null,
    private val superscriptExponent: Boolean,
) : BreaksGenerator {

    override fun generateBreaks(domain: DoubleSpan, targetCount: Int): ScaleBreaks {
        val breakValues = generateBreakValues(domain, recalculateBreaksCount(targetCount, domain, transform), transform)
        val breakFormatters = if (formatter != null) {
            List(breakValues.size) { formatter }
        } else {
            createFormatters(breakValues)
        }

        val labels = breakValues.mapIndexed() { i, v -> breakFormatters[i](v) }
        return ScaleBreaks(breakValues, breakValues, labels)
    }

    override fun labelFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String {
        return formatter ?: defaultFormatter(domain, targetCount)
    }

    override fun defaultFormatter(domain: DoubleSpan, targetCount: Int): (Any) -> String {
        return createMultiFormatter(generateBreakValues(domain, targetCount, transform))
    }

    private fun createMultiFormatter(breakValues: List<Double>): (Any) -> String {
        val breakFormatters = createFormatters(breakValues)
        return MultiFormatter(breakValues, breakFormatters)::apply
    }

    private fun createFormatters(breakValues: List<Double>): List<(Any) -> String> {
        if (breakValues.isEmpty()) return emptyList()
        if (breakValues.size == 1) {
            val domainValue = breakValues[0]
            val step = domainValue / 10
            return listOf(createFormatter(domainValue, step))
        }

        // format each tick with its own formatter
        val formatters: List<(Any) -> String> = breakValues.mapIndexed { i, currValue ->
            val step = abs(
                when (i) {
                    0 -> currValue - breakValues[i + 1]
                    else -> currValue - breakValues[i - 1]
                }
            )
            createFormatter(currValue, step)
        }
        return formatters
    }

    private fun createFormatter(domainValue: Double, step: Double): (Any) -> String {
        return NumericBreakFormatter(
            domainValue,
            step,
            true,
            superscriptExponent = superscriptExponent
        )::apply
    }

    companion object {
        private const val MIN_BREAKS_COUNT = 3

        private fun generateBreakValues(
            domain: DoubleSpan,
            targetCount: Int,
            transform: ContinuousTransform
        ): List<Double> {
            val transformedDomain = ScaleUtil.applyTransform(domain, transform)
            val transformedBreakValues: List<Double> =
                LinearBreaksGen.generateBreakValues(transformedDomain, targetCount)

            // Transform back to data space.
            return transform.applyInverse(transformedBreakValues).filterNotNull()
        }

        private fun recalculateBreaksCount(breaksCount: Int, domain: DoubleSpan, transform: ContinuousTransform): Int {
            return when (transform) {
                is Log10Transform,
                is SymlogTransform -> {
                    val transformedDomain = ScaleUtil.applyTransform(domain, transform)
                    val recalculatedBreaksCount = (floor(transformedDomain.upperEnd) - ceil(transformedDomain.lowerEnd)).roundToInt() + 1
                    if (recalculatedBreaksCount in MIN_BREAKS_COUNT..breaksCount) {
                        recalculatedBreaksCount
                    } else {
                        breaksCount
                    }
                }
                else -> breaksCount
            }
        }
    }

    private class MultiFormatter(
        val breakValues: List<Double>,
        val breakFormatters: List<(Any) -> String>
    ) {
        init {
            check(breakValues.size == breakFormatters.size) {
                "MultiFormatter: breakValues.size=${breakValues.size} but breakFormatters.size=${breakFormatters.size}"
            }
            if (breakValues.size > 1) {
                val ordered = breakValues
                    .mapIndexed { i, v -> if (i == 0) 0.0 else v - breakValues[i - 1] }
                    .all { it >= 0.0 }
                check(ordered) { "MultiFormatter: values must be sorted in ascending order. Were: $breakValues." }
            }
        }

        fun apply(v: Any): String {
            v as Double
            return when {
                breakValues.isEmpty() -> v.toString()
                else -> {
                    val i = abs(breakValues.binarySearch(v))
                    val ii = min(i, breakValues.size - 1)
                    breakFormatters[ii](v)
                }
            }
        }
    }
}