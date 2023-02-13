/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.projections

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.LonLatPoint
import jetbrains.datalore.base.spatial.limitLat
import jetbrains.datalore.base.spatial.limitLon
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.base.typedGeometry.explicitVec

internal class GeographicProjection : GeoProjection {
    override fun project(v: LonLatPoint): GeographicPoint = explicitVec(limitLon(v.x), limitLat(v.y))
    override fun invert(v: GeographicPoint): LonLatPoint = explicitVec(limitLon(v.x), limitLat(v.y))
    override fun validRect(): Rect<LonLat> = VALID_RECTANGLE
    override val cylindrical: Boolean = false

    companion object {
        private val VALID_RECTANGLE = Rect.LTRB<LonLat>(
            leftTop = explicitVec(-180.0, -90.0),
            rightBottom = explicitVec(+180.0, +90.0)
        )
    }
}