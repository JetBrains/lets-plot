/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.ContinuousTransform
import kotlin.math.max
import kotlin.math.min

object MapperUtil {
    fun map(r: ClosedRange<Double>, mapper: (Double?) -> Double?): ClosedRange<Double> {
        val a = mapper(r.lowerEnd)!!
        val b = mapper(r.upperEnd)!!
        return ClosedRange(min(a, b), max(a, b))
    }

    fun rangeWithLimitsAfterTransform(
        dataRange: ClosedRange<Double>,
        lowerLimit: Double?,
        upperLimit: Double?,
        trans: ContinuousTransform
    ): ClosedRange<Double> {
        val lower = if (lowerLimit != null && lowerLimit.isFinite()) {
            lowerLimit
        } else {
            dataRange.lowerEnd
        }
        check(trans.isInDomain(lower)) {
            "[${trans::class.simpleName}] Lower end $lower is outside of transform's domain."
        }

        val upper = if (upperLimit != null && upperLimit.isFinite()) {
            upperLimit
        } else {
            dataRange.upperEnd
        }
        check(trans.isInDomain(upper)) {
            "[${trans::class.simpleName}] Lower end $upper is outside of transform's domain."
        }

        val limits = listOf(lower, upper)
        return ClosedRange.encloseAll(trans.apply(limits))
    }
}
