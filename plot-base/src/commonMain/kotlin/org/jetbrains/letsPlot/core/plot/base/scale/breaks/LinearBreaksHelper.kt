/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.ExponentFormat
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import kotlin.math.*

internal class LinearBreaksHelper(
    rangeStart: Double,
    rangeEnd: Double,
    targetCount: Int,
    private val providedFormatter: ((Any) -> String)?,
    exponentFormat: ExponentFormat,
    minExponent: Int? = null,
    maxExponent: Int? = null,
    precise: Boolean = false
) : BreaksHelperBase(rangeStart, rangeEnd, targetCount) {

    override val breaks: List<Double>
    val formatter: (Any) -> String

    init {
        check(targetCount > 0) { "Can't compute breaks for count: $targetCount" }

        val step = if (precise) {
            this.targetStep
        } else {
            computeNiceStep(this.span, targetCount)
        }

        val breaks =
            if (SeriesUtil.isBeyondPrecision(normalStart, step) || SeriesUtil.isBeyondPrecision(normalEnd, step)) {
                emptyList()
            } else if (precise) {
                (0 until targetCount).map { normalStart + step / 2 + it * step }
            } else {
                computeNiceBreaks(normalStart, normalEnd, step)
            }

        this.breaks = if (breaks.isEmpty()) {
            listOf(normalStart)
        } else if (isReversed) {
            breaks.asReversed()
        } else {
            breaks
        }

        this.formatter = providedFormatter ?: createFormatter(this.breaks, exponentFormat, minExponent, maxExponent)
    }

    companion object {
        private fun computeNiceStep(
            span: Double,
            count: Int
        ): Double {
            // compute step so that it is multiple of 10, 5 or 2.
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

            val breaks = ArrayList<Double>()
            var tick = ceil(startE / step) * step
            if (start >= 0 && startE < 0) {
                // avoid negative zero
                tick = 0.0
            }
            while (tick <= endE) {
                // don't allow ticks to go beyond the range
                tick = min(tick, end)

                breaks.add(tick)
                tick += step
            }

            return breaks
        }

        private fun createFormatter(
            breakValues: List<Double>,
            exponentFormat: ExponentFormat,
            minExponent: Int?,
            maxExponent: Int?
        ): (Any) -> String {
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
                exponentFormat = exponentFormat,
                minExponent = minExponent,
                maxExponent = maxExponent
            )
            return formatter::apply
        }
    }
}
