/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.intern.spatial.GeoRectangle
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Rect
import org.jetbrains.letsPlot.gis.geoprotocol.MapRegion
import jetbrains.livemap.World
import jetbrains.livemap.geocoding.MapLocationGeocoder

interface MapLocation {

    fun getBBox(geocoder: MapLocationGeocoder): Async<Rect<World>>

    companion object {
        fun create(geoRectangle: GeoRectangle): MapLocation {
            return object : MapLocation {
                override fun getBBox(geocoder: MapLocationGeocoder) =
                    Asyncs.constant(geocoder.calculateBBoxOfGeoRect(geoRectangle))
            }
        }

        fun create(mapRegion: MapRegion): MapLocation {
            return object : MapLocation {
                override fun getBBox(geocoder: MapLocationGeocoder) =
                    geocoder.geocodeMapRegion(mapRegion)
            }
        }
    }
}
