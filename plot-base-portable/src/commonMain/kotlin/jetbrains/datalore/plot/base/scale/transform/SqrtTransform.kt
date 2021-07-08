/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.sqrt

class SqrtTransform : FunTransform(
    transformFun = { v -> sqrt(v) },
    inverseFun = { v -> v * v }
) {
    override fun hasDomainLimits() = true

    override fun isInDomain(v: Double?): Boolean {
        return SeriesUtil.isFinite(v) && v!! >= 0.0
    }
}