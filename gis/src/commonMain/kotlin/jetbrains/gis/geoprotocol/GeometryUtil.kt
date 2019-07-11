package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleRectangles
import jetbrains.datalore.base.projectionGeometry.LineString
import jetbrains.datalore.base.projectionGeometry.MultiPolygon

object GeometryUtil {
    fun bbox(multipolygon: MultiPolygon): DoubleRectangle? {
        val rects = multipolygon.limits
        return if (rects.isEmpty()) {
            null
        } else DoubleRectangles.boundingBox(
            sequenceOf(
                rects.asSequence().map { it.origin },
                rects.asSequence().map { it.origin.add(it.dimension) }
            ).flatten().asIterable()
        )
    }

    fun asLineString(geometry: Geometry): LineString {
        return LineString(geometry.asMultipolygon().get(0).get(0))
    }
}
