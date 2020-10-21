/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.config

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.typedGeometry.center
import jetbrains.livemap.LiveMap
import jetbrains.livemap.LiveMapConstants.TILE_PIXEL_SIZE
import jetbrains.livemap.camera.Viewport
import jetbrains.livemap.camera.ViewportHelper
import jetbrains.livemap.core.projections.MapRuler
import jetbrains.livemap.projection.*
import jetbrains.livemap.services.MapLocationGeocoder
import jetbrains.livemap.services.newFragmentProvider

class LiveMapFactory(
    private val myLiveMapSpec: LiveMapSpec
) {
    private val myMapProjection: MapProjection
    private val myViewport: Viewport
    private val myMapRuler: MapRuler<World>

    init {
        val mapRect = WorldRectangle(0.0, 0.0, TILE_PIXEL_SIZE, TILE_PIXEL_SIZE)
        myMapProjection = createMapProjection(myLiveMapSpec.projectionType, mapRect)
        val multiMapHelper = ViewportHelper(mapRect, myLiveMapSpec.isLoopX, myLiveMapSpec.isLoopY)
        myMapRuler = multiMapHelper

        myViewport = Viewport.create(
            multiMapHelper,
            myLiveMapSpec.size.toClientPoint(),
            mapRect.center,
            myLiveMapSpec.minZoom,
            myLiveMapSpec.maxZoom
        )
    }

    fun createLiveMap(): Async<LiveMap> {
        myViewport.zoom = 1

        return Asyncs.constant(
            LiveMap(
                myMapRuler = myMapRuler,
                myMapProjection = myMapProjection,
                viewport = myViewport,
                layers = myLiveMapSpec.layers,
                myTileSystemProvider = myLiveMapSpec.tileSystemProvider,
                myFragmentProvider = newFragmentProvider(myLiveMapSpec.geocodingService, myLiveMapSpec.size),
                myDevParams = myLiveMapSpec.devParams,
                myMapLocationConsumer = myLiveMapSpec.mapLocationConsumer,
                myGeocodingService = myLiveMapSpec.geocodingService,
                myMapLocationRect = myLiveMapSpec.location
                    ?.getBBox(
                        MapLocationGeocoder(
                            myLiveMapSpec.geocodingService,
                            myMapRuler,
                            myMapProjection
                        )
                    ),
                myZoom = myLiveMapSpec.zoom,
                myAttribution = myLiveMapSpec.attribution
            )
        )
    }
}