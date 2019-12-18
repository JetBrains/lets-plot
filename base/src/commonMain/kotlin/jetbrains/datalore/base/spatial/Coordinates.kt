/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial

import jetbrains.datalore.base.typedGeometry.Rect
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
