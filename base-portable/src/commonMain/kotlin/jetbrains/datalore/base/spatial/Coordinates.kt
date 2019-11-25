/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial

import jetbrains.datalore.base.projectionGeometry.Rect
import kotlin.math.max
import kotlin.math.min

class LonLat

internal const val MIN_LONGITUDE = -180.0
internal const val MAX_LONGITUDE = 180.0
const val FULL_LONGITUDE = MAX_LONGITUDE - MIN_LONGITUDE
private const val MIN_LATITUDE = -90.0
private const val MAX_LATITUDE = 90.0
private const val FULL_LATITUDE = MAX_LATITUDE - MIN_LATITUDE
val EARTH_RECT = Rect<LonLat>(MIN_LONGITUDE, MIN_LATITUDE, FULL_LONGITUDE, FULL_LATITUDE)

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
