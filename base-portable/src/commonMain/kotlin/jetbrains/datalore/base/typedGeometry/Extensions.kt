/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.typedGeometry

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import kotlin.math.max
import kotlin.math.min

class Untyped

@Suppress("UNCHECKED_CAST")
fun <TypeT> Vec<Untyped>.reinterpret(): Vec<TypeT> = this as Vec<TypeT>

@Suppress("UNCHECKED_CAST")
fun <TypeT> MultiPoint<Untyped>.reinterpret(): MultiPoint<TypeT> = this as MultiPoint<TypeT>

@Suppress("UNCHECKED_CAST")
fun <TypeT> LineString<Untyped>.reinterpret(): LineString<TypeT> = this as LineString<TypeT>

@Suppress("UNCHECKED_CAST")
fun <TypeT> MultiLineString<Untyped>.reinterpret(): MultiLineString<TypeT> = this as MultiLineString<TypeT>

@Suppress("UNCHECKED_CAST")
fun <TypeT> Polygon<Untyped>.reinterpret(): Polygon<TypeT> = this as Polygon<TypeT>

@Suppress("UNCHECKED_CAST")
fun <TypeT> MultiPolygon<Untyped>.reinterpret(): MultiPolygon<TypeT> = this as MultiPolygon<TypeT>

val Rect<*>.bottom: Double get() = origin.y + dimension.y
val Rect<*>.right: Double get() = origin.x + dimension.x
val Rect<*>.height: Double get() = dimension.y
val Rect<*>.width: Double get() = dimension.x
val Rect<*>.top: Double get() = origin.y
val Rect<*>.left: Double get() = origin.x

val <TypeT> Rect<TypeT>.scalarBottom: Scalar<TypeT> get() = Scalar(bottom)
val <TypeT> Rect<TypeT>.scalarRight: Scalar<TypeT> get() = Scalar(right)
val <TypeT> Rect<TypeT>.scalarHeight: Scalar<TypeT> get() = Scalar(height)
val <TypeT> Rect<TypeT>.scalarWidth: Scalar<TypeT> get() = Scalar(width)
val <TypeT> Rect<TypeT>.scalarTop: Scalar<TypeT> get() = Scalar(top)
val <TypeT> Rect<TypeT>.scalarLeft: Scalar<TypeT> get() = Scalar(left)

val <TypeT> Rect<TypeT>.center: Vec<TypeT> get() = dimension / 2.0 + origin
val <TypeT> Rect<TypeT>.rightBottom: Vec<TypeT> get() = origin + dimension
fun <TypeT> Rect<TypeT>.toPolygon(): Polygon<TypeT> {
    val points = ArrayList<Vec<TypeT>>()
    points.add(origin)
    points.add(origin.transform(newX = { it + scalarWidth }))
    points.add(origin + dimension)
    points.add(origin.transform(newY = { it + scalarHeight }))
    points.add(origin)
    return Polygon(listOf(Ring(points)))
}

val <TypeT> Vec<TypeT>.scalarX get(): Scalar<TypeT> = Scalar(x)
val <TypeT> Vec<TypeT>.scalarY get(): Scalar<TypeT> = Scalar(y)

operator fun <TypeT> Vec<TypeT>.plus(other: Vec<TypeT>): Vec<TypeT> = Vec(x + other.x, y + other.y)
operator fun <TypeT> Vec<TypeT>.minus(other: Vec<TypeT>): Vec<TypeT> = Vec(x - other.x, y - other.y)
operator fun <TypeT> Vec<TypeT>.times(other: Vec<TypeT>): Vec<TypeT> = Vec(x * other.x, y * other.y)
operator fun <TypeT> Vec<TypeT>.div(other: Vec<TypeT>): Vec<TypeT> = Vec(x / other.x, y / other.y)

operator fun <TypeT> Vec<TypeT>.times(scale: Double): Vec<TypeT> = Vec(x * scale, y * scale)
operator fun <TypeT> Vec<TypeT>.div(scale: Double): Vec<TypeT> = Vec(x / scale, y / scale)
operator fun <TypeT> Vec<TypeT>.unaryMinus(): Vec<TypeT> = Vec(-x, -y)
fun <TypeT> Vec<TypeT>.min(other: Vec<TypeT>):Vec<TypeT> = Vec(min(x, other.x), min(y, other.y))
fun <TypeT> Vec<TypeT>.max(other: Vec<TypeT>):Vec<TypeT> = Vec(max(x, other.x), max(y, other.y))

fun <TypeT> Vec<TypeT>.transform(
    newX: (Scalar<TypeT>) -> Scalar<TypeT> = { it },
    newY: (Scalar<TypeT>) -> Scalar<TypeT> = { it }
) = Vec<TypeT>(newX(scalarX).value, newY(scalarY).value)

operator fun <T> Scalar<T>.plus(other: Scalar<T>): Scalar<T> = Scalar(value + other.value)
operator fun <T> Scalar<T>.minus(other: Scalar<T>): Scalar<T> = Scalar(value - other.value)
operator fun <T> Scalar<T>.times(other: Scalar<T>): Scalar<T> = Scalar(value * other.value)

operator fun <T> Scalar<T>.div(other: Scalar<T>): Scalar<T> = Scalar(value / other.value)
operator fun <T> Scalar<T>.div(other: Double): Scalar<T> = Scalar(value / other)
operator fun <T> Scalar<T>.times(other: Double): Scalar<T> = Scalar(value * other)
operator fun <T> Scalar<T>.unaryMinus(): Scalar<T> = Scalar(-value)

operator fun <T> Scalar<T>.compareTo(i: Int) = value.compareTo(i)


fun <TypeT> Rect<TypeT>.contains(v: Vec<TypeT>): Boolean {
    return origin.x <= v.x && origin.x + dimension.x >= v.x && origin.y <= v.y && origin.y + dimension.y >= v.y
}

fun <TypeT> Rect<TypeT>.union(other: Rect<TypeT>) = Rect.LTRB(
    origin.min(other.origin),
    rightBottom.max(other.rightBottom)
)

fun <TypeT> List<Rect<TypeT>?>.union(): Rect<TypeT>? {
    return fold(firstOrNull()) { acc, box ->
        when {
            acc != null && box != null -> acc.union(box)
            acc == null && box == null -> null
            acc != null -> acc
            box != null -> box
            else -> error("Unreachable")
        }
    }
}

fun <TypeT> Rect<TypeT>.intersects(rect: Rect<TypeT>): Boolean {
    val t1 = origin
    val t2 = origin + dimension
    val r1 = rect.origin
    val r2 = rect.origin + rect.dimension
    return r2.x >= t1.x && t2.x >= r1.x && r2.y >= t1.y && t2.y >= r1.y
}

fun Rect<*>.xRange() = DoubleSpan(origin.x, origin.x + dimension.x)
fun Rect<*>.yRange() = DoubleSpan(origin.y, origin.y + dimension.y)

fun <TypeT> finiteVecOrNull(x: Double, y: Double): Vec<TypeT>?  = when {
    x.isFinite() && y.isFinite() -> explicitVec(x, y)
    else -> null
}

fun Vec<*>.toDoubleVector() = DoubleVector(x, y)
fun <T> DoubleVector.toVec() = Vec<T>(x, y)
fun <T> DoubleRectangle.toRect() = Rect.XYWH<T>(left, top, width, height)
fun <T> Rect<T>.toDoubleRectangle() = DoubleRectangle(left, top, width, height)