/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:Suppress("unused")

package jetbrains.livemap.api

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.LonLatPoint
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.center
import jetbrains.gis.geoprotocol.FeatureLevel
import jetbrains.gis.geoprotocol.GeoTransportImpl
import jetbrains.gis.geoprotocol.GeocodingService
import jetbrains.gis.geoprotocol.MapRegion
import jetbrains.gis.tileprotocol.TileService
import jetbrains.gis.tileprotocol.socket.TileWebSocketBuilder
import jetbrains.livemap.LiveMap
import jetbrains.livemap.WorldPoint
import jetbrains.livemap.chart.fragment.newFragmentProvider
import jetbrains.livemap.config.DevParams
import jetbrains.livemap.config.MAX_ZOOM
import jetbrains.livemap.config.MIN_ZOOM
import jetbrains.livemap.config.createMapProjection
import jetbrains.livemap.core.GeoProjection
import jetbrains.livemap.core.Projections
import jetbrains.livemap.core.ecs.ComponentsList
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.graphics.TextMeasurer
import jetbrains.livemap.core.layers.CanvasLayerComponent
import jetbrains.livemap.core.layers.LayerManager
import jetbrains.livemap.core.layers.PanningPolicy
import jetbrains.livemap.core.layers.ParentLayerComponent
import jetbrains.livemap.geocoding.*
import jetbrains.livemap.mapengine.LayerEntitiesComponent
import jetbrains.livemap.mapengine.MapProjection
import jetbrains.livemap.mapengine.basemap.BasemapTileSystemProvider
import jetbrains.livemap.mapengine.basemap.Tilesets.chessboard
import jetbrains.livemap.mapengine.viewport.Viewport
import jetbrains.livemap.mapengine.viewport.ViewportHelper
import jetbrains.livemap.toClientPoint
import jetbrains.livemap.ui.CursorService

@DslMarker
annotation class LiveMapDsl

@LiveMapDsl
class LiveMapBuilder {
    private var isLoopY: Boolean = false
    var size: DoubleVector = DoubleVector.ZERO
    var geocodingService: GeocodingService = Services.bogusGeocodingService()
    var tileSystemProvider: BasemapTileSystemProvider = chessboard()
    var layers: List<FeatureLayerBuilder.() -> Unit> = emptyList()
    var interactive: Boolean = true
    var mapLocation: MapLocation? = null
    var projection: GeoProjection = Projections.mercator()
    var mapLocationConsumer: (DoubleRectangle) -> Unit = { _ -> }
    var attribution: String? = null
    var showCoordPickTools = false
    var zoom: Int? = null
    var minZoom: Int = MIN_ZOOM
    var maxZoom: Int = MAX_ZOOM
    var cursorService: CursorService = CursorService()

    var devParams: DevParams = DevParams(HashMap<String, Any>())

    fun build(): LiveMap {
        require(minZoom <= maxZoom) {
            "minZoom should be less than or equal to maxZoom"
        }
        require(zoom == null || zoom in IntRange(minZoom, maxZoom)) {
            "Zoom must be in range [${minZoom}, ${maxZoom}], but was $zoom"
        }

        val mapProjection = createMapProjection(projection)
        val viewportHelper = ViewportHelper(mapProjection.mapRect, myLoopX = projection.cylindrical, isLoopY)
        val viewport = Viewport.create(
            viewportHelper,
            size.toClientPoint(),
            mapProjection.mapRect.center,
            minZoom,
            maxZoom
        )
        viewport.zoom = zoom ?: (viewport.minZoom + 1) // + 1 to not blink zoomOut button in disabled state


        return LiveMap(
            myMapRuler = viewportHelper,
            myMapProjection = mapProjection,
            viewport = viewport,
            layers = layers,
            myBasemapTileSystemProvider = tileSystemProvider,
            myFragmentProvider = newFragmentProvider(geocodingService, size),
            myDevParams = devParams,
            myMapLocationConsumer = mapLocationConsumer,
            myMapLocationRect = mapLocation
                ?.getBBox(
                    MapLocationGeocoder(
                        geocodingService,
                        viewportHelper,
                        mapProjection
                    )
                ),
            myZoom = zoom,
            myAttribution = attribution,
            myShowCoordPickTools = showCoordPickTools,
            myCursorService = cursorService
        )

    }
}

