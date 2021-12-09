/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.config

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.datalore.base.typedGeometry.center
import jetbrains.livemap.LiveMap
import jetbrains.livemap.World
import jetbrains.livemap.WorldRectangle
import jetbrains.livemap.core.projections.GeoProjection
import jetbrains.livemap.core.projections.MapRuler
import jetbrains.livemap.fragment.newFragmentProvider
import jetbrains.livemap.geocoding.MapLocationGeocoder
import jetbrains.livemap.mapengine.MapProjection
import jetbrains.livemap.mapengine.viewport.Viewport
import jetbrains.livemap.mapengine.viewport.ViewportHelper
import jetbrains.livemap.toClientPoint

const val MIN_ZOOM = 1
const val MAX_ZOOM = 15
const val TILE_PIXEL_SIZE = 256.0
val WORLD_RECTANGLE = WorldRectangle(0.0, 0.0, TILE_PIXEL_SIZE, TILE_PIXEL_SIZE)
val DEFAULT_LOCATION = GeoRectangle(-124.76, 25.52, -66.94, 49.39)

fun createMapProjection(geoProjection: GeoProjection): MapProjection {
    return MapProjectionBuilder(geoProjection, WORLD_RECTANGLE).apply {
        reverseY = true
    }.create()
}

class LiveMapFactory(
    private val myLiveMapSpec: LiveMapSpec
) {
    private val myMapProjection: MapProjection
    private val myViewport: Viewport
    private val myMapRuler: MapRuler<World>

    init {
        myMapProjection = createMapProjection(myLiveMapSpec.geoProjection)
        val multiMapHelper = ViewportHelper(myMapProjection.mapRect, myLiveMapSpec.isLoopX, myLiveMapSpec.isLoopY)
        myMapRuler = multiMapHelper

        myViewport = Viewport.create(
            multiMapHelper,
            myLiveMapSpec.size.toClientPoint(),
            myMapProjection.mapRect.center,
            myLiveMapSpec.minZoom,
            myLiveMapSpec.maxZoom
        )
        myViewport.zoom = myViewport.minZoom + 1 // + 1 to not blink zoomOut button in disabled state
    }

    fun createLiveMap(): Async<LiveMap> {
        return Asyncs.constant(
            LiveMap(
                myMapRuler = myMapRuler,
                myMapProjection = myMapProjection,
                viewport = myViewport,
                layers = myLiveMapSpec.layers,
                myBasemapTileSystemProvider = myLiveMapSpec.basemapTileSystemProvider,
                myFragmentProvider = newFragmentProvider(myLiveMapSpec.geocodingService, myLiveMapSpec.size),
                myDevParams = myLiveMapSpec.devParams,
                myMapLocationConsumer = myLiveMapSpec.mapLocationConsumer,
                myMapLocationRect = myLiveMapSpec.location
                    ?.getBBox(
                        MapLocationGeocoder(
                            myLiveMapSpec.geocodingService,
                            myMapRuler,
                            myMapProjection
                        )
                    ),
                myZoom = myLiveMapSpec.zoom,
                myAttribution = myLiveMapSpec.attribution,
                myCursorService = myLiveMapSpec.cursorService
            )
        )
    }
}