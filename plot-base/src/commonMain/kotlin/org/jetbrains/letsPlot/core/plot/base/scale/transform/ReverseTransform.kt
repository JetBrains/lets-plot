/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.transform

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil

internal class ReverseTransform : FunTransform({ v -> -v }, { v -> -v }) {
    override fun hasDomainLimits() = false
    override fun isInDomain(v: Double?): Boolean {
        return SeriesUtil.isFinite(v)
    }

    override fun createApplicableDomain(middle: Double?): DoubleSpan {
        return Transforms.IDENTITY.createApplicableDomain(middle)
    }

    override fun toApplicableDomain(range: DoubleSpan): DoubleSpan {
        return Transforms.IDENTITY.toApplicableDomain(range)
    }
}