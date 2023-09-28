/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.transform

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import kotlin.math.log
import kotlin.math.max
import kotlin.math.pow

internal open class LogTransform(private val base: Double) : FunTransform(
    transformFun = { v -> log(v, base) },
    inverseFun = { v -> base.pow(v) }
) {
    private val lowerLimTransformed by lazy { calcLowerLimTransformed(base) }
    private val lowerLimDomain by lazy { calcLowerLimDomain(base) }

    override fun hasDomainLimits(): Boolean = true

    override fun isInDomain(v: Double?): Boolean {
        return SeriesUtil.isFinite(v) && v!! >= lowerLimDomain
    }

    override fun apply(v: Double?): Double? {
        val transformedValue = super.apply(v)
        return when {
            transformedValue == null -> null
            transformedValue.isNaN() -> Double.NaN
            else -> max(lowerLimTransformed, transformedValue)
        }
    }

    override fun createApplicableDomain(middle: Double?): DoubleSpan {
        @Suppress("NAME_SHADOWING")
        val middle = when {
            isInDomain(middle) -> max(middle!!, lowerLimDomain)
            isZero(middle) -> lowerLimDomain // Special case.
            else -> 1.0
        }

        val lower = if (middle < 1) {
            middle / 2
        } else {
            middle - 0.5
        }
        return DoubleSpan(max(lower, lowerLimDomain), middle + 0.5)
    }

    override fun toApplicableDomain(range: DoubleSpan): DoubleSpan {
        val lower = max(range.lowerEnd, lowerLimDomain)
        val upper = max(range.upperEnd, lower)
        return DoubleSpan(lower, upper)
    }

    private fun isZero(v: Double?): Boolean {
        return SeriesUtil.isFinite(v) && v!! >= 0.0 && v < lowerLimDomain
    }

    companion object {
        internal fun calcLowerLimTransformed(base: Double): Double = -Double.MAX_VALUE / base

        internal fun calcLowerLimDomain(base: Double): Double = Double.MIN_VALUE * base
    }
}