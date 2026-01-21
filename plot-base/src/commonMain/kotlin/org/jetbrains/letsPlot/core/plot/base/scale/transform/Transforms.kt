/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.transform

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.isBeyondPrecision
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform

object Transforms {
    val IDENTITY: ContinuousTransform = IdentityTransform()
    val REVERSE: ContinuousTransform = ReverseTransform()
    val SQRT: ContinuousTransform = SqrtTransform()
    val LOG10: ContinuousTransform = Log10Transform()
    val LOG2: ContinuousTransform = Log2Transform()
    val SYMLOG: ContinuousTransform = SymlogTransform()

    fun continuousWithLimits(actual: ContinuousTransform, limits: Pair<Double?, Double?>): ContinuousTransform {
        return ContinuousTransformWithLimits(actual, limits.first, limits.second)
    }

    /**
     * Use with caution!
     *
     * Do not use this method on transformed data ranges. (see 'SeriesUtil.ensureApplicableRange()')
     *
     * Only use on original data ranges.
     */
    fun ensureApplicableDomain(
        dataRange: DoubleSpan?,
        transform: ContinuousTransform
    ): DoubleSpan {
        if (dataRange == null) {
            return transform.createApplicableDomain()
        }

        val domain = transform.toApplicableDomain(dataRange)
        return when (isBeyondPrecision(domain)) {
            true -> transform.createApplicableDomain(domain.upperEnd)
            false -> domain
        }
    }
}
