/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.spatial

import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import kotlin.math.max
import kotlin.math.min

interface LonLat {
    companion object {
        val DOMAIN = Rect.LTRB<LonLat>(-180.0, -90.0, 180.0, 90.0)
    }
}
typealias LonLatPoint = Vec<LonLat>

const val EARTH_RADIUS = 6378137.0
const val MIN_LONGITUDE = -180.0
const val MAX_LONGITUDE = 180.0
const val FULL_LONGITUDE = MAX_LONGITUDE - MIN_LONGITUDE
const val MIN_LATITUDE = -90.0
const val MAX_LATITUDE = 90.0
const val FULL_LATITUDE = MAX_LATITUDE - MIN_LATITUDE

val EARTH_RECT = Rect.XYWH<LonLat>(MIN_LONGITUDE, MIN_LATITUDE, FULL_LONGITUDE, FULL_LATITUDE)
val BBOX_CALCULATOR =
    org.jetbrains.letsPlot.commons.intern.spatial.GeoBoundingBoxCalculator(EARTH_RECT, myLoopX = true, myLoopY = false)

fun limitLon(lon: Double) = max(MIN_LONGITUDE, min(lon, MAX_LONGITUDE))
fun limitLat(lat: Double) = max(MIN_LATITUDE, min(lat, MAX_LATITUDE))

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

    return GeoRectangle(
        left,
        limitLat(rect.top),
        right,
        limitLat(rect.bottom)
    )
}

fun calculateQuadKeys(rect: Rect<LonLat>, zoom: Int): Set<QuadKey<LonLat>> {
    val flippedRect = Rect.XYWH<LonLat>(
        rect.left,
        -rect.bottom,
        rect.width,
        rect.height
    )
    return calculateQuadKeys(
        EARTH_RECT,
        flippedRect,
        zoom,
        ::QuadKey
    )
}