@LiveMapDsl
class FeatureLayerBuilder(
    val myComponentManager: EcsComponentManager,
    val layerManager: LayerManager,
    val mapProjection: MapProjection,
    val textMeasurer: TextMeasurer
)


@LiveMapDsl
class Location {
    var osmId: String? = null
        set(v) {
            field = v; mapLocation = v?.let { MapLocation.create(MapRegion.withId(it)) }
        }

    var coordinate: Vec<LonLat>? = null
        set(v) {
            field = v
            mapLocation = v?.let { MapLocation.create(GeoRectangle(it.x, it.y, it.x, it.y)) }
        }

    internal var mapLocation: MapLocation? = null
    internal var hint: GeocodingHint? = null
}

@LiveMapDsl
class GeocodingHint {
    var level: FeatureLevel? = null
    var parent: MapRegion? = null
}


@LiveMapDsl
class Projection {
    var geoProjection = Projections.mercator()
    var loopX = true
    var loopY = false
}

@LiveMapDsl
class LiveMapTileServiceBuilder {
    lateinit var url: String
    var theme = TileService.Theme.COLOR

    fun build(): TileService {
        return TileService(TileWebSocketBuilder(url), theme)
    }
}

@LiveMapDsl
class LiveMapGeocodingServiceBuilder {
    lateinit var url: String

    fun build(): GeocodingService {
        return GeocodingService(GeoTransportImpl(url))
    }
}

fun mapEntity(
    componentManager: EcsComponentManager,
    parentLayerComponent: ParentLayerComponent,
    name: String
): EcsEntity {
    return componentManager
        .createEntity(name)
        .addComponents {
            + parentLayerComponent
        }
}

class FeatureEntityFactory(
    layerEntity: EcsEntity,
    private val panningPointsMaxCount: Int
) {
    private var pointsTotalCount = 0
    private val myComponentManager: EcsComponentManager = layerEntity.componentManager
    private val myParentLayerComponent: ParentLayerComponent = ParentLayerComponent(layerEntity.id)
    private val myLayerEntitiesComponent: LayerEntitiesComponent = layerEntity.get()
    private val myCanvasLayerComponent: CanvasLayerComponent = layerEntity.get()

    internal fun incrementLayerPointsTotalCount(pointsCount: Int) {
        pointsTotalCount += pointsCount
        if (pointsTotalCount > panningPointsMaxCount) {
            myCanvasLayerComponent.canvasLayer.panningPolicy = PanningPolicy.COPY
        }
    }

    fun createFeature(name: String): EcsEntity {
        return mapEntity(myComponentManager, myParentLayerComponent, name)
            .also { myLayerEntitiesComponent.add(it.id) }
    }

    fun createStaticFeatureWithLocation(name: String, point: LonLatPoint): EcsEntity =
        createStaticFeature(name, point).addComponents {
            + NeedLocationComponent
            + NeedCalculateLocationComponent
        }

    fun createStaticFeature(name: String, point: LonLatPoint): EcsEntity =
        createFeature(name)
            .add(LonLatComponent(point))
}

fun liveMapConfig(block: LiveMapBuilder.() -> Unit) = LiveMapBuilder().apply(block)

fun LiveMapBuilder.layers(block: FeatureLayerBuilder.() -> Unit) {
    layers = listOf(block)
}

fun LiveMapBuilder.location(block: Location.() -> Unit) {
    Location().apply(block).let { location ->
        mapLocation = location.mapLocation
    }
}

fun Location.geocodingHint(block: GeocodingHint.() -> Unit) {
    GeocodingHint().apply(block).let {
        hint = it
    }
}

fun LiveMapBuilder.projection(block: Projection.() -> Unit) {
    Projection().apply(block).let {
        projection = it.geoProjection
    }
}

fun liveMapVectorTiles(block: LiveMapTileServiceBuilder.() -> Unit) =
    LiveMapTileServiceBuilder().apply(block).build()

fun liveMapGeocoding(block: LiveMapGeocodingServiceBuilder.() -> Unit): GeocodingService {
    return LiveMapGeocodingServiceBuilder().apply(block).build()
}

internal fun EcsEntity.setInitializer(block: ComponentsList.(worldPoint: WorldPoint) -> Unit): EcsEntity {
    return add(PointInitializerComponent(block))
}
