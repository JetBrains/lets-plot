/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.projectionGeometry

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangles

class Generic

fun <TypeT> Vec<Generic>.reinterpret(): Vec<TypeT> = this as Vec<TypeT>
fun <TypeT> MultiPoint<Generic>.reinterpret(): MultiPoint<TypeT> = this as MultiPoint<TypeT>
fun <TypeT> LineString<Generic>.reinterpret(): LineString<TypeT> = this as LineString<TypeT>
fun <TypeT> MultiLineString<Generic>.reinterpret(): MultiLineString<TypeT> = this as MultiLineString<TypeT>
fun <TypeT> Polygon<Generic>.reinterpret(): Polygon<TypeT> = this as Polygon<TypeT>
fun <TypeT> MultiPolygon<Generic>.reinterpret(): MultiPolygon<TypeT> = this as MultiPolygon<TypeT>

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

val <TypeT> Vec<TypeT>.scalarX get(): Scalar<TypeT> = Scalar(x)
val <TypeT> Vec<TypeT>.scalarY get(): Scalar<TypeT> = Scalar(y)

operator fun <TypeT> Vec<TypeT>.plus(other: Vec<TypeT>): Vec<TypeT> = Vec(x + other.x, y + other.y)
operator fun <TypeT> Vec<TypeT>.minus(other: Vec<TypeT>): Vec<TypeT> = Vec(x - other.x, y - other.y)
operator fun <TypeT> Vec<TypeT>.times(other: Vec<TypeT>): Vec<TypeT> = Vec(x * other.x, y * other.y)
operator fun <TypeT> Vec<TypeT>.div(other: Vec<TypeT>): Vec<TypeT> = Vec(x / other.x, y / other.y)

operator fun <TypeT> Vec<TypeT>.times(scale: Double): Vec<TypeT> = Vec(x * scale, y * scale)
operator fun <TypeT> Vec<TypeT>.div(scale: Double): Vec<TypeT> = Vec(x / scale, y / scale)
operator fun <TypeT> Vec<TypeT>.unaryMinus(): Vec<TypeT> = Vec(-x, -y)

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


fun <TypeT> newSpanRectangle(leftTop: Vec<TypeT>, rightBottom: Vec<TypeT>): Rect<TypeT> {
    return Rect(leftTop, rightBottom - leftTop)
}

fun <TypeT> Polygon<TypeT>.limit(): Rect<TypeT> {
    return DoubleRectangles.boundingBox(asSequence().flatten().asIterable())
}

fun <TypeT> Rect<TypeT>.intersects(rect: Rect<TypeT>): Boolean {
    val t1 = origin
    val t2 = origin + dimension
    val r1 = rect.origin
    val r2 = rect.origin + rect.dimension
    return r2.x >= t1.x && t2.x >= r1.x && r2.y >= t1.y && t2.y >= r1.y
}

fun Rect<*>.xRange() = ClosedRange.closed(origin.x, origin.x + dimension.x)
fun Rect<*>.yRange() = ClosedRange.closed(origin.y, origin.y + dimension.y)

fun <TypeT> MultiPolygon<TypeT>.limit(): List<Rect<TypeT>> { return map { polygon -> polygon.limit() } }
