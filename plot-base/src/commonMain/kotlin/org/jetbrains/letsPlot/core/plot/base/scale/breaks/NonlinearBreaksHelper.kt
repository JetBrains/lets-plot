/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleUtil
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Log10Transform
import org.jetbrains.letsPlot.core.plot.base.scale.transform.SymlogTransform
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

internal class NonlinearBreaksHelper(
    rangeStart: Double,
    rangeEnd: Double,
    targetCount: Int,
    superscriptExponent: Boolean,
    transform: ContinuousTransform,
    niceLogBreaks: Boolean,
) : BreaksHelperBase(rangeStart, rangeEnd, targetCount) {
    override val breaks: List<Double>
    val formatter: (Any) -> String

    init {
        val domain = DoubleSpan(rangeStart, rangeEnd)

        val transformedDomain = ScaleUtil.applyTransform(
            domain,
            transform
        )

        @Suppress("NAME_SHADOWING")
        val targetCount = if (niceLogBreaks) {
            when (transform) {
                is Log10Transform,
                is SymlogTransform ->
                    recalculateBreaksCount(targetCount, transformedDomain)

                else -> targetCount
            }

        } else {
            targetCount
        }
        val transformedBreakValues: List<Double> =
            LinearBreaksHelper(
                transformedDomain.lowerEnd,
                transformedDomain.upperEnd,
                targetCount,
                superscriptExponent
            ).breaks

        // Transform back to data space.
        this.breaks = transform.applyInverse(transformedBreakValues).filterNotNull()

        val breakFormatters = createFormatters(this.breaks, superscriptExponent)
        this.formatter = MultiFormatter(this.breaks, breakFormatters)::apply
    }

    companion object {
        private const val MIN_BREAKS_COUNT = 3

        private fun recalculateBreaksCount(
            breaksCount: Int,
            transformedDomain: DoubleSpan,
        ): Int {
            val recalculatedBreaksCount =
                (floor(transformedDomain.upperEnd) - ceil(transformedDomain.lowerEnd)).roundToInt() + 1

            return if (recalculatedBreaksCount in MIN_BREAKS_COUNT..breaksCount) {
                recalculatedBreaksCount
            } else {
                breaksCount
            }
        }

        private fun createFormatters(
            breakValues: List<Double>,
            superscriptExponent: Boolean
        ): List<(Any) -> String> {
            if (breakValues.isEmpty()) return emptyList()
            if (breakValues.size == 1) {
                val domainValue = breakValues[0]
                val step = domainValue / 10
                return listOf(createFormatter(domainValue, step, superscriptExponent))
            }

            // format each tick with its own formatter
            val formatters: List<(Any) -> String> = breakValues.mapIndexed { i, currValue ->
                val step = abs(
                    when (i) {
                        0 -> currValue - breakValues[i + 1]
                        else -> currValue - breakValues[i - 1]
                    }
                )
                createFormatter(currValue, step, superscriptExponent)
            }
            return formatters
        }

        private fun createFormatter(
            domainValue: Double,
            step: Double,
            superscriptExponent: Boolean
        ): (Any) -> String {
            return NumericBreakFormatter(
                domainValue,
                step,
                true,
                superscriptExponent = superscriptExponent
            )::apply
        }
    }
}