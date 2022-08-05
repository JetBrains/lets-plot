/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.typedGeometry.Polygon
import jetbrains.datalore.base.typedGeometry.Untyped


class Fragment(
    val key: QuadKey<LonLat>,
    boundaries: List<Boundary<Untyped>>
) {
    val multiPolygon: MultiPolygon<Untyped>

    init {
        val xyMultipolygon = ArrayList<Polygon<Untyped>>()
        for (boundary in boundaries) {
            val xyBoundary = boundary.asMultipolygon()
            for (xyPolygon in xyBoundary) {
                if (!xyPolygon.isEmpty()) {
                    xyMultipolygon.add(xyPolygon)
                }
            }
        }
        multiPolygon = MultiPolygon(xyMultipolygon)
    }
}
