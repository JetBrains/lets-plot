package jetbrains.datalore.base.projectionGeometry

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangles

object Typed {
    data class Scalar<ProjT>(
        val value: Double
    )

    data class Point<ProjT>(
        val x: Double,
        val y: Double
    ) {
        constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())

        fun add(p: Point<ProjT>) =
            Point<ProjT>(x + p.x, y + p.y)
        fun subtract(p: Point<ProjT>) =
            Point<ProjT>(x - p.x, y - p.y)
        fun mul(d: Double) = Point<ProjT>(x * d, y * d)
    }

    class Rectangle<ProjT>(
        val origin: Point<ProjT>,
        val dimension: Point<ProjT>
    ) {
        constructor(
            left: Double,
            top: Double,
            width: Double,
            height: Double
        ) : this(Point(left, top), Point(width, height))
    }

    class Ring<ProjT>(points: List<Point<ProjT>>) : AbstractGeometryList<Point<ProjT>>(points)
    class LineString<ProjT>(geometry: List<Point<ProjT>>) : AbstractGeometryList<Point<ProjT>>(geometry)
    class Polygon<ProjT>(rings: List<Ring<ProjT>>) : AbstractGeometryList<Ring<ProjT>>(rings)
    class MultiPoint<ProjT>(geometry: List<Point<ProjT>>) : AbstractGeometryList<Point<ProjT>>(geometry)
    class MultiLineString<ProjT>(geometry: List<LineString<ProjT>>) : AbstractGeometryList<LineString<ProjT>>(geometry)
    class MultiPolygon<ProjT>(polygons: List<Polygon<ProjT>>) : AbstractGeometryList<Polygon<ProjT>>(polygons)

    class TileGeometry<ProjT> private constructor(
        val type: GeometryType,
        val multiPoint: MultiPoint<ProjT>?,
        val multiLineString: MultiLineString<ProjT>?,
        val multiPolygon: MultiPolygon<ProjT>?
    ) {
        companion object {
            fun <ProjT> createMultiPoint(multiPoint: MultiPoint<ProjT>): TileGeometry<ProjT> {
                return TileGeometry(MULTI_POINT, multiPoint, null, null)
            }

            fun <ProjT> createMultiLineString(multiLineString: MultiLineString<ProjT>): TileGeometry<ProjT> {
                return TileGeometry(MULTI_LINESTRING, null, multiLineString, null)
            }

            fun <ProjT> createMultiPolygon(multiPolygon: MultiPolygon<ProjT>): TileGeometry<ProjT> {
                return TileGeometry(MULTI_POLYGON, null, null, multiPolygon)
            }
        }
    }

    enum class GeometryType {
        MULTI_POINT,
        MULTI_LINESTRING,
        MULTI_POLYGON;
    }
}

fun <ProjT> Typed.Polygon<ProjT>.limit(): Typed.Rectangle<ProjT> {
    return DoubleRectangles.boundingBox(
        this.asSequence()
            .flatten()
            .map { it }
            .asIterable()
    )
}

fun <ProjT> Typed.Rectangle<ProjT>.intersects(rect: Typed.Rectangle<ProjT>): Boolean {
    val t1 = origin
    val t2 = origin.add(dimension)
    val r1 = rect.origin
    val r2 = rect.origin.add(rect.dimension)
    return r2.x >= t1.x && t2.x >= r1.x && r2.y >= t1.y && t2.y >= r1.y
}

fun <ProjT> Typed.MultiPolygon<ProjT>.limit(): List<Typed.Rectangle<ProjT>> { return map { polygon -> polygon.limit() } }

class Generic
class LonLat

typealias Rectangle = Typed.Rectangle<Generic>

typealias Point = Typed.Point<Generic>
typealias Ring = Typed.Ring<Generic>
typealias LineString = Typed.LineString<Generic>
typealias Polygon = Typed.Polygon<Generic>
typealias MultiPoint = Typed.MultiPoint<Generic>
typealias MultiLineString = Typed.MultiLineString<Generic>
typealias MultiPolygon = Typed.MultiPolygon<Generic>

typealias LonLatPoint = Typed.Point<LonLat>
typealias LonLatRectangle = Typed.Rectangle<LonLat>

typealias AnyPoint = Typed.Point<*>
typealias AnyLineString = Typed.LineString<*>

fun <ProjT> Typed.Point<*>.reinterpret(): Typed.Point<ProjT> = this as Typed.Point<ProjT>
fun <ProjT> Typed.MultiPoint<*>.reinterpret(): Typed.MultiPoint<ProjT> = this as Typed.MultiPoint<ProjT>
fun <ProjT> Typed.MultiLineString<*>.reinterpret(): Typed.MultiLineString<ProjT> = this as Typed.MultiLineString<ProjT>
fun <ProjT> Typed.MultiPolygon<*>.reinterpret(): Typed.MultiPolygon<ProjT> = this as Typed.MultiPolygon<ProjT>

/**
 * Create generic rectangle by any points
 */
fun erasedRectangle(origin: AnyPoint, dimension: AnyPoint): Rectangle {
    return Rectangle(
        origin as Point,
        dimension as Point
    )
}

fun <ProjT> newSpanRectangle(leftTop: Typed.Point<ProjT>, rightBottom: Typed.Point<ProjT>): Typed.Rectangle<ProjT> {
    return Typed.Rectangle(leftTop, rightBottom.subtract(leftTop))
}

fun Typed.Rectangle<*>.xRange() = ClosedRange.closed(origin.x, origin.x + dimension.x)
fun Typed.Rectangle<*>.yRange() = ClosedRange.closed(origin.y, origin.y + dimension.y)

val Typed.Rectangle<*>.bottom: Double get() = origin.y + dimension.y
val Typed.Rectangle<*>.right: Double get() = origin.x + dimension.x
val Typed.Rectangle<*>.height: Double get() = dimension.y
val Typed.Rectangle<*>.width: Double get() = dimension.x
val Typed.Rectangle<*>.top: Double get() = origin.y
val Typed.Rectangle<*>.left: Double get() = origin.x
val <ProjT> Typed.Rectangle<ProjT>.center: Typed.Point<ProjT> get() = origin.add(dimension.mul(0.5))

fun <ProjT> Typed.Point<ProjT>.subX(p: Typed.Point<ProjT>) = Typed.Point<ProjT>(x - p.x, y)
fun <ProjT> Typed.Point<ProjT>.subY(p: Typed.Point<ProjT>) = Typed.Point<ProjT>(x, y - p.y)
fun <ProjT> Typed.Point<ProjT>.addX(p: Typed.Point<ProjT>) = Typed.Point<ProjT>(x + p.x, y)
fun <ProjT> Typed.Point<ProjT>.addY(p: Typed.Point<ProjT>) = Typed.Point<ProjT>(x, y + p.y)
