package jetbrains.datalore.base.projectionGeometry

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangles

object Typed {
    data class Vec<TypeT>(
        val x: Double,
        val y: Double
    ) {
        constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())

        fun add(p: Vec<TypeT>) =
            Vec<TypeT>(x + p.x, y + p.y)
        fun subtract(p: Vec<TypeT>) =
            Vec<TypeT>(x - p.x, y - p.y)
        fun mul(d: Double) = Vec<TypeT>(x * d, y * d)
    }

    class Rectangle<TypeT>(
        val origin: Vec<TypeT>,
        val dimension: Vec<TypeT>
    ) {
        constructor(
            left: Double,
            top: Double,
            width: Double,
            height: Double
        ) : this(Vec(left, top), Vec(width, height))
    }

    class Ring<TypeT>(points: List<Vec<TypeT>>) : AbstractGeometryList<Vec<TypeT>>(points)
    class LineString<TypeT>(geometry: List<Vec<TypeT>>) : AbstractGeometryList<Vec<TypeT>>(geometry)
    class Polygon<TypeT>(rings: List<Ring<TypeT>>) : AbstractGeometryList<Ring<TypeT>>(rings)
    class MultiPoint<TypeT>(geometry: List<Vec<TypeT>>) : AbstractGeometryList<Vec<TypeT>>(geometry)
    class MultiLineString<TypeT>(geometry: List<LineString<TypeT>>) : AbstractGeometryList<LineString<TypeT>>(geometry)
    class MultiPolygon<TypeT>(polygons: List<Polygon<TypeT>>) : AbstractGeometryList<Polygon<TypeT>>(polygons)

    class TileGeometry<TypeT> private constructor(
        val type: GeometryType,
        val multiPoint: MultiPoint<TypeT>?,
        val multiLineString: MultiLineString<TypeT>?,
        val multiPolygon: MultiPolygon<TypeT>?
    ) {
        companion object {
            fun <TypeT> createMultiPoint(multiPoint: MultiPoint<TypeT>): TileGeometry<TypeT> {
                return TileGeometry(GeometryType.MULTI_POINT, multiPoint, null, null)
            }

            fun <TypeT> createMultiLineString(multiLineString: MultiLineString<TypeT>): TileGeometry<TypeT> {
                return TileGeometry(GeometryType.MULTI_LINESTRING, null, multiLineString, null)
            }

            fun <TypeT> createMultiPolygon(multiPolygon: MultiPolygon<TypeT>): TileGeometry<TypeT> {
                return TileGeometry(GeometryType.MULTI_POLYGON, null, null, multiPolygon)
            }
        }
    }

    enum class GeometryType {
        MULTI_POINT,
        MULTI_LINESTRING,
        MULTI_POLYGON;
    }
}

fun <TypeT> Typed.Polygon<TypeT>.limit(): Typed.Rectangle<TypeT> {
    return DoubleRectangles.boundingBox(
        this.asSequence()
            .flatten()
            .map { it }
            .asIterable()
    )
}

fun <TypeT> Typed.Rectangle<TypeT>.intersects(rect: Typed.Rectangle<TypeT>): Boolean {
    val t1 = origin
    val t2 = origin.add(dimension)
    val r1 = rect.origin
    val r2 = rect.origin.add(rect.dimension)
    return r2.x >= t1.x && t2.x >= r1.x && r2.y >= t1.y && t2.y >= r1.y
}

fun <TypeT> Typed.MultiPolygon<TypeT>.limit(): List<Typed.Rectangle<TypeT>> { return map { polygon -> polygon.limit() } }

class Generic
class LonLat

typealias Rectangle = Typed.Rectangle<Generic>

typealias Point = Typed.Vec<Generic>
typealias Ring = Typed.Ring<Generic>
typealias LineString = Typed.LineString<Generic>
typealias Polygon = Typed.Polygon<Generic>
typealias MultiPoint = Typed.MultiPoint<Generic>
typealias MultiLineString = Typed.MultiLineString<Generic>
typealias MultiPolygon = Typed.MultiPolygon<Generic>

typealias LonLatPoint = Typed.Vec<LonLat>
typealias LonLatRectangle = Typed.Rectangle<LonLat>

typealias AnyPoint = Typed.Vec<*>
typealias AnyLineString = Typed.LineString<*>

fun <TypeT> Typed.Vec<*>.reinterpret(): Typed.Vec<TypeT> = this as Typed.Vec<TypeT>
fun <TypeT> Typed.MultiPoint<*>.reinterpret(): Typed.MultiPoint<TypeT> = this as Typed.MultiPoint<TypeT>
fun <TypeT> Typed.LineString<*>.reinterpret(): Typed.LineString<TypeT> = this as Typed.LineString<TypeT>
fun <TypeT> Typed.MultiLineString<*>.reinterpret(): Typed.MultiLineString<TypeT> = this as Typed.MultiLineString<TypeT>
fun <TypeT> Typed.Polygon<*>.reinterpret(): Typed.Polygon<TypeT> = this as Typed.Polygon<TypeT>
fun <TypeT> Typed.MultiPolygon<*>.reinterpret(): Typed.MultiPolygon<TypeT> = this as Typed.MultiPolygon<TypeT>

/**
 * Create generic rectangle by any points
 */
fun erasedRectangle(origin: AnyPoint, dimension: AnyPoint): Rectangle {
    return Rectangle(
        origin as Point,
        dimension as Point
    )
}

fun <TypeT> newSpanRectangle(leftTop: Typed.Vec<TypeT>, rightBottom: Typed.Vec<TypeT>): Typed.Rectangle<TypeT> {
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
val <TypeT> Typed.Rectangle<TypeT>.center: Typed.Vec<TypeT> get() = origin.add(dimension.mul(0.5))

fun <TypeT> Typed.Vec<TypeT>.subX(p: Typed.Vec<TypeT>) = Typed.Vec<TypeT>(x - p.x, y)
fun <TypeT> Typed.Vec<TypeT>.subY(p: Typed.Vec<TypeT>) = Typed.Vec<TypeT>(x, y - p.y)
fun <TypeT> Typed.Vec<TypeT>.addX(p: Typed.Vec<TypeT>) = Typed.Vec<TypeT>(x + p.x, y)
fun <TypeT> Typed.Vec<TypeT>.addY(p: Typed.Vec<TypeT>) = Typed.Vec<TypeT>(x, y + p.y)
