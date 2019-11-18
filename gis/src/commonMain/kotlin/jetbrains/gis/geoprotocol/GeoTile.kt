/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.projectionGeometry.Generic
import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.datalore.base.projectionGeometry.Polygon
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey


class GeoTile(val key: QuadKey<LonLat>, val geometries: List<Boundary<Generic>>) {
    val multiPolygon: MultiPolygon<Generic>

    init {
        val xyMultipolygon = ArrayList<Polygon<Generic>>()
        for (boundary in geometries) {
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
