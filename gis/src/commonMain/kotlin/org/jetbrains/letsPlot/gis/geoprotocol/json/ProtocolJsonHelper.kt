/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.geoprotocol.json

import org.jetbrains.letsPlot.commons.intern.json.FluentObject
import org.jetbrains.letsPlot.commons.intern.json.Obj
import org.jetbrains.letsPlot.commons.intern.json.getDouble
import org.jetbrains.letsPlot.commons.intern.spatial.GeoRectangle

internal object ProtocolJsonHelper {
    private const val MIN_LON = "min_lon"
    private const val MIN_LAT = "min_lat"
    private const val MAX_LON = "max_lon"
    private const val MAX_LAT = "max_lat"

    fun parseGeoRectangle(obj: Obj): GeoRectangle {
        return GeoRectangle(
            obj.getDouble(MIN_LON),
            obj.getDouble(MIN_LAT),
            obj.getDouble(MAX_LON),
            obj.getDouble(MAX_LAT)
        )
    }

    fun formatGeoRectangle(rect: GeoRectangle): FluentObject =
        FluentObject()
            .put(MIN_LON, rect.startLongitude())
            .put(MIN_LAT, rect.minLatitude())
            .put(MAX_LAT, rect.maxLatitude())
            .put(MAX_LON, rect.endLongitude())
}

