package jetbrains.livemap

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.projectionGeometry.GeoRectangle
import jetbrains.datalore.base.projectionGeometry.center
import jetbrains.gis.tileprotocol.http.HttpTileTransport
import jetbrains.livemap.DevParams.Companion.COMPUTATION_PROJECTION_QUANT
import jetbrains.livemap.DevParams.Companion.DEBUG_TILES
import jetbrains.livemap.DevParams.Companion.RASTER_TILES
import jetbrains.livemap.DevParams.Companion.VECTOR_TILES
import jetbrains.livemap.api.internalTiles
import jetbrains.livemap.entities.regions.EmptinessChecker
import jetbrains.livemap.fragments.FragmentProvider
import jetbrains.livemap.projections.*
import jetbrains.livemap.projections.ProjectionUtil.TILE_PIXEL_SIZE
import jetbrains.livemap.projections.ProjectionUtil.createMapProjection
import jetbrains.livemap.tiles.TileLoadingSystemBuilder
import jetbrains.livemap.tiles.TileLoadingSystemBuilder.DummyTileLoadingSystemBuilder
import jetbrains.livemap.tiles.raster.RasterTileLoadingSystemBuilder
import jetbrains.livemap.tiles.vector.VectorTileLoadingSystemBuilder

class LiveMapFactory(private val myLiveMapSpec: LiveMapSpec) : BaseLiveMapFactory {
    private val myMapProjection: MapProjection
    private val myViewProjection: ViewProjection
    private val myMapRuler: MapRuler<World>

    init {
        val mapRect = WorldRectangle(0.0, 0.0, TILE_PIXEL_SIZE, TILE_PIXEL_SIZE)
        myMapProjection = createMapProjection(myLiveMapSpec.projectionType, mapRect)
        val multiMapHelper = MultiMapHelper(mapRect, myLiveMapSpec.isLoopX, myLiveMapSpec.isLoopY)
        myMapRuler = multiMapHelper
        myViewProjection = ViewProjection.create(
            multiMapHelper,
            myLiveMapSpec.size.toClientPoint(),
            mapRect.center
        )
    }

    override fun createLiveMap(): Async<BaseLiveMap> {
        val mapDataGeocodingHelper = MapDataGeocodingHelper(
            myLiveMapSpec.size,
            myLiveMapSpec.geocodingService,
            myLiveMapSpec.layers,
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

    private fun createTileLoadingBuilder(): TileLoadingSystemBuilder {
        if (myLiveMapSpec.devParams.isSet(DEBUG_TILES))
            return DummyTileLoadingSystemBuilder()

        val rasterTiles = myLiveMapSpec.devParams.read(RASTER_TILES)
        if (rasterTiles != null)
            return RasterTileLoadingSystemBuilder(
                HttpTileTransport(rasterTiles.host, rasterTiles.port, ""),
                rasterTiles.format
            )

        val vectorTiles = myLiveMapSpec.devParams.read(VECTOR_TILES)
        return VectorTileLoadingSystemBuilder(
            myLiveMapSpec.devParams.read(COMPUTATION_PROJECTION_QUANT),
            internalTiles {
                host = vectorTiles.host
                port = vectorTiles.port
                theme = vectorTiles.theme
            }
        )
    }

    private fun createLiveMap(zoom: Int, center: WorldPoint, regionBBoxes: Map<String, GeoRectangle>): BaseLiveMap {
        myViewProjection.zoom = zoom
        myViewProjection.center = center

        return LiveMap(
            myMapProjection,
            myViewProjection,
            myLiveMapSpec.layers,
            createTileLoadingBuilder(),
            FragmentProvider.create(myLiveMapSpec.geocodingService, myLiveMapSpec.size),
            myLiveMapSpec.devParams,
            EmptinessChecker.BBoxEmptinessChecker(regionBBoxes),
            myLiveMapSpec.mapLocationConsumer
        )
    }
}