/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import kotlin.math.max
import kotlin.math.min

object MapperUtil {
    fun map(r: DoubleSpan, mapper: ScaleMapper<Double>): DoubleSpan {
        val a = mapper(r.lowerEnd)!!
        val b = mapper(r.upperEnd)!!
        return DoubleSpan(min(a, b), max(a, b))
    }

    fun rangeWithLimitsAfterTransform(
        dataRange: DoubleSpan,
        trans: ContinuousTransform
    ): DoubleSpan {
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
        )

        return DoubleSpan.encloseAll(transformedLimits)
    }
}
