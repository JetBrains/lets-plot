package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.projectionGeometry.Generic
import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.datalore.base.projectionGeometry.Polygon
import jetbrains.datalore.base.projectionGeometry.QuadKey


class GeoTile(val key: QuadKey, val geometries: List<Geometry>) {
    private val multipolygon: MultiPolygon<Generic>

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
        multipolygon = MultiPolygon(xyMultipolygon)
    }
}
