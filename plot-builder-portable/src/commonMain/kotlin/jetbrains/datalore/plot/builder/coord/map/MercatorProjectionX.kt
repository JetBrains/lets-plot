/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord.map

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.spatial.MercatorUtils.VALID_LONGITUDE_RANGE
import jetbrains.datalore.base.spatial.MercatorUtils.getMercatorX
import jetbrains.datalore.plot.base.coord.Projection

class MercatorProjectionX : Projection {
    override fun apply(v: Double): Double {
        return getMercatorX(v)
    }

    override fun toValidDomain(domain: ClosedRange<Double>): ClosedRange<Double> {
        if (VALID_LONGITUDE_RANGE.isConnected(domain)) {
            return VALID_LONGITUDE_RANGE.intersection(domain)
        }
        throw IllegalArgumentException("Illegal longitude range for mercator projection: $domain")
    }
}
