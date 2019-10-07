package jetbrains.livemap

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoRectangle
import jetbrains.datalore.base.projectionGeometry.center
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator
import jetbrains.livemap.entities.regions.EmptinessChecker
import jetbrains.livemap.projections.*
import jetbrains.livemap.projections.ProjectionUtil.TILE_PIXEL_SIZE
import jetbrains.livemap.projections.ProjectionUtil.createMapProjection
import jetbrains.livemap.tilegeometry.TileGeometryProvider

class LiveMapFactory(private val myLiveMapSpec: LiveMapSpec) : BaseLiveMapFactory {
    private val myMapProjection: MapProjection
    private val myViewProjection: ViewProjection
    private val myMapRuler: MapRuler<World>
    private val myRegionGeometryStorage: RegionGeometryStorage

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

        myRegionGeometryStorage = RegionGeometryStorage()
    }

    override fun createGeomTargetLocator(): GeomTargetLocator {
        return object: GeomTargetLocator {
            override fun search(coord: DoubleVector): GeomTargetLocator.LookupResult? {
                return null
            }
        }
//        val targetLocatorFactory = TargetLocatorFactory(myMapRuler, myRegionGeometryStorage)
//
//        val targetLocators = ArrayList<TargetLocator>()
//        myLiveMapSpec.layers.forEach { mapLayer -> targetLocators.add(targetLocatorFactory.create(mapLayer)) }
//        return LivemapTargetLocator.create(targetLocators, myViewProjection, myRegionGeometryStorage)
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

    private fun createLiveMap(zoom: Int, center: WorldPoint, regionBBoxes: Map<String, GeoRectangle>): BaseLiveMap {
        myViewProjection.zoom = zoom
        myViewProjection.center = center

        return LiveMap(
            myMapProjection,
            myViewProjection,
            myLiveMapSpec.eventSource,
            myLiveMapSpec.layers,
            myLiveMapSpec.tileService,
            TileGeometryProvider.create(myLiveMapSpec.geocodingService, myLiveMapSpec.size),
            myRegionGeometryStorage,
            myLiveMapSpec.devParams,
            EmptinessChecker.BBoxEmptinessChecker(regionBBoxes),
            myLiveMapSpec.mapLocationConsumer
        )
    }
}