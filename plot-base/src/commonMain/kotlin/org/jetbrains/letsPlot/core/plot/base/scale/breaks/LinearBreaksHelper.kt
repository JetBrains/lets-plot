/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.ExponentFormat
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import kotlin.math.*

internal class LinearBreaksHelper constructor(
    domain: DoubleSpan,
    targetCount: Int,
    private val providedFormatter: ((Any) -> String)?,
    expFormat: ExponentFormat,
) {

    val breaks: List<Double>
    val formatter: (Any) -> String

    init {
        check(targetCount > 0) { "'count' must be positive: $targetCount" }

        val step = computeNiceStep(domain.length, targetCount)
        val start = domain.lowerEnd
        val end = domain.upperEnd
        val breaks =
            if (SeriesUtil.isBeyondPrecision(start, step) ||
                SeriesUtil.isBeyondPrecision(end, step)
            ) {
                emptyList()
            } else {
                computeNiceBreaks(start, end, step)
            }

        this.breaks = breaks.ifEmpty {
            listOf(start)
        }

        this.formatter = providedFormatter ?: createFormatter(this.breaks, expFormat)
    }

    companion object {

        private fun computeNiceStep(
            span: Double,
            count: Int
        ): Double {
            // compute a step so that it is multiple of 10, 5 or 2.
            val stepRaw = span / count
            val step10Power = floor(log10(stepRaw))
            val step = 10.0.pow(step10Power)
            val error = step * count / span
            return when {
                error <= 0.15 -> step * 10.0
                error <= 0.35 -> step * 5.0
                error <= 0.75 -> step * 2.0
                else -> step
            }
        }

        private fun computeNiceBreaks(
            start: Double,
            end: Double,
            step: Double
        ): List<Double> {
            if (step == 0.0) return emptyList()

            // extend range to allow for FP errors
            val delta = step / 10000
            val startE = start - delta
            val endE = end + delta

            if (startE <= 0 && endE >= 0) {
                // The domain includes zero.
                val neg = generateSequence(0.0) { it - step }
                    .takeWhile { it >= startE }
                    .map { if (it == -0.0) 0.0 else it }
                    .map { max(it, start) }
                    .toList()
                    .reversed()

                val pos = generateBreaks(DoubleSpan(0.0, end), step)

                return (neg + pos).distinct()
            } else {
                return generateBreaks(DoubleSpan(start, end), step)
            }
        }

        internal fun generateBreaks(
            domain: DoubleSpan,
            step: Double
        ): List<Double> {
            check(step > 0) { "Step must be positive: $step" }

            val start = domain.lowerEnd
            val end = domain.upperEnd

            // extend range to allow for FP errors
            val delta = step / 10000
            val startE = start - delta
            val endE = end + delta

            val startTick = ceil(startE / step) * step
            return generateSequence(startTick) { it + step }
                .takeWhile { it <= endE }
                .map { if (it == -0.0) 0.0 else it }
                .map { min(it, end) }  // Do not allow ticks to go beyond the range
                .distinct()
                .toList()
        }

        private fun createFormatter(breakValues: List<Double>, expFormat: ExponentFormat): (Any) -> String {
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
                expFormat = expFormat
            )
            return formatter::apply
        }
    }
}
