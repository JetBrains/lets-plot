/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.transform

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.Transform

internal class ContinuousTransformWithLimits(
    private val actual: ContinuousTransform,
    private val lowerLimit: Double? = null,
    private val upperLimit: Double? = null,
    // Delegating 'by' fails to call 'isInDomain(v: Double?)' (overridden here)
    // from `isInDomain(v: Any?)` (in the parent class).
//) : ContinuousTransform by actual {
) : ContinuousTransform {

    init {
        check(!(lowerLimit == null && upperLimit == null)) {
            "Continuous transform: undefined limit."
        }
        if (lowerLimit != null) {
            check(lowerLimit.isFinite()) { "Continuous transform lower limit: $lowerLimit." }
            check(actual.isInDomain(lowerLimit)) {
                "Lower limit: $lowerLimit is outside of ${actual::class.simpleName} domain."
            }
        }
        if (upperLimit != null) {
            check(upperLimit.isFinite()) { "Continuous transform upper limit: $upperLimit" }
            check(actual.isInDomain(upperLimit)) {
                "Upper limit: $upperLimit is outside of ${actual::class.simpleName} domain."
            }
        }
        check(lowerLimit == null || upperLimit == null || upperLimit >= lowerLimit) {
            "Continuous transform limits: lower ($lowerLimit) > upper ($upperLimit)"
        }
    }

    override fun hasDomainLimits() = true

    override fun isInDomain(v: Double?): Boolean {
        if (v == null || !v.isFinite()) return false
        if (lowerLimit != null && v < lowerLimit) return false
        if (upperLimit != null && v > upperLimit) return false
        return actual.isInDomain(v)
    }

    override fun apply(v: Double?): Double? = actual.apply(v)
    override fun apply(l: List<*>): List<Double?> = actual.apply(l)
    override fun applyInverse(v: Double?): Double? = actual.applyInverse(v)
    override fun applyInverse(l: List<Double?>): List<Double?> = actual.applyInverse(l)
    override fun createApplicableDomain(middle: Double?): DoubleSpan = actual.createApplicableDomain(middle)
    override fun toApplicableDomain(range: DoubleSpan): DoubleSpan = actual.toApplicableDomain(range)

    override fun unwrap(): Transform = actual.unwrap()
    override fun definedLimits(): Pair<Double?, Double?> {
        return Pair(lowerLimit, upperLimit)
    }
}