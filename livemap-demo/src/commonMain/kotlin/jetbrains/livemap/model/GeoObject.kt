package jetbrains.livemap.demo.model

import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.Vec

data class GeoObject(
    val lon: Double,
    val lat: Double
) {
    val geoCoord = Vec<LonLat>(lon, lat)
}