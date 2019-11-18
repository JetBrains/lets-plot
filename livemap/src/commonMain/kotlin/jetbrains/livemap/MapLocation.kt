/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.projectionGeometry.Rect
import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.gis.geoprotocol.MapRegion
import jetbrains.livemap.projections.World

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