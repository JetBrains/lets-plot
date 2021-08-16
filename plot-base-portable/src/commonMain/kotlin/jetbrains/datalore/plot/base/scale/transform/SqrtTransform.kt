/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.max
import kotlin.math.sqrt

internal class SqrtTransform : FunTransform(
    transformFun = { v -> sqrt(v) },
    inverseFun = { v -> v * v }
) {
    override fun hasDomainLimits() = true

    override fun isInDomain(v: Double?): Boolean {
        return SeriesUtil.isFinite(v) && v!! >= 0.0
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
        val upper = max(range.upperEnd, 0.0)
        return ClosedRange(lower, upper)
    }
}