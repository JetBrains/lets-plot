package jetbrains.datalore.base.projectionGeometry

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleRectangles
import jetbrains.datalore.base.geometry.DoubleVector

object Typed {
    data class Coordinate<ProjT>(
        val x: Double,
        val y: Double
    ) {
        constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())

        fun add(p: Coordinate<ProjT>) = Coordinate<ProjT>(x + p.x, y + p.y)
        fun subtract(p: Coordinate<ProjT>) = Coordinate<ProjT>(x - p.x, y - p.y)
        fun mul(d: Double) = Coordinate<ProjT>(x * d, y * d)
    }

    class Ring<ProjT>(points: List<Coordinate<ProjT>>) : AbstractGeometryList<Coordinate<ProjT>>(points)
    class LineString<ProjT>(geometry: List<Coordinate<ProjT>>) : AbstractGeometryList<Coordinate<ProjT>>(geometry)
    class Polygon<ProjT>(rings: List<Ring<ProjT>>) : AbstractGeometryList<Ring<ProjT>>(rings)
    class MultiPoint<ProjT>(geometry: List<Coordinate<ProjT>>) : AbstractGeometryList<Coordinate<ProjT>>(geometry)
    class MultiLineString<ProjT>(geometry: List<LineString<ProjT>>) : AbstractGeometryList<LineString<ProjT>>(geometry)
    class MultiPolygon<ProjT>(polygons: List<Polygon<ProjT>>) : AbstractGeometryList<Polygon<ProjT>>(polygons)
}

fun Typed.Polygon<*>.limit(): DoubleRectangle {
    return DoubleRectangles.boundingBox(
        this.asSequence()
            .flatten()
            .map { DoubleVector(it.x, it.y) }
            .asIterable()
    )
}

fun Typed.MultiPolygon<*>.limit(): List<DoubleRectangle> { return map { polygon -> polygon.limit() } }

class Generic
class LonLat

typealias Point = Typed.Coordinate<Generic>
typealias Ring = Typed.Ring<Generic>
typealias LineString = Typed.LineString<Generic>
typealias Polygon = Typed.Polygon<Generic>
typealias MultiPoint = Typed.MultiPoint<Generic>
typealias MultiLineString = Typed.MultiLineString<Generic>
typealias MultiPolygon = Typed.MultiPolygon<Generic>
