/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.Transform

internal class ContinuousTransformWithLimits(
    private val actual: ContinuousTransform,
    private val lowerLimit: Double? = null,
    private val upperLimit: Double? = null,
) : ContinuousTransform by actual {

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

    override fun unwrapLimits(): Transform {
        return actual.unwrapLimits()
    }

    override fun definedLimits(): Pair<Double?, Double?> {
        return Pair(lowerLimit, upperLimit)
    }
}