/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.projectionGeometry.explicitVec
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.MercatorUtils.checkLat
import jetbrains.datalore.base.spatial.MercatorUtils.checkLon

internal object LonLatParser {
    private val LON_LAT = Regex("^(-?\\d+\\.?\\d*), ?(-?\\d+\\.?\\d*)$")
    private const val LON = 1
    private const val LAT = 2

    fun parse(lonlat: String): Vec<LonLat>? {
        val matcher = LON_LAT.matchEntire(lonlat)?.groups ?: return null
        val lon = matcher[LON]?.value?.toDouble() ?: return null
        val lat = matcher[LAT]?.value?.toDouble() ?: return null

        return if (checkLon(lon) && checkLat(lat)) {
            explicitVec(lon, lat)
        } else {
            null
        }
    }
}