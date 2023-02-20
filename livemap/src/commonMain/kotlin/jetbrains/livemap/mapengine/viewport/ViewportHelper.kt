/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.viewport

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.spatial.GeoBoundingBoxCalculator
import jetbrains.datalore.base.spatial.calculateQuadKeys
import jetbrains.datalore.base.spatial.union
import jetbrains.datalore.base.typedGeometry.*
import jetbrains.livemap.World
import jetbrains.livemap.WorldPoint
import jetbrains.livemap.WorldRectangle
import jetbrains.livemap.core.MapRuler
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min


class ViewportHelper(
    private val myMapRect: Rect<World>,
    private val myLoopX: Boolean,
    private val myLoopY: Boolean
) : MapRuler<World> {

    fun <T> normalize(v: Vec<T>): Vec<T> = explicitVec<T>(normalizeX(v.x), normalizeY(v.y))

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


    private fun length(mapRange: DoubleSpan): Double {
        return mapRange.upperEnd - mapRange.lowerEnd
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

    override fun calculateBoundingBox(xyRects: List<Rect<World>>): Rect<World> {
        return GeoBoundingBoxCalculator(myMapRect, myLoopX, myLoopY).union(xyRects)
    }

    private fun normalizeX(x: Double): Double {
        return normalize(x, myMapRect.left, myMapRect.right, myLoopX)
    }

    private fun normalizeY(y: Double): Double {
        return normalize(y, myMapRect.top, myMapRect.bottom, myLoopY)
    }

    internal fun getOrigins(objRect: WorldRectangle, viewRect: WorldRectangle): List<WorldPoint> {
        fun getOrigins(
            objRange: DoubleSpan,
            mapRange: DoubleSpan,
            viewRange: DoubleSpan,
            loop: Boolean
        ): List<Double> {
            if (!loop) {
                return if (objRange.connected(viewRange)) listOf(objRange.lowerEnd) else emptyList()
            }

            val mapRangeLen = length(mapRange)

            val n = floor((viewRange.lowerEnd - mapRange.lowerEnd) / mapRangeLen).toInt()
            var origin = mapRange.lowerEnd + n * mapRangeLen + objRange.lowerEnd

            if (origin + length(objRange) < viewRange.lowerEnd) {
                origin += mapRangeLen
            }

            val result = ArrayList<Double>()
            while (origin < viewRange.upperEnd) {
                result.add(origin)
                origin += mapRangeLen
            }
            return result
        }

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

    fun getCells(viewRect: WorldRectangle, cellLevel: Int): Set<CellKey> =
        splitRect(viewRect)
            .map { calculateQuadKeys(myMapRect, it, cellLevel, ::CellKey) }
            .flatten()
            .toSet()

    private fun splitRect(rect: WorldRectangle): List<Rect<World>> {
        fun splitRange(
            range: DoubleSpan,
            mapRange: DoubleSpan,
            loop: Boolean
        ): List<DoubleSpan> {
            val xRanges = ArrayList<DoubleSpan>()
            var lower = range.lowerEnd
            var upper = range.upperEnd

            if (lower < mapRange.lowerEnd) {
                if (loop && upper < mapRange.upperEnd) {
                    val newLeft = max(lower + length(mapRange), upper)
                    xRanges.add(DoubleSpan(newLeft, mapRange.upperEnd))
                }
                lower = mapRange.lowerEnd
            }

            if (mapRange.upperEnd < upper) {
                if (loop && mapRange.lowerEnd < lower) {
                    val newRight = min(upper - length(mapRange), lower)
                    xRanges.add(DoubleSpan(mapRange.lowerEnd, newRight))
                }
                upper = mapRange.upperEnd
            }

            xRanges.add(DoubleSpan(lower, upper))
            return xRanges
        }

        val xRanges = splitRange(rect.xRange(), myMapRect.xRange(), myLoopX)
        val yRanges = splitRange(rect.yRange(), myMapRect.yRange(), myLoopY)

        val rects = ArrayList<Rect<World>>()
        xRanges.forEach { xRange ->
            yRanges.forEach { yRange ->
                rects.add(
                    Rect.XYWH(
                        xRange.lowerEnd,
                        yRange.lowerEnd,
                        length(xRange),
                        length(yRange)
                    )
                )
            }
        }
        return rects
    }

    private fun deltaOnLoop(x1: Double, x2: Double, length: Double): Double {
        val dist = abs(x2 - x1)

        if (dist <= length - dist) {
            return x2 - x1
        }

        var closestX2 = x2
        if (x2 < x1) {
            closestX2 += length
        } else {
            closestX2 -= length
        }
        return closestX2 - x1
    }


}