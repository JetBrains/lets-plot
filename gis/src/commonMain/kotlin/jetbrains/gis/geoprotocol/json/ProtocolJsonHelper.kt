package jetbrains.gis.protocol.json

import jetbrains.datalore.base.json.JsonObject
import jetbrains.datalore.base.json.JsonValue
import jetbrains.datalore.base.projectionGeometry.GeoRectangle

internal object ProtocolJsonHelper {
    val MIN_LON = "min_lon"
    val MIN_LAT = "min_lat"
    val MAX_LON = "max_lon"
    val MAX_LAT = "max_lat"

    fun parseGeoRectangle(data: JsonValue): GeoRectangle {
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
        obj.put(MIN_LON, rect.minLongitude())
        obj.put(MIN_LAT, rect.minLatitude())
        obj.put(MAX_LAT, rect.maxLatitude())
        obj.put(MAX_LON, rect.maxLongitude())
        return obj
    }
}
