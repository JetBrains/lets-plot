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
import kotlin.math.abs
import kotlin.math.min

internal class NonlinearBreaksGen(
    private val transform: ContinuousTransform,
    private val formatter: ((Any) -> String)? = null
) : BreaksGenerator {

    override fun generateBreaks(domain: ClosedRange<Double>, targetCount: Int): ScaleBreaks {
        val breakValues = generateBreakValues(domain, targetCount, transform)
        val breakFormatters = if (formatter != null) {
            List(breakValues.size) { formatter }
        } else {
            createFormatters(breakValues)
        }

        val labels = breakValues.mapIndexed() { i, v -> breakFormatters[i](v) }
        return ScaleBreaks(breakValues, breakValues, labels)
    }

    override fun labelFormatter(domain: ClosedRange<Double>, targetCount: Int): (Any) -> String {
        return formatter ?: defaultFormatter(domain, targetCount)
    }

    override fun defaultFormatter(domain: ClosedRange<Double>, targetCount: Int): (Any) -> String {
        return createMultiFormatter(generateBreakValues(domain, targetCount, transform))
    }

    companion object {
        private fun generateBreakValues(
            domain: ClosedRange<Double>,
            targetCount: Int,
            transform: ContinuousTransform
        ): List<Double> {
            val transformedDomain = MapperUtil.map(domain) { transform.apply(it) }
            val transformedBreakValues: List<Double> =
                LinearBreaksGen.generateBreakValues(transformedDomain, targetCount)

            // Transform back to data space.
            return transform.applyInverse(transformedBreakValues).filterNotNull()
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
            @Suppress("UnnecessaryVariable")
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
                true
            )::apply
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
