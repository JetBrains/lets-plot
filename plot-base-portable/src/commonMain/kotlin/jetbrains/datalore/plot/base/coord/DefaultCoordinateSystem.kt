/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.gcommon.collect.Comparables.max
import jetbrains.datalore.base.gcommon.collect.Comparables.min
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.CoordinateSystem

internal open class DefaultCoordinateSystem(
    val toClientOffsetX: (Double) -> Double,
    val toClientOffsetY: (Double) -> Double,
    val fromClientOffsetX: (Double) -> Double,
    val fromClientOffsetY: (Double) -> Double,
    val xLimits: ClosedRange<Double>?,
    val yLimits: ClosedRange<Double>?
) : CoordinateSystem {

    override fun toClient(p: DoubleVector): DoubleVector {
        return DoubleVector(toClientOffsetX(p.x), toClientOffsetY(p.y))
    }

    override fun fromClient(p: DoubleVector): DoubleVector {
        return DoubleVector(fromClientOffsetX(p.x), fromClientOffsetY(p.y))
    }

//    override fun isPointInLimits(p: DoubleVector, isClient: Boolean): Boolean {
//        val coord = if (isClient) fromClient(p) else p
//        return (xLim?.contains(coord.x) ?: true) && (yLim?.contains(coord.y) ?: true)
//    }
//
//    override fun isRectInLimits(rect: DoubleRectangle, isClient: Boolean): Boolean {
//        val r = if (isClient) GeomCoord(this).fromClient(rect) else rect
//        return (xLim?.encloses(r.xRange()) ?: true) && (yLim?.encloses(r.yRange()) ?: true)
//    }
//
//    override fun isPathInLimits(path: List<DoubleVector>): Boolean {
//        return path.any { point -> isPointInLimits(point) }
//    }
//
//    override fun isPolygonInLimits(polygon: List<DoubleVector>): Boolean {
//        val bbox = DoubleRectangles.boundingBox(polygon)
//        return isRectInLimits(bbox)
//    }

    override fun applyClientLimits(clientBounds: DoubleRectangle): DoubleRectangle {
        val hRange = xLimits?.let { lim -> convertRange(lim, toClientOffsetX) }
            ?: clientBounds.xRange()
        val vRange = yLimits?.let { lim -> convertRange(lim, toClientOffsetY) }
            ?: clientBounds.yRange()
        return DoubleRectangle(hRange, vRange)
    }

    override fun flip(): CoordinateSystem {
        return FlippedCoordinateSystem(this)
    }


    companion object {
        private fun convertRange(range: ClosedRange<Double>, transform: (Double) -> Double): ClosedRange<Double> {
            val l = transform(range.lowerEnd)
            val u = transform(range.upperEnd)
            return ClosedRange(
                min(l, u),
                max(l, u),
            )
        }
    }
}
