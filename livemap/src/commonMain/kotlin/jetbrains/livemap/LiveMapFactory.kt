/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.typedGeometry.center
import jetbrains.livemap.camera.Viewport
import jetbrains.livemap.camera.ViewportHelper
import jetbrains.livemap.entities.geocoding.GeocodingProvider
import jetbrains.livemap.fragments.FragmentProvider
import jetbrains.livemap.projections.*
import jetbrains.livemap.projections.ProjectionUtil.TILE_PIXEL_SIZE
import jetbrains.livemap.projections.ProjectionUtil.createMapProjection
import jetbrains.livemap.tiles.TileLoadingSystemFactory.Companion.createTileLoadingFactory

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
            mapRect.center
        )
    }

    fun createLiveMap(): Async<LiveMap> {
        myViewport.zoom = 1

        return Asyncs.constant(LiveMap(
            myMapProjection,
            myViewport,
            myLiveMapSpec.layerProvider,
            createTileLoadingFactory(myLiveMapSpec.devParams),
            FragmentProvider.create(myLiveMapSpec.geocodingService, myLiveMapSpec.size),
            myLiveMapSpec.devParams,
            myLiveMapSpec.mapLocationConsumer,
            GeocodingProvider(
                myLiveMapSpec.geocodingService,
                myLiveMapSpec.level,
                myLiveMapSpec.parent
            ),
            myLiveMapSpec.location
                ?.getBBox(MapLocationGeocoder(myLiveMapSpec.geocodingService, myMapRuler, myMapProjection)),
            myLiveMapSpec.zoom
        ))
    }
}