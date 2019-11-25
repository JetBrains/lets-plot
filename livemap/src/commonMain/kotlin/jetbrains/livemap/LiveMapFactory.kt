/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.projectionGeometry.center
import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.livemap.camera.Viewport
import jetbrains.livemap.camera.ViewportHelper
import jetbrains.livemap.entities.regions.EmptinessChecker
import jetbrains.livemap.fragments.FragmentProvider
import jetbrains.livemap.projections.*
import jetbrains.livemap.projections.ProjectionUtil.TILE_PIXEL_SIZE
import jetbrains.livemap.projections.ProjectionUtil.createMapProjection
import jetbrains.livemap.tiles.TileLoadingSystemFactory.Companion.createTileLoadingFactory

class LiveMapFactory(private val myLiveMapSpec: LiveMapSpec) : BaseLiveMapFactory {
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

    override fun createLiveMap(): Async<BaseLiveMap> {
        val mapDataGeocodingHelper = MapDataGeocodingHelper(
            myLiveMapSpec.size,
            myLiveMapSpec.geocodingService,
            myLiveMapSpec.layerProvider.layers,
            myLiveMapSpec.level,
            myLiveMapSpec.parent,
            myLiveMapSpec.location,
            myLiveMapSpec.zoom,
            myMapRuler,
            myMapProjection,
            true //for BBoxEmptinessChecker
        )

        return mapDataGeocodingHelper.geocodeMapData()
            .map { mapPosition ->
                mapPosition
                    ?.let { createLiveMap(it.zoom, it.coordinate, mapDataGeocodingHelper.regionBBoxes) }
                    ?: error("Map position must to be not null")
            }
    }

    private fun createLiveMap(zoom: Int, center: WorldPoint, regionBBoxes: Map<String, GeoRectangle>): BaseLiveMap {
        myViewport.zoom = zoom
        myViewport.position = center

        return LiveMap(
            myMapProjection,
            myViewport,
            myLiveMapSpec.layerProvider,
            createTileLoadingFactory(myLiveMapSpec.devParams),
            FragmentProvider.create(myLiveMapSpec.geocodingService, myLiveMapSpec.size),
            myLiveMapSpec.devParams,
            EmptinessChecker.BBoxEmptinessChecker(regionBBoxes),
            myLiveMapSpec.mapLocationConsumer
        )
    }
}