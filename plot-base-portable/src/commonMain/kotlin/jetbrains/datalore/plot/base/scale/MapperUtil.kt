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

    fun rangeWithLimitsAfterTransform2(
        dataRange: ClosedRange<Double>,
        trans: ContinuousTransform
    ): ClosedRange<Double> {
        check(trans.isInDomain(dataRange.lowerEnd)) {
            "[${trans::class.simpleName}] Lower end ${dataRange.lowerEnd} is outside of transform's domain."
        }
        check(trans.isInDomain(dataRange.upperEnd)) {
            "[${trans::class.simpleName}] Upper end ${dataRange.upperEnd} is outside of transform's domain."
        }

        val transformedLimits = listOf(
            trans.apply(trans.definedLimits().first),
            trans.apply(trans.definedLimits().second),
            trans.apply(dataRange.lowerEnd),
            trans.apply(dataRange.upperEnd),
        ).filterNotNull()

        return ClosedRange.encloseAll(transformedLimits)
    }
}
