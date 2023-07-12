/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.transform

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow

internal class Log10Transform : FunTransform(
    transformFun = { v -> log10(v) },
    inverseFun = { v -> 10.0.pow(v) }
) {
    override fun hasDomainLimits() = true

    override fun isInDomain(v: Double?): Boolean {
        return SeriesUtil.isFinite(v) && v!! >= LOWER_LIM_DOMAIN
    }

    private fun isZero(v: Double?): Boolean {
        return SeriesUtil.isFinite(v) && v!! >= 0.0 && v < LOWER_LIM_DOMAIN
    }

    override fun apply(v: Double?): Double? {
        return trimInfinity(super.apply(v))
    }

    override fun createApplicableDomain(middle: Double?): DoubleSpan {
        @Suppress("NAME_SHADOWING")
        val middle = when {
            isInDomain(middle) -> max(middle!!, LOWER_LIM_DOMAIN)
            isZero(middle) -> LOWER_LIM_DOMAIN  // Special case.
            else -> 1.0
        }

        val lower = if (middle < 1) {
            middle / 2
        } else {
            middle - 0.5
        }
        return DoubleSpan(max(lower, LOWER_LIM_DOMAIN), middle + 0.5)
    }

    override fun toApplicableDomain(range: DoubleSpan): DoubleSpan {
        val lower = max(range.lowerEnd, LOWER_LIM_DOMAIN)
        val upper = max(range.upperEnd, lower)
        return DoubleSpan(lower, upper)
    }

    companion object {
        internal const val LOWER_LIM_TRANSFOTMED: Double = -Double.MAX_VALUE / 10
        internal const val LOWER_LIM_DOMAIN: Double = Double.MIN_VALUE * 10

        /**
         * Avoid transforming 0.0 -> -Infinity
         */
        private fun trimInfinity(v: Double?): Double? {
            return when {
                v == null -> null
                v.isNaN() -> Double.NaN
                else -> max(LOWER_LIM_TRANSFOTMED, v)
            }
        }
    }
}
