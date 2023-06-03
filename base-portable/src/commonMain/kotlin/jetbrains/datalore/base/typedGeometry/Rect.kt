/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.typedGeometry

import jetbrains.datalore.base.interval.DoubleSpan

data class Rect<TypeT>(
    val origin: Vec<TypeT>,
    val dimension: Vec<TypeT>
) {

    companion object {
        fun <TypeT> LTRB(left: Double, top: Double, right: Double, bottom: Double): Rect<TypeT> {
            return Rect(Vec(left, top), Vec(right-left, bottom-top))
        }

        fun <TypeT> LTRB(leftTop: Vec<TypeT>, rightBottom: Vec<TypeT>): Rect<TypeT> {
            return Rect(leftTop, rightBottom - leftTop)
        }

        fun <TypeT> XYWH(x: Double, y: Double, width: Double, height: Double): Rect<TypeT> {
            return Rect(Vec(x, y), Vec(width, height))
        }

        fun <TypeT> XYWH(origin: Vec<TypeT>, dimension: Vec<TypeT>): Rect<TypeT> {
            return Rect(origin, dimension)
        }
    }
}

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

fun Rect<*>.xRange() = DoubleSpan(origin.x, origin.x + dimension.x)
fun Rect<*>.yRange() = DoubleSpan(origin.y, origin.y + dimension.y)
fun <TypeT> Rect<TypeT>.toPolygon(): Polygon<TypeT> {
    val points = ArrayList<Vec<TypeT>>()
    points.add(origin)
    points.add(origin.transform(newX = { it + scalarWidth }))
    points.add(origin + dimension)
    points.add(origin.transform(newY = { it + scalarHeight }))
    points.add(origin)
    return Polygon(Ring(points))
}


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

fun <TypeT> Rect<TypeT>.inflate(dim: Vec<TypeT>): Rect<TypeT> {
    return Rect.LTRB(origin - dim, rightBottom + dim)
}

fun <TypeT> Rect<TypeT>.inflate(dim: Scalar<TypeT>) = inflate(newVec(dim, dim))
fun <TypeT> Rect<TypeT>.inflate(dim: Number) = inflate(Vec(dim, dim))
