package jetbrains.livemap.demo.model

import jetbrains.datalore.base.geometry.DoubleVector

data class GeoObject(
    val lon: Double,
    val lat: Double
) {
    val geoCoord = DoubleVector(lon, lat)
}