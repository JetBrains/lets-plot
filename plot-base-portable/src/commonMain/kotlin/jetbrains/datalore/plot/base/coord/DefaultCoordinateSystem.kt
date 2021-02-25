/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.gcommon.collect.Comparables.max
import jetbrains.datalore.base.gcommon.collect.Comparables.min
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleRectangles
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.geom.util.GeomCoord

internal class DefaultCoordinateSystem(
    private val myToClientOffsetX: (Double) -> Double,
    private val myToClientOffsetY: (Double) -> Double,
    private val myFromClientOffsetX: (Double) -> Double,
    private val myFromClientOffsetY: (Double) -> Double,
    private val xLim: ClosedRange<Double>?,
    private val yLim: ClosedRange<Double>?
) :
    CoordinateSystem {

    override fun toClient(p: DoubleVector): DoubleVector {
        return DoubleVector(myToClientOffsetX(p.x), myToClientOffsetY(p.y))
    }

    override fun fromClient(p: DoubleVector): DoubleVector {
        return DoubleVector(myFromClientOffsetX(p.x), myFromClientOffsetY(p.y))
    }

    override fun isPointInLimits(p: DoubleVector): Boolean {
        val coord = fromClient(p)
        return (xLim?.contains(coord.x) ?: true) && (yLim?.contains(coord.y) ?: true)
    }

    override fun isRectInLimits(rect: DoubleRectangle): Boolean {
        val fromClientRect = GeomCoord(this).fromClient(rect)
        return (xLim?.encloses(fromClientRect.xRange()) ?: true) && (yLim?.encloses(fromClientRect.yRange()) ?: true)
    }

    override fun isPathInLimits(path: List<DoubleVector>): Boolean {
        return path.any(::isPointInLimits)
    }

    override fun isPolygonInLimits(polygon: List<DoubleVector>): Boolean {
        val bbox = DoubleRectangles.boundingBox(polygon)
        return isRectInLimits(bbox)
    }

    override val xLimitRange: ClosedRange<Double>?
        get() = xLim?.let { range -> convertRange(range, myToClientOffsetX) }

    override val yLimitRange: ClosedRange<Double>?
        get() = yLim?.let { range -> convertRange(range, myToClientOffsetY) }

    private fun convertRange(range: ClosedRange<Double>, translate: (Double) -> Double): ClosedRange<Double> {
        val l = translate(range.lowerEnd)
        val u = translate(range.upperEnd)
        return ClosedRange(
            min(l, u),
            max(l, u),
        )
    }
}
