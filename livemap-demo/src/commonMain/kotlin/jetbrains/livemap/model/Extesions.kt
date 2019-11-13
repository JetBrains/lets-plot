/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.model

import jetbrains.datalore.base.projectionGeometry.explicitVec
import jetbrains.livemap.api.ChartSource
import jetbrains.livemap.api.LineBuilder
import jetbrains.livemap.api.PointBuilder
import jetbrains.livemap.api.TextBuilder

fun PointBuilder.coord(geoObj: GeoObject) {
    point = explicitVec(geoObj.lon, geoObj.lat)
}

fun PointBuilder.coord(lon: Double, lat: Double) {
    point = explicitVec(lon, lat)
}

fun LineBuilder.coord(geoObj: GeoObject) {
    point = explicitVec(geoObj.lon, geoObj.lat)
}

fun LineBuilder.coord(lon: Double, lat: Double) {
    point = explicitVec(lon, lat)
}

fun ChartSource.coord(geoObj: GeoObject) {
    point = explicitVec(geoObj.lon, geoObj.lat)
}

fun ChartSource.coord(lon: Double, lat: Double) {
    point = explicitVec(lon, lat)
}

fun TextBuilder.coord(geoObj: GeoObject) {
    point = explicitVec(geoObj.lon, geoObj.lat)
}

fun TextBuilder.coord(lon: Double, lat: Double) {
    point = explicitVec(lon, lat)
}