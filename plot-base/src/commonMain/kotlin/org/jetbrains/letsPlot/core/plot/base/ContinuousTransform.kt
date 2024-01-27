/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil

interface ContinuousTransform : Transform {
    override fun isInDomain(v: Any?): Boolean {
        return if (v is Number) isInDomain(v.toDouble()) else false
    }

    fun isInDomain(v: Double?): Boolean
    fun apply(v: Double?): Double?
    override fun applyInverse(v: Double?): Double?
    override fun applyInverse(l: List<Double?>): List<Double?>
    fun createApplicableDomain(middle: Double? = null): DoubleSpan
    fun toApplicableDomain(range: DoubleSpan): DoubleSpan
    fun definedLimits(): Pair<Double?, Double?> = null to null

    fun applyInverse(span: DoubleSpan): DoubleSpan? {
        val v0 = applyInverse(span.lowerEnd)
        val v1 = applyInverse(span.upperEnd)
        return if (SeriesUtil.allFinite(v0, v1)) {
            DoubleSpan(v0!!, v1!!)
        } else {
            null
        }
    }
}
