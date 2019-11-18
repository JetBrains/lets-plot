/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord.map

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.spatial.MercatorUtils.VALID_LATITUDE_RANGE
import jetbrains.datalore.base.spatial.MercatorUtils.getMercatorY
import jetbrains.datalore.plot.base.coord.Projection

class MercatorProjectionY : Projection {
    override fun apply(v: Double): Double {
        return getMercatorY(v)
    }

    override fun toValidDomain(domain: ClosedRange<Double>): ClosedRange<Double> {
        if (VALID_LATITUDE_RANGE.isConnected(domain)) {
            return VALID_LATITUDE_RANGE.intersection(domain)
        }
        throw IllegalArgumentException("Illegal latitude range for mercator projection: $domain")
    }
}
