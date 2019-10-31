/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.model

import jetbrains.livemap.api.PointBuilder

fun PointBuilder.coord(geoObj: GeoObject) {
    lon = geoObj.lon
    lat = geoObj.lat
}