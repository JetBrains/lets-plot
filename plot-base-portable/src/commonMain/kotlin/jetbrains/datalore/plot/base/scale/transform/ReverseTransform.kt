/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.common.data.SeriesUtil

internal class ReverseTransform : FunTransform({ v -> -v }, { v -> -v }) {
    override fun hasDomainLimits() = false
    override fun isInDomain(v: Double?): Boolean {
        return SeriesUtil.isFinite(v)
    }

    override fun createApplicableDomain(middle: Double?): ClosedRange<Double> {
        return Transforms.IDENTITY.createApplicableDomain(middle)
    }

    override fun toApplicableDomain(range: ClosedRange<Double>): ClosedRange<Double> {
        return Transforms.IDENTITY.toApplicableDomain(range)
    }
}