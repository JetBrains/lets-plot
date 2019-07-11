package jetbrains.gis.geoprotocol.json

import jetbrains.datalore.base.projectionGeometry.GeoRectangle

internal object ProtocolJsonHelper {
    val MIN_LON = "min_lon"
    val MIN_LAT = "min_lat"
    val MAX_LON = "max_lon"
    val MAX_LAT = "max_lat"

    fun parseGeoRectangle(data: Any): GeoRectangle {
        val obj = data as JsonObject
        return GeoRectangle(
            obj.getDouble(MIN_LON),
            obj.getDouble(MIN_LAT),
            obj.getDouble(MAX_LON),
            obj.getDouble(MAX_LAT)
        )
    }

    fun formatGeoRectangle(rect: GeoRectangle): JsonObject {
        val obj = JsonObject()
        obj[MIN_LON] = rect.minLongitude()
        obj[MIN_LAT] = rect.minLatitude()
        obj[MAX_LAT] = rect.maxLatitude()
        obj[MAX_LON] = rect.maxLongitude()
        return obj
    }
}
