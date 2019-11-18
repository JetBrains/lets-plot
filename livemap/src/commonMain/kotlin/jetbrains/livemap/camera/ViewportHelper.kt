/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.camera

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.datalore.base.spatial.GeoBoundingBoxCalculator
import jetbrains.datalore.base.spatial.GeoUtils.deltaOnLoop
import jetbrains.livemap.projections.MapRuler
import jetbrains.livemap.projections.World
import jetbrains.livemap.projections.WorldPoint
import jetbrains.livemap.projections.WorldRectangle
import jetbrains.livemap.tiles.CellKey
import jetbrains.livemap.tiles.calculateCellKeys
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class ViewportHelper<TypeT>(
    private val myMapRect: Rect<TypeT>,
    private val myLoopX: Boolean,
    private val myLoopY: Boolean
) : ViewportMath, MapRuler<TypeT> {
    private fun splitRange(
        range: ClosedRange<Double>,
        mapRange: ClosedRange<Double>,
        loop: Boolean
    ): List<ClosedRange<Double>> {
        val xRanges = ArrayList<ClosedRange<Double>>()
        var lower = range.lowerEndpoint()
        var upper = range.upperEndpoint()

        if (lower < mapRange.lowerEndpoint()) {
            if (loop && upper < mapRange.upperEndpoint()) {
                val newLeft = max(lower + length(mapRange), upper)
                xRanges.add(ClosedRange.closed(newLeft, mapRange.upperEndpoint()))
            }
            lower = mapRange.lowerEndpoint()
        }

        if (mapRange.upperEndpoint() < upper) {
            if (loop && mapRange.lowerEndpoint() < lower) {
                val newRight = min(upper - length(mapRange), lower)
                xRanges.add(ClosedRange.closed(mapRange.lowerEndpoint(), newRight))
            }
            upper = mapRange.upperEndpoint()
        }

        xRanges.add(ClosedRange.closed(lower, upper))
        return xRanges
    }

    private fun normalize(v: Double, min: Double, max: Double, loop: Boolean): Double {
        if (!loop) {
            return max(min, min(v, max))
        }

        val len = max - min
        var result = v - (v / len).toInt() * len

        if (result > max) {
            result -= len
        }
        if (result < min) {
            result += len
        }

        return result
    }

    private fun getOrigins(
        objRange: ClosedRange<Double>,
        mapRange: ClosedRange<Double>,
        viewRange: ClosedRange<Double>,
        loop: Boolean
    ): List<Double> {
        if (!loop) {
            return if (objRange.isConnected(viewRange)) listOf(objRange.lowerEndpoint()) else emptyList()
        }

        val mapRangeLen = length(mapRange)

        val n = floor((viewRange.lowerEndpoint() - mapRange.lowerEndpoint()) / mapRangeLen).toInt()
        var origin = mapRange.lowerEndpoint() + n * mapRangeLen + objRange.lowerEndpoint()

        if (origin + length(objRange) < viewRange.lowerEndpoint()) {
            origin += mapRangeLen
        }

        val result = ArrayList<Double>()
        while (origin < viewRange.upperEndpoint()) {
            result.add(origin)
            origin += mapRangeLen
        }
        return result
    }

    private fun length(mapRange: ClosedRange<Double>): Double {
        return mapRange.upperEndpoint() - mapRange.lowerEndpoint()
    }

    override fun deltaX(x1: Double, x2: Double): Double {
        return if (myLoopX) deltaOnLoop(x1, x2, myMapRect.width) else x2 - x1
    }

    override fun deltaY(y1: Double, y2: Double): Double {
        return if (myLoopY) deltaOnLoop(y1, y2, myMapRect.height) else y2 - y1
    }

    override fun distanceX(x1: Double, x2: Double): Double {
        return abs(deltaX(x1, x2))
    }

    override fun distanceY(y1: Double, y2: Double): Double {
        return abs(deltaY(y1, y2))
    }

    override fun calculateBoundingBox(xyRects: List<Rect<TypeT>>): Rect<TypeT> {
        return GeoBoundingBoxCalculator(myMapRect, myLoopX, myLoopY).calculateBoundingBoxFromRectangles(xyRects)
    }

    override fun normalizeX(x: Double): Double {
        return normalize(x, myMapRect.left, myMapRect.right, myLoopX)
    }

    override fun normalizeY(y: Double): Double {
        return normalize(y, myMapRect.top, myMapRect.bottom, myLoopY)
    }

    override fun getOrigins(objRect: WorldRectangle, viewRect: WorldRectangle): List<WorldPoint> {
        val xOrigins = getOrigins(objRect.xRange(), myMapRect.xRange(), viewRect.xRange(), myLoopX)
        val yOrigins = getOrigins(objRect.yRange(), myMapRect.yRange(), viewRect.yRange(), myLoopY)

        val result = ArrayList<WorldPoint>()
        for (xOrigin in xOrigins) {
            for (yOrigin in yOrigins) {
                result.add(explicitVec<World>(xOrigin, yOrigin))
            }
        }
        return result
    }

    override fun getCells(viewRect: WorldRectangle, cellLevel: Int): Set<CellKey> =
        HashSet<CellKey>().apply {
            splitRect(viewRect).forEach {
                this.addAll(calculateCellKeys(myMapRect, it, cellLevel))
            }
        }

    private fun splitRect(rect: WorldRectangle): List<DoubleRectangle> {
        val xRanges = splitRange(rect.xRange(), myMapRect.xRange(), myLoopX)
        val yRanges = splitRange(rect.yRange(), myMapRect.yRange(), myLoopY)

        val rects = ArrayList<DoubleRectangle>()
        xRanges.forEach { xRange ->
            yRanges.forEach { yRange ->
                rects.add(
                    DoubleRectangle(
                        xRange.lowerEndpoint(),
                        yRange.lowerEndpoint(),
                        length(xRange),
                        length(yRange)
                    )
                )
            }
        }
        return rects
    }
}