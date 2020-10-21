/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.Transform
import kotlin.math.max
import kotlin.math.min

object MapperUtil {
    fun map(r: ClosedRange<Double>, mapper: (Double?) -> Double?): ClosedRange<Double> {
        val a = mapper(r.lowerEnd)!!
        val b = mapper(r.upperEnd)!!
        return ClosedRange(min(a, b), max(a, b))
    }

    fun mapDiscreteDomainValuesToNumbers(values: Collection<*>): Map<Any, Double> {
        return mapDiscreteDomainValuesToIndices(values)
    }

    private fun mapDiscreteDomainValuesToIndices(values: Collection<*>): Map<Any, Double> {
        val result = LinkedHashMap<Any, Double>()
        var index = 0
        for (v in values) {
            if (v != null && !result.containsKey(v)) {
                result[v] = index++.toDouble()
            }
        }
        return result
    }

    fun rangeWithLimitsAfterTransform(
        dataRange: ClosedRange<Double>,
        lowerLimit: Double?,
        upperLimit: Double?,
        trans: Transform?
    ): ClosedRange<Double> {
        val lower = lowerLimit ?: dataRange.lowerEnd
        val upper = upperLimit ?: dataRange.upperEnd
        val limits = listOf(lower, upper)
        return ClosedRange.encloseAll(trans?.apply(limits) ?: limits)
    }
}
