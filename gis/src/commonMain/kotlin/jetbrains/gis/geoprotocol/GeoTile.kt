package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.projectionGeometry.Multipolygon
import jetbrains.datalore.base.projectionGeometry.Polygon
import jetbrains.datalore.base.projectionGeometry.QuadKey


class GeoTile(val key: QuadKey, private val geometries: List<Geometry>?) {
    private val multipolygon: Multipolygon

    init {
        if (geometries == null) {
            throw IllegalArgumentException("geometry is null")
        }

        val xyMultipolygon = ArrayList<Polygon>()
        for (boundary in geometries) {
            val xyBoundary = boundary.asMultipolygon()
            for (xyPolygon in xyBoundary) {
                if (!xyPolygon.isEmpty()) {
                    xyMultipolygon.add(xyPolygon)
                }
            }
        }
        multipolygon = Multipolygon(xyMultipolygon)
    }
}
