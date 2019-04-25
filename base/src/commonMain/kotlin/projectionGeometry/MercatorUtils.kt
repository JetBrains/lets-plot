package jetbrains.datalore.base.projectionGeometry

import jetbrains.datalore.base.gcommon.collect.ClosedRange

import jetbrains.datalore.base.projectionGeometry.GeoUtils.EARTH_RADIUS
import jetbrains.datalore.base.projectionGeometry.GeoUtils.toDegrees
import jetbrains.datalore.base.projectionGeometry.GeoUtils.toRadians
import kotlin.math.*

object MercatorUtils {
    val MAX_LONGITUDE = 180.0
    val MAX_LATITUDE = 85.0511287798
    val VALID_LONGITUDE_RANGE = ClosedRange.closed(-MAX_LONGITUDE, MAX_LONGITUDE)
    val VALID_LATITUDE_RANGE = ClosedRange.closed(-MAX_LATITUDE, MAX_LATITUDE)

    fun getMercatorX(lon: Double?): Double? {
        return (if (lon != null) toRadians(lon) * EARTH_RADIUS else null)?.toDouble()
    }

    fun getMercatorY(lat: Double?): Double? {
        return if (lat != null) {
            ln(tan(PI / 4 + toRadians(normalizeLat(lat)) / 2)) * EARTH_RADIUS
        } else null
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