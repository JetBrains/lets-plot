/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial

import jetbrains.datalore.base.typedGeometry.*
import kotlin.math.max
import kotlin.math.min

class LonLat

val EARTH_RECT = Rect<LonLat>(
    MIN_LONGITUDE,
    MIN_LATITUDE,
    FULL_LONGITUDE,
    FULL_LATITUDE
)

fun limitLon(lon: Double) = max(
    MIN_LONGITUDE, min(lon,
        MAX_LONGITUDE
    ))

fun limitLat(lat: Double) = max(
    MIN_LATITUDE, min(lat,
        MAX_LATITUDE
    ))

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

val BBOX_CALCULATOR = GeoBoundingBoxCalculator(
    EARTH_RECT,
    myLoopX = true,
    myLoopY = false
)

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
    val flippedRect = Rect<LonLat>(
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

