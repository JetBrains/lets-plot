/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.geometry

import jetbrains.datalore.base.gcommon.collect.ClosedRange

class DoubleRectangle(val origin: DoubleVector, val dimension: DoubleVector) {

    val center: DoubleVector
        get() = origin.add(dimension.mul(0.5))

    val left: Double
        get() = origin.x

    val right: Double
        get() = origin.x + dimension.x

    val top: Double
        get() = origin.y

    val bottom: Double
        get() = origin.y + dimension.y

    val width: Double
        get() = dimension.x

    val height: Double
        get() = dimension.y

    val parts: Iterable<DoubleSegment>
        get() {
            val result = ArrayList<DoubleSegment>()
            result.add(DoubleSegment(origin, origin.add(DoubleVector(dimension.x, 0.0))))
            result.add(DoubleSegment(origin, origin.add(DoubleVector(0.0, dimension.y))))
            result.add(DoubleSegment(origin.add(dimension), origin.add(DoubleVector(dimension.x, 0.0))))
            result.add(DoubleSegment(origin.add(dimension), origin.add(DoubleVector(0.0, dimension.y))))
            return result
        }

    constructor(x: Double, y: Double, w: Double, h: Double) : this(DoubleVector(x, y), DoubleVector(w, h))

    constructor(xRange: ClosedRange<Double>, yRange: ClosedRange<Double>) : this(
        xRange.lowerEnd, yRange.lowerEnd,
        xRange.upperEnd - xRange.lowerEnd, yRange.upperEnd - yRange.lowerEnd
    )

    fun xRange(): ClosedRange<Double> {
        return ClosedRange(origin.x, origin.x + dimension.x)
    }

    fun yRange(): ClosedRange<Double> {
        return ClosedRange(origin.y, origin.y + dimension.y)
    }

    operator fun contains(v: DoubleVector): Boolean {
        return origin.x <= v.x && origin.x + dimension.x >= v.x && origin.y <= v.y && origin.y + dimension.y >= v.y
    }

    fun flip(): DoubleRectangle {
        return DoubleRectangle(
            origin.flip(),
            dimension.flip()
        )
    }

    fun union(rect: DoubleRectangle): DoubleRectangle {
        val newOrigin = origin.min(rect.origin)
        val corner = origin.add(dimension)
        val rectCorner = rect.origin.add(rect.dimension)
        val newCorner = corner.max(rectCorner)
        val newDimension = newCorner.subtract(newOrigin)
        return DoubleRectangle(newOrigin, newDimension)
    }

    fun intersects(rect: DoubleRectangle): Boolean {
        val t1 = origin
        val t2 = origin.add(dimension)
        val r1 = rect.origin
        val r2 = rect.origin.add(rect.dimension)
        return r2.x >= t1.x && t2.x >= r1.x && r2.y >= t1.y && t2.y >= r1.y
    }

    fun intersect(r: DoubleRectangle): DoubleRectangle? {
        val t1 = origin
        val t2 = origin.add(dimension)
        val r1 = r.origin
        val r2 = r.origin.add(r.dimension)

        val res1 = t1.max(r1)
        val res2 = t2.min(r2)

        val dim = res2.subtract(res1)

        return if (dim.x < 0 || dim.y < 0) {
            null
        } else DoubleRectangle(res1, dim)

    }

    fun add(v: DoubleVector): DoubleRectangle {
        return DoubleRectangle(origin.add(v), dimension)
    }

    fun subtract(v: DoubleVector): DoubleRectangle {
        return DoubleRectangle(origin.subtract(v), dimension)
    }

    fun distance(to: DoubleVector): Double {
        var result = 0.0
        var hasResult = false
        for (s in parts) {
            if (!hasResult) {
                result = s.distance(to)
                hasResult = true
            } else {
                val distance = s.distance(to)
                if (distance < result) {
                    result = distance
                }
            }
        }
        return result
    }

    override fun hashCode(): Int {
        return origin.hashCode() * 31 + dimension.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is DoubleRectangle) {
            return false
        }
        val r = other as DoubleRectangle?
        return r!!.origin.equals(origin) && r.dimension.equals(dimension)
    }

    override fun toString(): String {
        return "[rect $origin, $dimension]"
    }

    companion object {
        fun span(leftTop: DoubleVector, rightBottom: DoubleVector): DoubleRectangle {
            return DoubleRectangle(leftTop, rightBottom.subtract(leftTop))
        }
    }
}