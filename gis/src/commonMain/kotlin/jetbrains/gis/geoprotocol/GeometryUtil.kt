package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.geometry.DoubleRectangles
import jetbrains.datalore.base.projectionGeometry.LineString
import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.datalore.base.projectionGeometry.Rect
import jetbrains.datalore.base.projectionGeometry.limit

object GeometryUtil {
    fun <TypeT> bbox(multipolygon: MultiPolygon<TypeT>): Rect<TypeT>? {
        val rects = multipolygon.limit()
        return if (rects.isEmpty()) {
            null
        } else DoubleRectangles.boundingBox(
            sequenceOf(
                rects.asSequence().map { it.origin },
                rects.asSequence().map { it.origin.add(it.dimension) }
            ).flatten().asIterable()
        )
    }

    fun <TypeT> asLineString(geometry: TypedGeometry<TypeT>): LineString<TypeT> {
        return LineString(geometry.asMultipolygon().get(0).get(0))
    }
}
