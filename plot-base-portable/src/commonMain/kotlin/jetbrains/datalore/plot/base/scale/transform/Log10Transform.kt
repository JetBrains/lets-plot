/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow

internal class Log10Transform : FunTransform(
    transformFun = { v -> log10(v) },
    inverseFun = { v -> 10.0.pow(v) }
) {
    override fun hasDomainLimits() = true

    override fun isInDomain(v: Double?): Boolean {
        return SeriesUtil.isFinite(v) && v!! >= 0.0
    }

    override fun apply(v: Double?): Double? {
        return trimInfinity(super.apply(v))
    }

    override fun applyInverse(v: Double?): Double? {
        return super.applyInverse(v)
    }

    override fun createApplicableDomain(middle: Double?): ClosedRange<Double> {
        @Suppress("NAME_SHADOWING")
        val middle = when {
            isInDomain(middle) -> max(middle!!, 0.0)
            else -> 1.0
        }

        return ClosedRange(max(middle - 0.5, 0.0), middle + 0.5)
    }

    override fun toApplicableDomain(range: ClosedRange<Double>): ClosedRange<Double> {
        val lower = max(range.lowerEnd, 0.0)
        val upper = max(range.upperEnd, lower)
        return ClosedRange(lower, upper)
    }

    companion object {
        internal const val LOWER_LIM_TRANSFOTMED: Double = -Double.MAX_VALUE / 10
//        internal const val LOWER_LIM_DOMAIN: Double = Double.MIN_VALUE * 10

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
