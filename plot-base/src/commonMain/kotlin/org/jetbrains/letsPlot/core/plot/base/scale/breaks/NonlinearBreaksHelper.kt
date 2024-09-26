/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat
import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.ExponentFormat
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
    private val providedFormatter: ((Any) -> String)?,
    exponentFormat: ExponentFormat,
    minExponent: Int = NumberFormat.DEF_MIN_EXP,
    maxExponent: Int? = null,
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
                providedFormatter = DUMMY_FORMATTER,
                exponentFormat,
                minExponent,
                maxExponent
            ).breaks

        // Transform back to data space.
        this.breaks = transform.applyInverse(transformedBreakValues).filterNotNull()
        this.formatter = providedFormatter ?: let {
            val breakFormatters = createFormatters(this.breaks, exponentFormat, minExponent, maxExponent)
            MultiFormatter(this.breaks, breakFormatters)::apply
        }
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
            exponentFormat: ExponentFormat,
            minExponent: Int,
            maxExponent: Int?
        ): List<(Any) -> String> {
            if (breakValues.isEmpty()) return emptyList()
            if (breakValues.size == 1) {
                val domainValue = breakValues[0]
                val step = domainValue / 10
                return listOf(createFormatter(domainValue, step, exponentFormat, minExponent, maxExponent))
            }

            // format each tick with its own formatter
            val formatters: List<(Any) -> String> = breakValues.mapIndexed { i, currValue ->
                val step = abs(
                    when (i) {
                        0 -> currValue - breakValues[i + 1]
                        else -> currValue - breakValues[i - 1]
                    }
                )
                createFormatter(currValue, step, exponentFormat, minExponent, maxExponent)
            }
            return formatters
        }

        private fun createFormatter(
            domainValue: Double,
            step: Double,
            exponentFormat: ExponentFormat,
            minExponent: Int,
            maxExponent: Int?
        ): (Any) -> String {
            return NumericBreakFormatter(
                domainValue,
                step,
                true,
                exponentFormat = exponentFormat,
                minExponent = minExponent,
                maxExponent = maxExponent
            )::apply
        }
    }
}