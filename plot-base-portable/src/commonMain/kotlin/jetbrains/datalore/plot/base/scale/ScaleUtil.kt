/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.max
import kotlin.math.min

object ScaleUtil {

    fun labelByBreak(scale: Scale<*>): Map<Any, String> {
        val scaleBreaks = scale.getScaleBreaks()
        return scaleBreaks.domainValues.zip(scaleBreaks.labels).toMap()
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

    fun applyTransform(source: List<*>, transform: Transform): List<Double?> {
        // Replace values outside 'transform limits' with null-s.
        @Suppress("NAME_SHADOWING")
        val source = if (transform.hasDomainLimits()) {
            source.map { if (transform.isInDomain(it)) it else null }
        } else {
            source
        }

        return transform.apply(source)
    }

    fun applyTransform(r: ClosedRange<Double>, transform: ContinuousTransform): ClosedRange<Double> {
        val a = transform.apply(r.lowerEnd)!!
        val b = transform.apply(r.upperEnd)!!
        return ClosedRange(min(a, b), max(a, b))
    }

    fun applyInverseTransform(r: ClosedRange<Double>, transform: ContinuousTransform): ClosedRange<Double> {
        val a = transform.applyInverse(r.lowerEnd)!!
        val b = transform.applyInverse(r.upperEnd)!!
        return ClosedRange(min(a, b), max(a, b))
    }
}
