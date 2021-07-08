/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import jetbrains.datalore.plot.common.data.SeriesUtil

internal class IdentityTransform : FunTransform({ v -> v }, { v -> v }) {
    override fun hasDomainLimits(): Boolean = false

    override fun isInDomain(v: Double?) = SeriesUtil.isFinite(v)

    override fun apply(l: List<*>): List<Double?> {
        return safeCastToDoubles(l)
    }

    override fun applyInverse(l: List<Double?>): List<Double?> {
        return l
    }
}
