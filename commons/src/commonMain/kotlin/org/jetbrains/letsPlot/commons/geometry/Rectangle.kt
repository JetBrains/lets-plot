/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.geometry

import org.jetbrains.letsPlot.commons.interval.IntSpan

class Rectangle(val origin: Vector, val dimension: Vector) {

    val isEmpty: Boolean get() = (dimension.x <= 0) || (dimension.y <= 0)

    val boundSegments: Array<Segment>
        get() {
            val p = boundPoints
            return arrayOf<Segment>(Segment(p[0], p[1]), Segment(p[1], p[2]), Segment(p[2], p[3]), Segment(p[3], p[0]))
        }

    private val boundPoints: Array<Vector>
        get() = arrayOf(
            origin,
            origin.add(Vector(dimension.x, 0)),
            origin.add(dimension),
            origin.add(Vector(0, dimension.y))
        )

    constructor(x: Int, y: Int, width: Int, height: Int) : this(Vector(x, y), Vector(width, height))

    fun add(v: Vector): Rectangle {
        return Rectangle(origin.add(v), dimension)
    }

    fun sub(v: Vector): Rectangle {
        return Rectangle(origin.sub(v), dimension)
    }

    operator fun contains(r: Rectangle): Boolean {
        return contains(r.origin) && contains(r.origin.add(r.dimension))
    }

    operator fun contains(v: Vector): Boolean {
        return origin.x <= v.x && origin.x + dimension.x >= v.x && origin.y <= v.y && origin.y + dimension.y >= v.y
    }

    fun union(rect: Rectangle): Rectangle {
        val newOrigin = origin.min(rect.origin)
        val corner = origin.add(dimension)
        val rectCorner = rect.origin.add(rect.dimension)
        val newCorner = corner.max(rectCorner)
        val newDimension = newCorner.sub(newOrigin)
        return Rectangle(newOrigin, newDimension)
    }

    fun intersects(rect: Rectangle): Boolean {
        val t1 = origin
        val t2 = origin.add(dimension)
        val r1 = rect.origin
        val r2 = rect.origin.add(rect.dimension)
        return r2.x >= t1.x && t2.x >= r1.x && r2.y >= t1.y && t2.y >= r1.y
    }

    fun intersect(r: Rectangle): Rectangle {
        if (!intersects(r)) {
            throw IllegalStateException("rectangle [$this] doesn't intersect [$r]")
        }

        val too = origin.add(dimension)
        val roo = r.origin.add(r.dimension)
        val ioo = too.min(roo)

        val io = origin.max(r.origin)
        return Rectangle(io, ioo.sub(io))
    }

    fun innerIntersects(rect: Rectangle): Boolean {
        val t1 = origin
        val t2 = origin.add(dimension)
        val r1 = rect.origin
        val r2 = rect.origin.add(rect.dimension)
        return r2.x > t1.x && t2.x > r1.x && r2.y > t1.y && t2.y > r1.y
    }

    fun changeDimension(dim: Vector): Rectangle {
        return Rectangle(origin, dim)
    }

    fun distance(to: Vector): Double {
        return toDoubleRectangle().distance(to.toDoubleVector())
    }

    fun xRange(): IntSpan {
        return IntSpan(origin.x, origin.x + dimension.x)
    }

    fun yRange(): IntSpan {
        return IntSpan(origin.y, origin.y + dimension.y)
    }

    override fun hashCode(): Int {
        return origin.hashCode() * 31 + dimension.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Rectangle) return false

        val otherRect = other as Rectangle?
        return origin == otherRect!!.origin && dimension == otherRect.dimension
    }

    private fun toDoubleRectangle(): DoubleRectangle {
        return DoubleRectangle(origin.toDoubleVector(), dimension.toDoubleVector())
    }

    fun center(): Vector {
        return origin.add(Vector(dimension.x / 2, dimension.y / 2))
    }

    override fun toString(): String {
        return "$origin - $dimension"
    }

}
