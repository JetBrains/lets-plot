package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.geometry.DoubleRectangles
import jetbrains.datalore.base.projectionGeometry.Typed
import jetbrains.datalore.base.projectionGeometry.limit

object GeometryUtil {
    fun <TypeT> bbox(multipolygon: Typed.MultiPolygon<TypeT>): Typed.Rectangle<TypeT>? {
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

    fun <TypeT> asLineString(geometry: TypedGeometry<TypeT>): Typed.LineString<TypeT> {
        return Typed.LineString(geometry.asMultipolygon().get(0).get(0))
    }
}
