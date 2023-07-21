/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:Suppress("unused")

package org.jetbrains.letsPlot.livemap.api

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.spatial.GeoRectangle
import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.LonLatPoint
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.center
import org.jetbrains.letsPlot.gis.geoprotocol.FeatureLevel
import org.jetbrains.letsPlot.gis.geoprotocol.GeoTransportImpl
import org.jetbrains.letsPlot.gis.geoprotocol.GeocodingService
import org.jetbrains.letsPlot.gis.geoprotocol.MapRegion
import org.jetbrains.letsPlot.gis.tileprotocol.TileService
import org.jetbrains.letsPlot.gis.tileprotocol.socket.TileWebSocketBuilder
import org.jetbrains.letsPlot.livemap.LiveMap
import org.jetbrains.letsPlot.livemap.WorldPoint
import org.jetbrains.letsPlot.livemap.chart.fragment.newFragmentProvider
import org.jetbrains.letsPlot.livemap.config.DevParams
import org.jetbrains.letsPlot.livemap.config.MAX_ZOOM
import org.jetbrains.letsPlot.livemap.config.MIN_ZOOM
import org.jetbrains.letsPlot.livemap.config.createMapProjection
import org.jetbrains.letsPlot.livemap.core.GeoProjection
import org.jetbrains.letsPlot.livemap.core.Projections
import org.jetbrains.letsPlot.livemap.core.ecs.ComponentsList
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.core.ecs.addComponents
import org.jetbrains.letsPlot.livemap.core.graphics.TextMeasurer
import org.jetbrains.letsPlot.livemap.core.layers.CanvasLayerComponent
import org.jetbrains.letsPlot.livemap.core.layers.LayerManager
import org.jetbrains.letsPlot.livemap.core.layers.PanningPolicy
import org.jetbrains.letsPlot.livemap.core.layers.ParentLayerComponent
import org.jetbrains.letsPlot.livemap.geocoding.*
import org.jetbrains.letsPlot.livemap.mapengine.LayerEntitiesComponent
import org.jetbrains.letsPlot.livemap.mapengine.MapProjection
import org.jetbrains.letsPlot.livemap.mapengine.basemap.BasemapTileSystemProvider
import org.jetbrains.letsPlot.livemap.mapengine.basemap.Tilesets.chessboard
import org.jetbrains.letsPlot.livemap.mapengine.viewport.Viewport
import org.jetbrains.letsPlot.livemap.mapengine.viewport.ViewportHelper
import org.jetbrains.letsPlot.livemap.toClientPoint
import org.jetbrains.letsPlot.livemap.ui.CursorService

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

internal fun EcsEntity.setInitializer(block: ComponentsList.(worldPoint: org.jetbrains.letsPlot.livemap.WorldPoint) -> Unit): EcsEntity {
    return add(PointInitializerComponent(block))
}
