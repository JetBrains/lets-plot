/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.projections

import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.datalore.base.spatial.GeoUtils.limitLat
import jetbrains.datalore.base.spatial.GeoUtils.limitLon
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.MercatorUtils
import jetbrains.datalore.base.spatial.MercatorUtils.VALID_LATITUDE_RANGE
import jetbrains.datalore.base.spatial.MercatorUtils.VALID_LONGITUDE_RANGE

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

    companion object {
        private val VALID_RECTANGLE = newSpanRectangle(
            explicitVec<LonLat>(VALID_LONGITUDE_RANGE.lowerEndpoint(), VALID_LATITUDE_RANGE.lowerEndpoint()),
            explicitVec<LonLat>(VALID_LONGITUDE_RANGE.upperEndpoint(), VALID_LATITUDE_RANGE.upperEndpoint())
        )
    }
}