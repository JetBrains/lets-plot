/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.model

import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.livemap.api.*

fun PointBuilder.coord(geoObj: GeoObject) {
    point = explicitVec(geoObj.centroid.x, geoObj.centroid.y)
}

fun PointBuilder.coord(lon: Double, lat: Double) {
    point = explicitVec(lon, lat)
}

fun LineBuilder.coord(geoObj: GeoObject) {
    point = explicitVec(geoObj.centroid.x, geoObj.centroid.y)
}

fun LineBuilder.coord(lon: Double, lat: Double) {
    point = explicitVec(lon, lat)
}

fun TextBuilder.coord(geoObj: GeoObject) {
    point = explicitVec(geoObj.centroid.x, geoObj.centroid.y)
}

fun TextBuilder.coord(lon: Double, lat: Double) {
    point = explicitVec(lon, lat)
}

fun PieBuilder.coord(geoObj: GeoObject) {
    point = explicitVec(geoObj.centroid.x, geoObj.centroid.y)
}

fun PieBuilder.coord(lon: Double, lat: Double) {
    point = explicitVec(lon, lat)
}