package jetbrains.livemap

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.DoubleVector.Companion.ZERO
import jetbrains.datalore.base.projectionGeometry.GeoRectangle
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator
import jetbrains.livemap.entities.regions.EmptinessChecker
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.MapRuler
import jetbrains.livemap.projections.MultiMapHelper
import jetbrains.livemap.projections.ProjectionUtil.TILE_PIXEL_SIZE
import jetbrains.livemap.projections.ProjectionUtil.createMapProjection
import jetbrains.livemap.projections.ViewProjection
import jetbrains.livemap.tilegeometry.TileGeometryProvider

class LiveMapFactory(private val myLiveMapSpec: LiveMapSpec) : BaseLiveMapFactory {
    private val myMapProjection: MapProjection
    private val myViewProjection: ViewProjection
    private val myMapRuler: MapRuler
    private val myRegionGeometryStorage: RegionGeometryStorage

    init {

        val mapRect = DoubleRectangle(ZERO, DoubleVector(TILE_PIXEL_SIZE, TILE_PIXEL_SIZE))
        myMapProjection = createMapProjection(myLiveMapSpec.projectionType, mapRect)
        val multiMapHelper = MultiMapHelper(mapRect, myLiveMapSpec.isLoopX, myLiveMapSpec.isLoopY)
        myMapRuler = multiMapHelper
        myViewProjection = ViewProjection.create(
            multiMapHelper,
            myLiveMapSpec.size,
            mapRect.center
        )

        myRegionGeometryStorage = RegionGeometryStorage()
    }

    override fun createGeomTargetLocator(): GeomTargetLocator {
        return object: GeomTargetLocator {
            override fun search(coord: DoubleVector): GeomTargetLocator.LookupResult? {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
//        val targetLocatorFactory = TargetLocatorFactory(myMapRuler, myRegionGeometryStorage)
//
//        val targetLocators = ArrayList<TargetLocator>()
//        myLiveMapSpec.layers.forEach { mapLayer -> targetLocators.add(targetLocatorFactory.create(mapLayer)) }
//        return LivemapTargetLocator.create(targetLocators, myViewProjection, myRegionGeometryStorage)
    }

//    fun createLivemap(): Async<BaseLiveMap> {
//        val mapDataGeocodingHelper = MapDataGeocodingHelper(
//            myLiveMapSpec.size,
//            myLiveMapSpec.geocodingService,
//            myLiveMapSpec.layers,
//            myLiveMapSpec.level,
//            myLiveMapSpec.parent,
//            myLiveMapSpec.location,
//            myLiveMapSpec.zoom,
//            myMapRuler,
//            myMapProjection,
//            true //for BBoxEmptinessChecker
//        )
//
//        return mapDataGeocodingHelper.geocodeMapData()
//            .map({ mapPosition -> createLivemap(mapPosition, mapDataGeocodingHelper.getRegionBBoxes()) })
//    }

    override fun createLiveMap(): Async<BaseLiveMap> {
        return Asyncs.constant(createLiveMap(2, ZERO, emptyMap()))
    }

    private fun createLiveMap(zoom: Int, center: DoubleVector, regionBBoxes: Map<String, GeoRectangle>): BaseLiveMap {
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