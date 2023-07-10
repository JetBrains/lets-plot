/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.base.math.toDegrees
import jetbrains.datalore.base.math.toRadians
import kotlin.math.*

object MercatorUtils {
    private const val MAX_LONGITUDE = 180.0
    private const val MAX_LATITUDE = 85.0511287798
    val VALID_LONGITUDE_RANGE = DoubleSpan(-MAX_LONGITUDE, MAX_LONGITUDE)
    val VALID_LATITUDE_RANGE = DoubleSpan(-MAX_LATITUDE, MAX_LATITUDE)

    fun getMercatorX(lon: Double): Double = toRadians(lon) * EARTH_RADIUS

    fun getMercatorY(lat: Double): Double {
        @Suppress("NAME_SHADOWING")
        val lat = normalizeLat(lat)
        return ln(tan(PI / 4 + toRadians(lat) / 2)) * EARTH_RADIUS
    }

    fun getLongitude(x: Double): Double {
        return toDegrees(x / EARTH_RADIUS)
    }

    fun getLatitude(y: Double): Double {
        return normalizeLat(toDegrees((atan(exp(y / EARTH_RADIUS)) - PI / 4) * 2))
    }

    fun checkLon(lon: Double): Boolean {
        return VALID_LONGITUDE_RANGE.contains(lon)
    }

    fun checkLat(lat: Double): Boolean {
        return VALID_LATITUDE_RANGE.contains(lat)
    }

    private fun normalizeLat(lat: Double): Double {
        return max(-MAX_LATITUDE, min(lat, MAX_LATITUDE))
    }
}