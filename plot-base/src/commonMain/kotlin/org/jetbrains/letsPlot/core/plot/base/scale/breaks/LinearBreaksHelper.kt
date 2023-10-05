/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import kotlin.math.*

class LinearBreaksHelper(
    rangeStart: Double,
    rangeEnd: Double,
    count: Int,
    minStep: Double = Double.MIN_VALUE,
    precise: Boolean = false
) : BreaksHelperBase(rangeStart, rangeEnd, count) {
    override val breaks: List<Double>

    init {
        check(count > 0) { "Can't compute breaks for count: $count" }

        val step = if (precise) {
            this.targetStep
        } else {
            computeNiceStep(this.span, count, minStep)
        }

        val breaks =
            if (SeriesUtil.isBeyondPrecision(normalStart, step) || SeriesUtil.isBeyondPrecision(normalEnd, step)) {
                emptyList()
            } else if (precise) {
                (0 until count).map { normalStart + step / 2 + it * step }
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
    }

    companion object {
        private const val MIN_BREAKS_COUNT = 3

        private fun computeNiceStep(
            span: Double,
            count: Int,
            minStep: Double
        ): Double {
            // compute step so that it is multiple of 10, 5 or 2.
            val stepRaw = span / count
            val step10Power = floor(log10(stepRaw))
            val stepPow = 10.0.pow(step10Power)
            val error = stepPow * count / span
            val step = when {
                error <= 0.15 -> stepPow * 10.0
                error <= 0.35 -> stepPow * 5.0
                error <= 0.75 -> stepPow * 2.0
                else -> stepPow
            }
            // If there is enough space for the minimal number of breaks, then restrict step to minStep.
            return if (minStep * (MIN_BREAKS_COUNT - 1) < span) {
                max(step, minStep)
            } else {
                step
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
    }
}
