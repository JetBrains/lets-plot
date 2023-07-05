/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.gis.geoprotocol.MapRegion
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
