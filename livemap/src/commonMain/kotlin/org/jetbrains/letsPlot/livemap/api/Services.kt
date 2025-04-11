/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.api

import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.gis.geoprotocol.GeoRequest
import org.jetbrains.letsPlot.gis.geoprotocol.GeoResponse
import org.jetbrains.letsPlot.gis.geoprotocol.GeoTransport
import org.jetbrains.letsPlot.gis.geoprotocol.GeocodingService

object Services {

    fun bogusGeocodingService(): GeocodingService = GeocodingService(
        object : GeoTransport {
            override fun send(request: GeoRequest): Async<GeoResponse> {
                return Asyncs.failure(RuntimeException("Geocoding is disabled."))
            }
        }
    )

    fun devGeocodingService() = liveMapGeocoding {
        url = "http://10.0.0.127:3020/map_data/geocoding"
    }

    fun jetbrainsGeocodingService() = liveMapGeocoding {
        url = "https://geo2.datalore.jetbrains.com"
    }

    fun devTileProvider() = liveMapVectorTiles {
        url = "ws://10.0.0.127:3933"
    }

    fun jetbrainsTileProvider() = liveMapVectorTiles {
        url = "wss://tiles.datalore.jetbrains.com"
    }
}
