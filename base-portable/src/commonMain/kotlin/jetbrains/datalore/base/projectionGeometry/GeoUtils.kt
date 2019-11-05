/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.projectionGeometry

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import kotlin.math.*


object GeoUtils {

    internal val MIN_LONGITUDE = -180.0
    internal val MAX_LONGITUDE = 180.0
    val FULL_LONGITUDE = MAX_LONGITUDE - MIN_LONGITUDE
    private val MIN_LATITUDE = -90.0
    private val MAX_LATITUDE = 90.0
    val FULL_LATITUDE = MAX_LATITUDE - MIN_LATITUDE
    val EARTH_RECT = Rect<LonLat>(MIN_LONGITUDE, MIN_LATITUDE, FULL_LONGITUDE, FULL_LATITUDE)
    val BBOX_CALCULATOR = GeoBoundingBoxCalculator(EARTH_RECT, true, false)
    private val QUAD_KEY_CREATOR = { tileKey: String -> QuadKey(tileKey) }

    fun toRadians(degrees: Double): Double = degrees * PI / 180.0

    fun toDegrees(radians: Double): Double = radians * 180.0 / PI

    fun limitLon(lon: Double): Double {
        return max(MIN_LONGITUDE, min(lon, MAX_LONGITUDE))
    }

    fun limitLat(lat: Double): Double {
        return max(MIN_LATITUDE, min(lat, MAX_LATITUDE))
    }

    fun normalizeLon(lon: Double): Double {
        var result = lon - (lon / FULL_LONGITUDE).toInt() * FULL_LONGITUDE

        if (result > MAX_LONGITUDE) {
            result -= FULL_LONGITUDE
        }
        if (result < -MAX_LONGITUDE) {
            result += FULL_LONGITUDE
        }

        return result
    }

    fun calculateEarthAngle(coord1: DoubleVector, coord2: DoubleVector): Double {
        val dLon = deltaLon(coord1.x, coord2.x)
        val dLon1 = dLon * cos(toRadians(coord1.y))
        val dLon2 = dLon * cos(toRadians(coord2.y))
        val dLat = coord2.y - coord1.y
        return atan2(dLat, (dLon1 + dLon2) / 2)
    }

    private fun deltaLon(lon1: Double, lon2: Double): Double {
        return deltaOnLoop(lon1, lon2, FULL_LONGITUDE)
    }

    fun deltaOnLoop(x1: Double, x2: Double, length: Double): Double {
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

    fun convertToGeoRectangle(rect: Rect<LonLat>): GeoRectangle {
        val left: Double
        val right: Double

        if (rect.width < EARTH_RECT.width) {
            left = normalizeLon(rect.left)
            right = normalizeLon(rect.right)
        } else {
            left = EARTH_RECT.left
            right = EARTH_RECT.right
        }

        return GeoRectangle(left, limitLat(rect.top), right, limitLat(rect.bottom))
    }

    fun calculateQuadKeys(lonLatRect: Rect<LonLat>, zoom: Int?): Set<QuadKey> {
        val flipRect = Rect<LonLat>(
                lonLatRect.left,
                -lonLatRect.bottom,
                lonLatRect.width,
                lonLatRect.height
        )
        return calculateTileKeys(EARTH_RECT, flipRect, zoom, QUAD_KEY_CREATOR)
    }

    fun getQuadKeyRect(quadKey: QuadKey): Rect<LonLat> {
        val origin = getTileOrigin(EARTH_RECT, quadKey.string)
        val dimension = EARTH_RECT.dimension * (1.0 / getTileCount(quadKey.string.length))

        val flipY = EARTH_RECT.scalarBottom - (origin.scalarY + dimension.scalarY - EARTH_RECT.scalarTop)
        return Rect(origin.transform(fy = { flipY }), dimension)
    }

    fun <TypeT> getTileOrigin(mapRect: Rect<TypeT>, tileKey: String): Vec<TypeT> {
        var left = mapRect.scalarLeft
        var top = mapRect.scalarTop
        var width = mapRect.scalarWidth
        var height = mapRect.scalarHeight

        for (quadrant in tileKey) {
            width /= 2.0
            height /= 2.0

            if (quadrant == '1' || quadrant == '3') {
                left += width
            }
            if (quadrant == '2' || quadrant == '3') {
                top += height
            }
        }
        return newVec(left, top)
    }

    fun <T> calculateTileKeys(mapRect: Rect<LonLat>, viewRect: Rect<LonLat>, zoom: Int?, constructor: (String) -> T): Set<T> {
        val tileKeys = HashSet<T>()
        val tileCount = getTileCount(zoom!!)

        val xmin = calcTileNum(viewRect.left, mapRect.xRange(), tileCount)
        val xmax = calcTileNum(viewRect.right, mapRect.xRange(), tileCount)
        val ymin = calcTileNum(viewRect.top, mapRect.yRange(), tileCount)
        val ymax = calcTileNum(viewRect.bottom, mapRect.yRange(), tileCount)

        for (x in xmin..xmax) {
            for (y in ymin..ymax) {
                tileKeys.add(constructor(tileXYToTileID(x, y, zoom)))
            }
        }

        return tileKeys
    }

    fun calcTileNum(value: Double, range: ClosedRange<Double>, tileCount: Int): Int {
        val position = (value - range.lowerEndpoint()) / (range.upperEndpoint() - range.lowerEndpoint())
        return max(0.0, min(position * tileCount, (tileCount - 1).toDouble())).toInt()
    }

    fun tileXYToTileID(tileX: Int, tileY: Int, zoom: Int): String {
        var tileID = ""

        for (i in zoom downTo 1) {
            var digit = '0'
            val mask = 1 shl i - 1

            if (tileX and mask != 0) {
                ++digit
            }

            if (tileY and mask != 0) {
                digit += 2
            }

            tileID += digit
        }

        return tileID
    }

    fun getTileCount(zoom: Int): Int {
        return 1 shl zoom
    }

}
