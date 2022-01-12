/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.max
import kotlin.math.min

object ScaleUtil {

    fun labelByBreak(scale: Scale<*>): Map<Any, String> {
        val scaleBreaks = scale.getScaleBreaks()
        return scaleBreaks.domainValues.zip(scaleBreaks.labels).toMap()
    }

    fun map(range: ClosedRange<Double>, scale: Scale<Double>): ClosedRange<Double> {
        return MapperUtil.map(range, scale.mapper)
    }

    fun <T> map(l: List<Double?>, scale: Scale<T>): List<T?> {
        val mapper = scale.mapper
        return l.map {
            mapper(it)
        }
    }

    fun transformedDefinedLimits(transform: ContinuousTransform): Pair<Double, Double> {
        val (lower, upper) = Pair(
            transform.apply(transform.definedLimits().first) ?: Double.NaN,
            transform.apply(transform.definedLimits().second) ?: Double.NaN,
        )

        return if (SeriesUtil.allFinite(lower, upper)) {
            Pair(
                min(lower, upper),
                max(lower, upper)
            )
        } else {
            Pair(lower, upper)
        }
    }
}
