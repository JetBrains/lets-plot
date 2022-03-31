/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord.map

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.spatial.MercatorUtils.getMercatorX
import jetbrains.datalore.plot.base.coord.Projection

// ToDo: duplicates jetbrains.livemap.core.projections.MercatorProjection
class MercatorProjectionX : Projection {
    override val nonlinear = false

    override fun apply(v: Double): Double {
        return getMercatorX(v)
    }

    override fun toValidDomain(domain: DoubleSpan): DoubleSpan {
        return domain
    }
}
