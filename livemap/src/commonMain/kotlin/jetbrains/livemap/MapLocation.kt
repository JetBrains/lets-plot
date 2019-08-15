package jetbrains.livemap

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.projectionGeometry.GeoRectangle
import jetbrains.gis.geoprotocol.MapRegion

interface MapLocation {

    fun getBBox(geocoder: MapLocationGeocoder): Async<DoubleRectangle>

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