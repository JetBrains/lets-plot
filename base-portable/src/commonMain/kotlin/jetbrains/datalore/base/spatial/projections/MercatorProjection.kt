/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial.projections

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.base.spatial.MercatorUtils
import jetbrains.datalore.base.spatial.MercatorUtils.VALID_LATITUDE_RANGE
import jetbrains.datalore.base.spatial.MercatorUtils.VALID_LONGITUDE_RANGE
import jetbrains.datalore.base.spatial.limitLat
import jetbrains.datalore.base.spatial.limitLon

internal class MercatorProjection : Projection {
    override val cylindrical: Boolean = true

    override fun project(v: DoubleVector): DoubleVector =
        DoubleVector(
            MercatorUtils.getMercatorX(limitLon(v.x)),
            MercatorUtils.getMercatorY(limitLat(v.y))
        )

    override fun invert(v: DoubleVector): DoubleVector =
        DoubleVector(
            limitLon(MercatorUtils.getLongitude(v.x)),
            limitLat(MercatorUtils.getLatitude(v.y))
        )

    override fun validDomain(): DoubleRectangle = VALID_RECTANGLE

    companion object {
        private val VALID_RECTANGLE = DoubleRectangle(
            xRange = VALID_LONGITUDE_RANGE,
            yRange = VALID_LATITUDE_RANGE
        )
    }
}