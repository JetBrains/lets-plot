/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.async.PlatformAsyncs
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.Rect
import jetbrains.datalore.base.projectionGeometry.center
import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.gis.geoprotocol.FeatureLevel
import jetbrains.gis.geoprotocol.GeocodingService
import jetbrains.gis.geoprotocol.MapRegion
import jetbrains.livemap.MapWidgetUtil.convertToWorldRects
import jetbrains.livemap.mapobjects.MapLayer
import jetbrains.livemap.mapobjects.MapLayerKind
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.MapRuler
import jetbrains.livemap.projections.World

internal class MapDataGeocodingHelper(
    private val mySize: DoubleVector,
    private val myGeocodingService: GeocodingService,
    private val myMapLayers: List<MapLayer>,
    private val myLevel: FeatureLevel?,
    private val myParent: MapRegion?,
    private val myMapLocation: MapLocation?,
    private val myZoom: Int?,
    private val myMapRuler: MapRuler<World>,
    private val myMapProjection: MapProjection,
    private val myNeedBBoxes: Boolean
) {
    val regionBBoxes: MutableMap<String, GeoRectangle> = HashMap()

    fun geocodeMapData(): Async<MapPosition?> {
        return try {
            geocodeLayersData().flatMap(::calculateMapPosition)
        } catch (e: RuntimeException) {
            Asyncs.failure(e)
        }
    }

    private fun geocodeLayersData(): Async<List<Rect<World>>> {
        val needLocations = myMapLocation == null

        val asyncs = ArrayList<Async<Unit>>()
        val locationBBoxes = ArrayList<Rect<World>>()

        for (mapLayer in myMapLayers) {
            if (mapLayer.mapObjects.isEmpty()) continue

            MapLayerGeocodingHelper(
                mapLayer,
                myGeocodingService,
                myMapProjection,
                needLocations,
                myNeedBBoxes,
                myLevel,
                myParent
            ).run {
                geocodeLayerData().map { bBoxes ->
                    locationBBoxes.addAll(bBoxes)
                    if (myNeedBBoxes && mapLayer.kind == MapLayerKind.POLYGON) {
                        regionBBoxes.putAll(osmIdBBoxMap)
                    }
                    return@map
                }
            }.run {
                asyncs.add(this)
            }
        }

        return PlatformAsyncs.parallel(asyncs).map { locationBBoxes }
    }

    private fun calculateMapPosition(bBoxes: List<Rect<World>>): Async<MapPosition> {
        return calculateLocation(bBoxes).map(::createMapPosition)
    }

    private fun calculateLocation(bBoxes: List<Rect<World>>): Async<Rect<World>> {
        if (myMapLocation != null) {
            return myMapLocation.getBBox(
                MapLocationGeocoder(
                    myGeocodingService,
                    myMapRuler,
                    myMapProjection
                )
            )
        }

        return if (bBoxes.isNotEmpty()) {
            Asyncs.constant(myMapRuler.calculateBoundingBox(bBoxes))
        } else Asyncs.constant(myMapRuler.calculateBoundingBox(DEFAULT_LOCATION.convertToWorldRects(myMapProjection)))

    }

    private fun createMapPosition(rectangle: Rect<World>): MapPosition {
        val zoom: Int = myZoom
            ?: if (rectangle.dimension.x != 0.0 && rectangle.dimension.y != 0.0) {
                calculateZoom(rectangle)
            } else {
                calculateZoom(myMapRuler.calculateBoundingBox(DEFAULT_LOCATION.convertToWorldRects(myMapProjection)))
            }

        return MapPosition(zoom, rectangle.center)
    }

    private fun calculateZoom(rectangle: Rect<World>): Int {
        return MapWidgetUtil.calculateMaxZoom(rectangle.dimension, mySize)
    }

    companion object {
        private val DEFAULT_LOCATION = GeoRectangle(-124.76, 25.52, -66.94, 49.39)
    }
}
