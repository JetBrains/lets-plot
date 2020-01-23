/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.livemap.core.projections.MapRuler
import jetbrains.livemap.projection.World
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

object LocatorUtil {
    fun rescaleLengthByZoom(srcLength: Double, srcZoom: Int, dstZoom: Int): Double {
        return if (dstZoom >= srcZoom) {
            srcLength * (1 shl dstZoom - srcZoom)
        } else {
            srcLength / (1 shl srcZoom - dstZoom)
        }
    }

    fun <TypeT> calculateAngle(coord1: Vec<TypeT>, coord2: Vec<TypeT>): Double {
        val dx: Double = (coord2.x - coord1.x)
        val dy: Double = (coord2.y - coord1.y)
        return atan2(dy, dx)
    }

    fun <TypeT> distance(coord1: Vec<TypeT>, coord2: Vec<TypeT>): Double {
        return sqrt(
            (coord1.x - coord2.x).pow(2.0) + (coord1.y - coord2.y).pow(2.0)
        )
    }

    fun distance(coord1: Vec<World>, coord2: Vec<World>, mapRuler: MapRuler<World>): Double {
        return sqrt(
            mapRuler.deltaX(coord1.x, coord2.x).pow(2.0) +
                    mapRuler.deltaY(coord1.y, coord2.y).pow(2.0)
        )
    }

    fun coordInExtendedRect(
        coord: DoubleVector,
        rect: DoubleRectangle,
        delta: Double,
        mapRuler: MapRuler<World>
    ): Boolean {
        return rect.contains(coord) || mapRuler.distanceX(
            coord.x,
            rect.left
        ) <= delta || mapRuler.distanceX(coord.x, rect.right) <= delta || mapRuler.distanceY(
            coord.y,
            rect.bottom
        ) <= delta || mapRuler.distanceY(coord.y, rect.top) <= delta
    }

    fun pathContainsCoordinate(
        coord: DoubleVector,
        path: List<DoubleVector>,
        strokeWidth: Double,
        mapRuler: MapRuler<World>
    ): Boolean {
        for (i in 0 until path.size - 1) {
            if (calculateSquareDistanceToPathSegment(coord, path, i, mapRuler) <= strokeWidth.pow(2.0)) {
                return true
            }
        }
        return false
    }

    fun calculateSquareDistanceToPathSegment(
        coord: DoubleVector,
        path: List<DoubleVector>,
        segmentNum: Int,
        mapRuler: MapRuler<World>
    ): Double {
        val next = segmentNum + 1
        val dx: Double = mapRuler.deltaX(path[segmentNum].x, path[next].x)
        val dy: Double = mapRuler.deltaY(path[segmentNum].y, path[next].y)
        val scalar: Double =
            dx * mapRuler.deltaX(path[segmentNum].x, coord.x) + dy * mapRuler.deltaY(path[segmentNum].y, coord.y)
        if (scalar <= 0) {
            return calculateSquareDistanceToPathPoint(coord, path, segmentNum, mapRuler)
        }
        val segmentSquareLength = dx * dx + dy * dy
        val baseSquareLength = scalar * scalar / segmentSquareLength
        return if (baseSquareLength >= segmentSquareLength) {
            calculateSquareDistanceToPathPoint(coord, path, next, mapRuler)
        } else calculateSquareDistanceToPathPoint(coord, path, segmentNum, mapRuler) - baseSquareLength
    }

    private fun calculateSquareDistanceToPathPoint(
        coord: DoubleVector,
        path: List<DoubleVector>,
        pointNum: Int,
        mapRuler: MapRuler<World>
    ): Double {
        val dx: Double = mapRuler.deltaX(coord.x, path[pointNum].x)
        val dy: Double = mapRuler.deltaY(coord.y, path[pointNum].y)
        return dx * dx + dy * dy
    }

    fun <TypeT> ringContainsCoordinate(ring: List<Vec<TypeT>>, coord: Vec<TypeT>): Boolean {
        var intersectionCount = 0
        for (i in 1 until ring.size) {
            val start = i - 1
            if (ring[start].y >= coord.y && ring[i].y >= coord.y ||
                ring[start].y < coord.y && ring[i].y < coord.y
            ) {
                continue
            }
            val x: Double = ring[start].x + (coord.y - ring[start].y) *
                    (ring[i].x - ring[start].x) / (ring[i].y - ring[start].y)
            if (x <= coord.x) {
                intersectionCount++
            }
        }
        return intersectionCount % 2 != 0
    }
}