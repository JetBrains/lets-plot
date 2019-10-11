package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle

object GeometryUtil {
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
