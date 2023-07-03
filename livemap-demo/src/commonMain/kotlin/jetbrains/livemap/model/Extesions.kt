/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.model

import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.livemap.api.*

fun PointEntityBuilder.coord(geoObj: GeoObject) {
    point = explicitVec(geoObj.centroid.x, geoObj.centroid.y)
}

fun PointEntityBuilder.coord(lon: Double, lat: Double) {
    point = explicitVec(lon, lat)
}

fun LineEntityBuilder.coord(geoObj: GeoObject) {
    point = explicitVec(geoObj.centroid.x, geoObj.centroid.y)
}

fun LineEntityBuilder.coord(lon: Double, lat: Double) {
    point = explicitVec(lon, lat)
}

fun TextEntityBuilder.coord(geoObj: GeoObject) {
    point = explicitVec(geoObj.centroid.x, geoObj.centroid.y)
}

fun TextEntityBuilder.coord(lon: Double, lat: Double) {
    point = explicitVec(lon, lat)
}

fun PieEntityBuilder.coord(geoObj: GeoObject) {
    point = explicitVec(geoObj.centroid.x, geoObj.centroid.y)
}

fun PieEntityBuilder.coord(lon: Double, lat: Double) {
    point = explicitVec(lon, lat)
}