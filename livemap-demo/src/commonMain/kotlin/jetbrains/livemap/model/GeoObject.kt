package jetbrains.livemap.demo.model

import jetbrains.datalore.base.projectionGeometry.LonLatPoint

data class GeoObject(
    val lon: Double,
    val lat: Double
) {
    val geoCoord = LonLatPoint(lon, lat)
}