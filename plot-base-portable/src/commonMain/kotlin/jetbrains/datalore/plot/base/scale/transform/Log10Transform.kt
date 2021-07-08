/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.log10
import kotlin.math.pow

internal class Log10Transform : FunTransform(
    transformFun = { v -> log10(v) },
    inverseFun = { v -> 10.0.pow(v) }
) {
    override fun hasDomainLimits() = true

    override fun isInDomain(v: Double?): Boolean {
        return SeriesUtil.isFinite(v) && v!! > 0.0
    }
}
