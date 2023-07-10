/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import org.jetbrains.letsPlot.commons.interval.DoubleSpan

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
}
