/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.geometry

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import kotlin.math.max
import kotlin.math.min

class DoubleRectangle(val origin: DoubleVector, val dimension: DoubleVector) {

    // ToDo: this breaks TooltipBox
//    init {
//        check(dimension.x >= 0 && dimension.y >= 0) { "Rectangle dimentions should be positive: $dimension" }
//    }

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

    // top, left, right, bottom
    val parts: Iterable<DoubleSegment>
        get() {
            val result = ArrayList<DoubleSegment>()
            result.add(DoubleSegment(origin, origin.add(DoubleVector(dimension.x, 0.0))))
            result.add(DoubleSegment(origin, origin.add(DoubleVector(0.0, dimension.y))))
            result.add(DoubleSegment(origin.add(dimension), origin.add(DoubleVector(dimension.x, 0.0))))
            result.add(DoubleSegment(origin.add(dimension), origin.add(DoubleVector(0.0, dimension.y))))
            return result
        }

    val points: List<DoubleVector>
        get() = listOf(
            origin,
            origin.add(DoubleVector(dimension.x, 0.0)),
            origin.add(dimension),
            origin.add(DoubleVector(0.0, dimension.y)),
            origin
        )

    constructor(x: Double, y: Double, w: Double, h: Double) : this(DoubleVector(x, y), DoubleVector(w, h))

    constructor(xRange: DoubleSpan, yRange: DoubleSpan) : this(
        xRange.lowerEnd, yRange.lowerEnd,
        xRange.length, yRange.length
    )

    fun xRange(): DoubleSpan {
        return DoubleSpan(origin.x, origin.x + dimension.x)
    }

    fun yRange(): DoubleSpan {
        return DoubleSpan(origin.y, origin.y + dimension.y)
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

    fun flipIf(flipped: Boolean): DoubleRectangle {
        return if (flipped) flip() else this
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

    fun shrinkToAspectRatio(targetRatio: DoubleVector): DoubleRectangle {
        check(targetRatio.x > 0 && targetRatio.y > 0)
        val aspectRatio = targetRatio.x / targetRatio.y
        val newSize = if (aspectRatio >= 1.0) {
            val newHeight = width / aspectRatio
            val scaling = if (newHeight > height) height / newHeight else 1.0
            DoubleVector(width * scaling, newHeight * scaling)
        } else {
            val newWidth = height * aspectRatio
            val scaling = if (newWidth > width) width / newWidth else 1.0
            DoubleVector(newWidth * scaling, height * scaling)
        }

        // The srinked rect has the same center as this one.
        val newOrigin = DoubleVector(
            x = origin.x + (width - newSize.x) / 2,
            y = origin.y + (height - newSize.y) / 2,
        )
        return DoubleRectangle(newOrigin, newSize)
    }

    fun inflate(delta: Double): DoubleRectangle {
        return DoubleRectangle(
            origin.subtract(DoubleVector(delta, delta)),
            dimension.add(DoubleVector(delta * 2, delta * 2))
        )
    }

    fun rotate(angle: Double, around: DoubleVector): List<DoubleVector> {
        val lt = origin.rotateAround(around, angle)
        val lb = DoubleVector(left, bottom).rotateAround(around, angle)
        val rt = DoubleVector(right, top).rotateAround(around, angle)
        val rb = DoubleVector(right, bottom).rotateAround(around, angle)
        return listOf(lt, lb, rb, rt)
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
            val x0 = min(leftTop.x, rightBottom.x)
            val x1 = max(leftTop.x, rightBottom.x)
            val y0 = min(leftTop.y, rightBottom.y)
            val y1 = max(leftTop.y, rightBottom.y)
            return DoubleRectangle(x0, y0, x1 - x0, y1 - y0)
        }

        @Suppress("FunctionName")
        fun LTRB(left: Number, top: Number, right: Number, bottom: Number): DoubleRectangle {
            return DoubleRectangle(
                left.toDouble(),
                top.toDouble(),
                right.toDouble() - left.toDouble(),
                bottom.toDouble() - top.toDouble()
            )
        }

        @Suppress("FunctionName")
        fun XYWH(x: Number, y: Number, width: Number, height: Number): DoubleRectangle {
            return DoubleRectangle(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
        }

        fun hvRange(hRange: DoubleSpan, vRange: DoubleSpan): DoubleRectangle {
            return DoubleRectangle(
                hRange.lowerEnd,
                vRange.lowerEnd,
                hRange.length,
                vRange.length
            )
        }
    }
}