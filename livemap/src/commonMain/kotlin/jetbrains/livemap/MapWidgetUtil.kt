/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.MapRuler
import jetbrains.livemap.projections.ProjectionUtil
import jetbrains.livemap.projections.World
import kotlin.math.*

object MapWidgetUtil {
    const val MIN_ZOOM = 1
    const val MAX_ZOOM = 15
    private const val FULL_ANGLE = 360.0
    private const val STRAIGHT_ANGLE = 180.0

    fun splitPathByAntiMeridian(path: List<Vec<LonLat>>): List<List<Vec<LonLat>>> {
        val pathList = ArrayList<List<Vec<LonLat>>>()
        var currentPath = ArrayList<Vec<LonLat>>()
        if (path.isNotEmpty()) {
            currentPath.add(path[0])

            for (i in 1 until path.size) {
                val prev = path[i - 1]
                val next = path[i]
                val lonDelta = abs(next.x - prev.x)

                if (lonDelta > FULL_ANGLE - lonDelta) {
                    val sign = (if (prev.x < 0.0) -1 else +1).toDouble()

                    val x1 = prev.x - sign * STRAIGHT_ANGLE
                    val x2 = next.x + sign * STRAIGHT_ANGLE
                    val lat = (next.y - prev.y) * (if (x2 == x1) 1.0 / 2.0 else x1 / (x1 - x2)) + prev.y

                    currentPath.add(explicitVec(sign * STRAIGHT_ANGLE, lat))
                    pathList.add(currentPath)
                    currentPath = ArrayList()
                    currentPath.add(explicitVec(-sign * STRAIGHT_ANGLE, lat))
                }

                currentPath.add(next)
            }
        }

        pathList.add(currentPath)
        return pathList
    }

    internal fun calculateMaxZoom(rectSize: Vec<World>, containerSize: DoubleVector): Int {
        val xZoom = calculateMaxZoom(rectSize.x, containerSize.x)
        val yZoom = calculateMaxZoom(rectSize.y, containerSize.y)
        val zoom = min(xZoom, yZoom)
        return max(MIN_ZOOM, min(zoom, MAX_ZOOM))
    }

    private fun calculateMaxZoom(regionLength: Double, containerLength: Double): Int {
        if (regionLength == 0.0) {
            return MAX_ZOOM
        }
        return if (containerLength == 0.0) {
            MIN_ZOOM
        } else (ln(containerLength / regionLength) / ln(2.0)).toInt()
    }

    internal fun <TypeT> calculateExtendedRectangleWithCenter(
        mapRuler: MapRuler<TypeT>,
        rect: Rect<TypeT>,
        center: Vec<TypeT>
    ): Rect<TypeT> {
        val radiusX = calculateRadius(
            center.x,
            rect.left,
            rect.width,
            mapRuler::distanceX)
        val radiusY = calculateRadius(
            center.y,
            rect.top,
            rect.height,
            mapRuler::distanceY)

        return Rect(
            center.x - radiusX,
            center.y - radiusY,
            radiusX * 2,
            radiusY * 2
        )
    }

    private fun calculateRadius(
        center: Double,
        left: Double,
        width: Double,
        distance: (Double, Double) -> Double
    ): Double {
        val right = left + width
        val minEdgeDistance = min(
            distance(center, left),
            distance(center, right)
        )
        return when (center) {
            in left..right -> width - minEdgeDistance
            else -> width + minEdgeDistance
        }
    }

//    internal class LiveMapMappedValues {
//        private val mappingMap = HashMap<String, String>()
//
//        fun append(label: String, value: String) {
//            mappingMap[label] = value
//        }
//
//        fun data(): List<String> {
//            val mappingsList = ArrayList<String>(mappingMap.size * 2)
//
//            mappingMap.forEach {
//                mappingsList.add(it.key)
//                mappingsList.add(it.value)
//            }
//
//            return mappingsList
//        }
//    }

    fun GeoRectangle.convertToWorldRects(mapProjection: MapProjection): List<Rect<World>> {
        return splitByAntiMeridian()
            .map { rect ->
                ProjectionUtil.transformBBox(rect) { mapProjection.project(it) }
            }
    }
}