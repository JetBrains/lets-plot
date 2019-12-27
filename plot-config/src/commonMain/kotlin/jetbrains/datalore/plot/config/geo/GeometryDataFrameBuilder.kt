/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.geo

import jetbrains.datalore.base.spatial.*
import jetbrains.datalore.base.typedGeometry.*
import jetbrains.datalore.plot.builder.map.GeoPositionField.POINT_X
import jetbrains.datalore.plot.builder.map.GeoPositionField.POINT_Y
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_XMAX
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_XMIN
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_YMAX
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_YMIN
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.GeoDataKind
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_JOIN_KEY_COLUMN


internal class GeometryDataFrameBuilder(private val myGeoDataKind: GeoDataKind) {
    private val myIds = ArrayList<String>()
    private val myLons = ArrayList<Double>()
    private val myLats = ArrayList<Double>()
    private val myLonMin = ArrayList<Double>()
    private val myLonMax = ArrayList<Double>()
    private val myLatMin = ArrayList<Double>()
    private val myLatMax = ArrayList<Double>()

    val data: Map<String, Any>
        get() {
            val data = geometry
            data[MAP_JOIN_KEY_COLUMN] = myIds
            return data
        }

    val geometry: MutableMap<String, Any>
        get() {
            return when {
                myGeoDataKind === GeoDataKind.LIMIT ->
                    mutableMapOf(
                        RECT_XMIN to myLonMin,
                        RECT_XMAX to myLonMax,
                        RECT_YMIN to myLatMin,
                        RECT_YMAX to myLatMax
                    )
                else -> {
                    mutableMapOf(
                        POINT_X to myLons,
                        POINT_Y to myLats
                    )
                }
            }
        }

    fun addPoint(id: String, point: Vec<LonLat>) {
        if (myGeoDataKind === GeoDataKind.CENTROID) {
            insertPoint(id, point)
        }
    }

    fun addBoundary(id: String, multiPoint: MultiPoint<LonLat>) {
        if (myGeoDataKind === GeoDataKind.LIMIT) {
            val bbox = BBOX_CALCULATOR.rectsBBox(listOf(multiPoint.boundingBox()))
            insertGeoRectangle(id, convertToGeoRectangle(bbox))
        } else {
            insertPoints(id, multiPoint, myGeoDataKind === GeoDataKind.BOUNDARY)
        }
    }

    fun addBoundary(id: String, lineString: LineString<LonLat>) {
        if (myGeoDataKind === GeoDataKind.LIMIT) {
            val bbox = BBOX_CALCULATOR.rectsBBox(listOf(lineString.boundingBox()))
            insertGeoRectangle(id, convertToGeoRectangle(bbox))
        } else {
            insertPoints(id, lineString, myGeoDataKind === GeoDataKind.BOUNDARY)
        }
    }

    fun addBoundary(id: String, multiLineString: MultiLineString<LonLat>) {
        if (myGeoDataKind === GeoDataKind.LIMIT) {
            val bbox = BBOX_CALCULATOR.rectsBBox(listOf(multiLineString.flatten().boundingBox()))
            insertGeoRectangle(id, convertToGeoRectangle(bbox))
        } else {
            insertPoints(id, multiLineString.flatten(), myGeoDataKind === GeoDataKind.BOUNDARY)
        }
    }

    fun addBoundary(id: String, polygon: Polygon<LonLat>) {
        if (myGeoDataKind === GeoDataKind.LIMIT) {
            val bbox = BBOX_CALCULATOR.rectsBBox(listOf(polygon.limit()))
            insertGeoRectangle(id, convertToGeoRectangle(bbox))
        } else {
            insertPoints(id, polygon.flatten(), myGeoDataKind === GeoDataKind.BOUNDARY)
        }
    }

    fun addBoundary(id: String, boundary: MultiPolygon<LonLat>) {
        if (myGeoDataKind === GeoDataKind.LIMIT) {
            val bbox = BBOX_CALCULATOR.rectsBBox(boundary.limit())
            insertGeoRectangle(id, convertToGeoRectangle(bbox))
        } else {
            boundary.flatten().flatten().let { insertPoints(id, it, myGeoDataKind === GeoDataKind.BOUNDARY) }
        }
    }

    fun addGeoRectangle(id: String, geoRectangle: GeoRectangle) {
        if (myGeoDataKind === GeoDataKind.LIMIT) {
            insertGeoRectangle(id, geoRectangle)
        }
    }

    private fun insertPoints(id: String, points: List<Vec<LonLat>>, closePath: Boolean) {
        points.forEach { insertPoint(id, it) }

        if (closePath && points.isNotEmpty() && points.first() != points.last()) {
            insertPoint(id, points.first())
        }
    }

    private fun insertPoint(id: String, point: Vec<LonLat>) {
        myIds.add(id)
        myLons.add(point.x)
        myLats.add(point.y)
    }

    private fun insertGeoRectangle(id: String, geoRectangle: GeoRectangle) {
        geoRectangle.splitByAntiMeridian().forEach { rect ->
            myIds.add(id)
            myLonMin.add(rect.left)
            myLonMax.add(rect.right)
            myLatMin.add(rect.top)
            myLatMax.add(rect.bottom)
        }
    }
}
