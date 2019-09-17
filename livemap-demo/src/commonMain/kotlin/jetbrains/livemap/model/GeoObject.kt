package jetbrains.livemap.demo.model

import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.explicitVec

data class GeoObject(
    val lon: Double,
    val lat: Double
) {
    val geoCoord = explicitVec<LonLat>(lon, lat)
}