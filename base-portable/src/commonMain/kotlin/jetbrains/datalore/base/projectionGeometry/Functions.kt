package jetbrains.datalore.base.projectionGeometry

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangles

fun <TypeT> Polygon<TypeT>.limit(): Rect<TypeT> {
    return DoubleRectangles.boundingBox(
        this.asSequence()
            .flatten()
            .map { it }
            .asIterable()
    )
}

fun <TypeT> Rect<TypeT>.intersects(rect: Rect<TypeT>): Boolean {
    val t1 = origin
    val t2 = origin.add(dimension)
    val r1 = rect.origin
    val r2 = rect.origin.add(rect.dimension)
    return r2.x >= t1.x && t2.x >= r1.x && r2.y >= t1.y && t2.y >= r1.y
}

fun <TypeT> MultiPolygon<TypeT>.limit(): List<Rect<TypeT>> { return map { polygon -> polygon.limit() } }

class Generic
class LonLat

typealias Point = Vec<Generic>

typealias AnyPoint = Vec<*>
typealias AnyLineString = LineString<*>

fun <TypeT> Vec<*>.reinterpret(): Vec<TypeT> = this as Vec<TypeT>
fun <TypeT> MultiPoint<*>.reinterpret(): MultiPoint<TypeT> = this as MultiPoint<TypeT>
fun <TypeT> LineString<*>.reinterpret(): LineString<TypeT> = this as LineString<TypeT>
fun <TypeT> MultiLineString<*>.reinterpret(): MultiLineString<TypeT> = this as MultiLineString<TypeT>
fun <TypeT> Polygon<*>.reinterpret(): Polygon<TypeT> = this as Polygon<TypeT>
fun <TypeT> MultiPolygon<*>.reinterpret(): MultiPolygon<TypeT> = this as MultiPolygon<TypeT>

/**
 * Create generic rectangle by any points
 */
fun erasedRectangle(origin: AnyPoint, dimension: AnyPoint): Rect<Generic> {
    return Rect<Generic>(
        origin as Point,
        dimension as Point
    )
}

fun <TypeT> newSpanRectangle(leftTop: Vec<TypeT>, rightBottom: Vec<TypeT>): Rect<TypeT> {
    return Rect(leftTop, rightBottom.subtract(leftTop))
}

fun Rect<*>.xRange() = ClosedRange.closed(origin.x, origin.x + dimension.x)
fun Rect<*>.yRange() = ClosedRange.closed(origin.y, origin.y + dimension.y)

val Rect<*>.bottom: Double get() = origin.y + dimension.y
val Rect<*>.right: Double get() = origin.x + dimension.x
val Rect<*>.height: Double get() = dimension.y
val Rect<*>.width: Double get() = dimension.x
val Rect<*>.top: Double get() = origin.y
val Rect<*>.left: Double get() = origin.x
val <TypeT> Rect<TypeT>.center: Vec<TypeT> get() = origin.add(dimension.mul(0.5))

fun <TypeT> Vec<TypeT>.subX(p: Vec<TypeT>) =
    Vec<TypeT>(x - p.x, y)
fun <TypeT> Vec<TypeT>.subY(p: Vec<TypeT>) =
    Vec<TypeT>(x, y - p.y)
fun <TypeT> Vec<TypeT>.addX(p: Vec<TypeT>) =
    Vec<TypeT>(x + p.x, y)
fun <TypeT> Vec<TypeT>.addY(p: Vec<TypeT>) =
    Vec<TypeT>(x, y + p.y)

fun <TypeT> Vec<TypeT>.transform(fx: (Double) -> Double = { it }, fy: (Double) -> Double = { it }) =
    Vec<TypeT>(fx(x), fy(y))