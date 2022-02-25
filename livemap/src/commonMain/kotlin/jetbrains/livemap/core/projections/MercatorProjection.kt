/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.projections

import jetbrains.datalore.base.spatial.*
import jetbrains.datalore.base.spatial.MercatorUtils.VALID_LATITUDE_RANGE
import jetbrains.datalore.base.spatial.MercatorUtils.VALID_LONGITUDE_RANGE
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.typedGeometry.newSpanRectangle

internal class MercatorProjection : GeoProjection {

    override fun project(v: LonLatPoint): GeographicPoint =
        explicitVec(
            MercatorUtils.getMercatorX(limitLon(v.x)),
            MercatorUtils.getMercatorY(limitLat(v.y))
        )

    override fun invert(v: GeographicPoint): LonLatPoint =
        explicitVec(
            limitLon(MercatorUtils.getLongitude(v.x)),
            limitLat(MercatorUtils.getLatitude(v.y))
        )

    override fun validRect(): Rect<LonLat> = VALID_RECTANGLE
    override val cylindrical: Boolean = true

    companion object {
        private val VALID_RECTANGLE = newSpanRectangle(
            explicitVec<LonLat>(VALID_LONGITUDE_RANGE.lowerEnd, VALID_LATITUDE_RANGE.lowerEnd),
            explicitVec<LonLat>(VALID_LONGITUDE_RANGE.upperEnd, VALID_LATITUDE_RANGE.upperEnd)
        )
    }
}