/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import org.jetbrains.letsPlot.commons.intern.spatial.*
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Rect
import jetbrains.datalore.plot.config.GeoConfig
import jetbrains.gis.geoprotocol.MapRegion
import jetbrains.livemap.api.MapLocation

object ConfigUtil {
    private const val REGION_TYPE = "type"
    private const val REGION_DATA = "data"
    private const val REGION_TYPE_NAME = "region_name"
    private const val REGION_TYPE_IDS = "region_ids"
    private const val REGION_TYPE_COORDINATES = "coordinates"
    private const val REGION_TYPE_DATAFRAME = "data_frame"

    private fun <T> List<T>.toDoubleList(): List<Double> {
        if (isEmpty()) {
            return emptyList()
        }

        if (all { it is Double }) {
            @Suppress("UNCHECKED_CAST")
            return this as List<Double>
        }

        error("Can't cast to collection of numbers")
    }

    private fun getWithIdList(data: Any): MapRegion {
        @Suppress("UNCHECKED_CAST")
        val list = data as List<String>
        return MapRegion.withIdList(list)
    }

    private fun calculateGeoRectangle(lonLatList: List<*>): GeoRectangle {
        require(!(lonLatList.isNotEmpty() && lonLatList.size % 2 != 0)) {
            ("Expected: location"
                    + " = [double lon1, double lat1, double lon2, double lat2, ... , double lonN, double latN]")
        }
        return convertToGeoRectangle(calculateBoundingBox(lonLatList.toDoubleList()))
    }

    private fun calculateGeoRectangle(lonLatDataMap: Map<*, *>): GeoRectangle {
        if (lonLatDataMap.containsKey(GeoConfig.POINT_X) && lonLatDataMap.containsKey(GeoConfig.POINT_Y)) {
            return convertToGeoRectangle(
                calculateBoundingBox(
                    (lonLatDataMap[GeoConfig.POINT_X] as List<*>).toDoubleList(),
                    (lonLatDataMap[GeoConfig.POINT_Y] as List<*>).toDoubleList()
                )
            )
        }

        if ((lonLatDataMap.containsKey(GeoConfig.RECT_XMIN) && lonLatDataMap.containsKey(GeoConfig.RECT_YMIN) &&
                    lonLatDataMap.containsKey(GeoConfig.RECT_XMAX) && lonLatDataMap.containsKey(GeoConfig.RECT_YMAX))
        ) {
            return convertToGeoRectangle(
                calculateBoundingBox(
                    (lonLatDataMap[GeoConfig.RECT_XMIN] as List<*>).toDoubleList(),
                    (lonLatDataMap[GeoConfig.RECT_YMIN] as List<*>).toDoubleList(),
                    (lonLatDataMap[GeoConfig.RECT_XMAX] as List<*>).toDoubleList(),
                    (lonLatDataMap[GeoConfig.RECT_YMAX] as List<*>).toDoubleList()
                )
            )
        }

        throw IllegalArgumentException(
            "Expected: location = DataFrame with " +
                    "['${GeoConfig.POINT_X}', '${GeoConfig.POINT_Y}'] or " +
                    "['${GeoConfig.RECT_XMIN}', '${GeoConfig.RECT_YMIN}', '${GeoConfig.RECT_XMAX}', '${GeoConfig.RECT_YMAX}'] " +
                    "columns"
        )
    }

    private fun createMapRegion(region: Any?): MapRegion? {
        return when (region) {
            null -> null
            is Map<*, *> -> {
                val handlerMap = HashMap<String, (Any) -> MapRegion>()
                handlerMap[REGION_TYPE_NAME] = { data -> MapRegion.withName(data as String) }
                handlerMap[REGION_TYPE_IDS] = { getWithIdList(it) }
                handleRegionObject(region, handlerMap)
            }
            else -> throw IllegalArgumentException("Expected: parent" + " = [String]")
        }
    }

    fun createMapLocation(location: Any?): MapLocation? {
        return when (location) {
            null -> null
            is Map<*, *> -> {
                val handlerMap = HashMap<String, (Any) -> MapLocation>()
                handlerMap[REGION_TYPE_NAME] = { data -> MapLocation.create(MapRegion.withName(data as String)) }
                handlerMap[REGION_TYPE_IDS] = { data -> MapLocation.create(getWithIdList(data)) }
                handlerMap[REGION_TYPE_COORDINATES] =
                    { data -> MapLocation.create(calculateGeoRectangle(data as List<*>)) }
                handlerMap[REGION_TYPE_DATAFRAME] =
                    { data -> MapLocation.create(calculateGeoRectangle(data as Map<*, *>)) }
                handleRegionObject(location, handlerMap)
            }
            else -> throw IllegalArgumentException("Expected: location" + " = [String|Array|DataFrame]")
        }
    }

    private fun <T> handleRegionObject(region: Map<*, *>, handlerMap: Map<String, (Any) -> T>): T {
        val regionType = region[REGION_TYPE] ?: throw IllegalArgumentException("Invalid map region object")
        val regionData = region[REGION_DATA] ?: throw IllegalArgumentException("Invalid map region object")

        for ((key, handler) in handlerMap) {
            if (regionType == key) {
                return handler(regionData)
            }
        }

        throw IllegalArgumentException("Invalid map region type: $regionType")
    }

    private fun calculateBoundingBox(xyCoords: List<Double>): Rect<LonLat> {
        return BBOX_CALCULATOR.pointsBBox(xyCoords)
    }

    private fun calculateBoundingBox(xCoords: List<Double>, yCoords: List<Double>): Rect<LonLat> {
        require(xCoords.size == yCoords.size) { "Longitude list count is not equal Latitude list count." }

        return BBOX_CALCULATOR.calculateBoundingBox(
            makeSegments(
                xCoords::get,
                xCoords::get,
                xCoords.size
            ),
            makeSegments(
                yCoords::get,
                yCoords::get,
                xCoords.size
            )
        )
    }

    private fun calculateBoundingBox(
        minXCoords: List<Double>,
        minYCoords: List<Double>,
        maxXCoords: List<Double>,
        maxYCoords: List<Double>,
    ): Rect<LonLat> {
        val count = minXCoords.size
        require(minYCoords.size == count && maxXCoords.size == count && maxYCoords.size == count)
        { "Counts of 'minLongitudes', 'minLatitudes', 'maxLongitudes', 'maxLatitudes' lists are not equal." }

        return BBOX_CALCULATOR.calculateBoundingBox(
            makeSegments(
                minXCoords::get,
                maxXCoords::get,
                count
            ),
            makeSegments(minYCoords::get, maxYCoords::get, count)
        )
    }

}