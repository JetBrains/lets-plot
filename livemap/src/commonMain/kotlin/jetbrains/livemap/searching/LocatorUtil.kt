/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.searching

import jetbrains.datalore.base.typedGeometry.*
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

object LocatorUtil {

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

    fun <TypeT> coordInExtendedRect(
        coord: Vec<TypeT>,
        rect: Rect<TypeT>,
        delta: Double
    ): Boolean {
        return rect.contains(coord)
                || abs(coord.x - rect.left) <= delta
                || abs(coord.x - rect.right) <= delta
                || abs(coord.y - rect.bottom) <= delta
                || abs(coord.y - rect.top) <= delta
    }

    fun <TypeT> pathContainsCoordinate(
        coord: Vec<TypeT>,
        path: List<Vec<TypeT>>,
        strokeWidth: Double
    ): Boolean {
        for (i in 0 until path.size - 1) {
            if (calculateSquareDistanceToPathSegment(coord, path, i) <= strokeWidth.pow(2.0)) {
                return true
            }
        }
        return false
    }

    private fun <TypeT> calculateSquareDistanceToPathSegment(
        coord: Vec<TypeT>,
        path: List<Vec<TypeT>>,
        segmentNum: Int
    ): Double {
        val next = segmentNum + 1
        val dx: Double = path[next].x - path[segmentNum].x
        val dy: Double = path[next].y - path[segmentNum].y
        val scalar: Double = dx * (coord.x - path[segmentNum].x) + dy * (coord.y - path[segmentNum].y)
        if (scalar <= 0) {
            return calculateSquareDistanceToPathPoint(coord, path, segmentNum)
        }
        val segmentSquareLength = dx * dx + dy * dy
        val baseSquareLength = scalar * scalar / segmentSquareLength
        return if (baseSquareLength >= segmentSquareLength) {
            calculateSquareDistanceToPathPoint(coord, path, next)
        } else calculateSquareDistanceToPathPoint(coord, path, segmentNum) - baseSquareLength
    }

    private fun <TypeT> calculateSquareDistanceToPathPoint(
        coord: Vec<TypeT>,
        path: List<Vec<TypeT>>,
        pointNum: Int
    ): Double {
        val dx: Double = coord.x - path[pointNum].x
        val dy: Double = coord.y - path[pointNum].y
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