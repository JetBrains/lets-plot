/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.Transform
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import kotlin.math.max
import kotlin.math.min

object ScaleUtil {

    fun labelByBreak(scale: Scale): Map<Any, String> {
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

    fun applyTransform(r: DoubleSpan, transform: ContinuousTransform): DoubleSpan {
        val a = transform.apply(r.lowerEnd)!!
        val b = transform.apply(r.upperEnd)!!
        return DoubleSpan(min(a, b), max(a, b))
    }

    fun applyInverseTransform(r: DoubleSpan, transform: ContinuousTransform): DoubleSpan {
        val a = transform.applyInverse(r.lowerEnd)!!
        val b = transform.applyInverse(r.upperEnd)!!
        return DoubleSpan(min(a, b), max(a, b))
    }
}
