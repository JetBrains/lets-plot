/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.QuadKey
import org.jetbrains.letsPlot.commons.intern.typedGeometry.MultiPolygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Polygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Untyped


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
