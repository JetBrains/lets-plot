/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.transform

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.Transform.Companion.MAX_DOUBLE
import org.jetbrains.letsPlot.core.plot.base.Transform.Companion.MIN_POSITIVE_DOUBLE
import kotlin.math.log
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

internal open class LogTransform(private val base: Double) : FunTransform(
    transformFun = { v -> log(v, base) },
    inverseFun = { v -> base.pow(v) }
) {
    private val lowerLimTransformed by lazy { calcLowerLimTransformed(transformFun) }
    private val upperLimTransformed by lazy { calcUpperLimTransformed(transformFun) }

    override fun hasDomainLimits(): Boolean = true

    override fun isInDomain(v: Double?): Boolean {
        return SeriesUtil.isFinite(v) && v!! >= LOWER_LIM_DOMAIN && v <= UPPER_LIM_DOMAIN
    }

    override fun apply(v: Double?): Double? {
        val transformedValue = super.apply(v)
        return when {
            transformedValue == null -> null
            transformedValue.isNaN() -> Double.NaN
            else -> min(upperLimTransformed, max(lowerLimTransformed, transformedValue))
        }
    }

    override fun applyInverse(v: Double?): Double? {
        @Suppress("NAME_SHADOWING")
        val v = if (v != null) min(max(v, lowerLimTransformed), upperLimTransformed) else null
        return super.applyInverse(v)
    }

    override fun createApplicableDomain(middle: Double?): DoubleSpan {
        @Suppress("NAME_SHADOWING")
        val middle = when {
            middle == null -> 1.0
            isInDomain(middle) -> middle //max(middle!!, lowerLimDomain)
            middle >= 0.0 && middle < LOWER_LIM_DOMAIN -> LOWER_LIM_DOMAIN
            middle > UPPER_LIM_DOMAIN -> UPPER_LIM_DOMAIN
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
        return DoubleSpan(
            min(lower, UPPER_LIM_DOMAIN),
            min(upper, UPPER_LIM_DOMAIN)
        )
    }

    companion object {
        const val UPPER_LIM_DOMAIN = MAX_DOUBLE
        const val LOWER_LIM_DOMAIN = MIN_POSITIVE_DOUBLE

        internal fun calcLowerLimTransformed(transformFun: (Double) -> Double): Double {
            return transformFun(MIN_POSITIVE_DOUBLE)
        }

        internal fun calcUpperLimTransformed(transformFun: (Double) -> Double): Double {
            return transformFun(MAX_DOUBLE)
        }
    }
}