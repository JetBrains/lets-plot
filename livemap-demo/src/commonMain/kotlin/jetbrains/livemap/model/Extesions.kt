package jetbrains.livemap.model

import jetbrains.livemap.api.PointBuilder
import jetbrains.livemap.demo.model.GeoObject

fun PointBuilder.coord(geoObj: GeoObject) {
    lon = geoObj.lon
    lat = geoObj.lat
}