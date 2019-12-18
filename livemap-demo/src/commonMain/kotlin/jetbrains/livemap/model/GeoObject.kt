/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.model

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.typedGeometry.explicitVec

data class GeoObject(
    val lon: Double,
    val lat: Double
) {
    val geoCoord = explicitVec<LonLat>(lon, lat)
}