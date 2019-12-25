/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.datalore.base.typedGeometry.Generic
import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.typedGeometry.Polygon


class Fragment(
    val key: QuadKey<LonLat>,
    val boundaries: List<Boundary<Generic>>
) {
    val multiPolygon: MultiPolygon<Generic>

    init {
        val xyMultipolygon = ArrayList<Polygon<Generic>>()
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
