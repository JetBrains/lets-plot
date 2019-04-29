package jetbrains.datalore.visualization.plot.gog.plot.layout

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.base.geometry.Vector
import kotlin.math.floor

object GeometryUtil {
    fun round(x: Double, y: Double): Vector {
        return Vector(Math.round(x).toInt(), Math.round(y).toInt())
    }

    fun round(v: DoubleVector): Vector {
        return round(v.x, v.y)
    }

    fun ceil(x: Double, y: Double): Vector {
        return Vector(
                Math.ceil(x).toInt(),
                Math.ceil(y).toInt()
        )
    }

    fun ceil(v: DoubleVector): Vector {
        return ceil(v.x, v.y)
    }

    fun containingRectangle(rect: DoubleRectangle): Rectangle {
        return containingRectangle(rect.origin, rect.dimension)
    }

    fun containingRectangle(origin: DoubleVector, dimension: DoubleVector): Rectangle {
        val left = floor(origin.x).toInt()
        val top = floor(origin.y).toInt()
        val right = Math.ceil(origin.x + dimension.x).toInt()
        val bottom = Math.ceil(origin.y + dimension.y).toInt()
        return Rectangle(left, top, right - left, bottom - top)
    }

    fun distance(vector: Vector, doubleVector: DoubleVector): Double {
        val dx = doubleVector.x - vector.x
        val dy = doubleVector.y - vector.y
        return Math.sqrt(dx * dx + dy * dy)
    }

    fun union(first: DoubleRectangle, optionalSecond: DoubleRectangle?): DoubleRectangle {
        return if (optionalSecond == null) {
            first
        } else first.union(optionalSecond)
    }

    fun union(head: DoubleRectangle, c: Collection<DoubleRectangle>): DoubleRectangle {
        var result = head
        for (r in c) {
            result = result.union(r)
        }
        return result
    }

    fun doubleRange(xRange: ClosedRange<Double>, yRange: ClosedRange<Double>): DoubleRectangle {
        val xOrigin = xRange.lowerEndpoint()
        val yOrigin = yRange.lowerEndpoint()
        val xSpan = xRange.upperEndpoint() - xRange.lowerEndpoint()
        val ySpan = yRange.upperEndpoint() - yRange.lowerEndpoint()
        return DoubleRectangle(xOrigin, yOrigin, xSpan, ySpan)
    }

    fun changeWidth(r: DoubleRectangle, width: Double): DoubleRectangle {
        return DoubleRectangle(
                r.origin.x,
                r.origin.y,
                width,
                r.dimension.y
        )
    }

    fun changeWidthKeepRight(r: DoubleRectangle, width: Double): DoubleRectangle {
        return DoubleRectangle(
                r.right - width,
                r.origin.y,
                width,
                r.dimension.y
        )
    }

    fun changeHeight(r: DoubleRectangle, height: Double): DoubleRectangle {
        return DoubleRectangle(
                r.origin.x,
                r.origin.y,
                r.dimension.x,
                height
        )
    }

    fun changeHeightKeepBottom(r: DoubleRectangle, height: Double): DoubleRectangle {
        return DoubleRectangle(
                r.origin.x,
                r.bottom - height,
                r.dimension.x,
                height
        )
    }
}
