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

    fun inverseTransformToContinuousDomain(l: List<Double?>, scale: Scale<*>): List<Double?> {
        check(scale.isContinuousDomain) { "Not continuous numeric domain: $scale" }
        return (scale.transform as ContinuousTransform).applyInverse(l)
    }

    fun inverseTransform(l: List<Double?>, scale: Scale<*>): List<*> {
        val transform = scale.transform
        return if (transform is ContinuousTransform) {
            transform.applyInverse(l)
        } else {
            l.map { transform.applyInverse(it) }
        }
    }

    fun transformedDefinedLimits(scale: Scale<*>): Pair<Double, Double> {
        scale as ContinuousScale
        val (lower, upper) = scale.continuousDomainLimits
        val transform = scale.transform as ContinuousTransform
        val (transformedLower, transformedUpper) = Pair(
            if (transform.isInDomain(lower)) transform.apply(lower)!! else Double.NaN,
            if (transform.isInDomain(upper)) transform.apply(upper)!! else Double.NaN
        )

        return if (SeriesUtil.allFinite(transformedLower, transformedUpper)) {
            Pair(
                min(transformedLower, transformedUpper),
                max(transformedLower, transformedUpper)
            )
        } else {
            Pair(transformedLower, transformedUpper)
        }
    }
}
