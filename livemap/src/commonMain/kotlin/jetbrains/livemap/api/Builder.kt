/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.*
import jetbrains.datalore.base.typedGeometry.*
import jetbrains.datalore.base.values.Color
import jetbrains.gis.geoprotocol.FeatureLevel
import jetbrains.gis.geoprotocol.GeoTransportImpl
import jetbrains.gis.geoprotocol.GeocodingService
import jetbrains.gis.geoprotocol.MapRegion
import jetbrains.gis.tileprotocol.TileService
import jetbrains.gis.tileprotocol.socket.TileWebSocketBuilder
import jetbrains.livemap.config.DevParams
import jetbrains.livemap.config.LiveMapSpec
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.projections.GeoProjection
import jetbrains.livemap.core.projections.Projections
import jetbrains.livemap.core.projections.createArcPath
import jetbrains.livemap.core.rendering.TextMeasurer
import jetbrains.livemap.core.rendering.layers.LayerManager
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.mapengine.LayerEntitiesComponent
import jetbrains.livemap.mapengine.MapProjection
import jetbrains.livemap.mapengine.basemap.BasemapTileSystemProvider
import jetbrains.livemap.mapengine.camera.CameraListenerComponent
import jetbrains.livemap.mapengine.camera.CenterChangedComponent
import jetbrains.livemap.mapengine.camera.ZoomFractionChangedComponent
import jetbrains.livemap.ui.CursorService
import kotlin.math.abs

@DslMarker
annotation class LiveMapDsl

@LiveMapDsl
class LiveMapBuilder {
    lateinit var size: DoubleVector
    lateinit var geocodingService: GeocodingService
    lateinit var tileSystemProvider: BasemapTileSystemProvider

    var layers: List<LayersBuilder.() -> Unit> = emptyList()

    var zoom: Int? = null
    var interactive: Boolean = true
    var mapLocation: MapLocation? = null

    var projection: GeoProjection = Projections.mercator()
    var isLoopX: Boolean = true
    var isLoopY: Boolean = false

    var mapLocationConsumer: (DoubleRectangle) -> Unit = { _ -> }

    var attribution: String? = null

    var devParams: DevParams =
        DevParams(HashMap<String, Any>())

    fun build(): LiveMapSpec {
        return LiveMapSpec(
            size = size,
            zoom = zoom,
            isInteractive = interactive,
            layers = layers,

            location = mapLocation,

            geoProjection = projection,
            isLoopX = isLoopX,
            isLoopY = isLoopY,

            geocodingService = geocodingService,

            mapLocationConsumer = mapLocationConsumer,

            basemapTileSystemProvider = tileSystemProvider,

            devParams = devParams,

            attribution = attribution,

            // deprecated
            isClustering = false,
            isLabels = true,
            isScaled = false,
            isTiles = true,
            isUseFrame = true,
            cursorService = CursorService()
        )
    }
}

@LiveMapDsl
class LayersBuilder(
    val myComponentManager: EcsComponentManager,
    val layerManager: LayerManager,
    val mapProjection: MapProjection,
    val zoomable: Boolean,
    val textMeasurer: TextMeasurer
)

@LiveMapDsl
class Symbol {
    var layerIndex: Int? = null
    var radius: Double = 0.0
    var point: Vec<LonLat>? = null

    var strokeColor: Color = Color.BLACK
    var strokeWidth: Double = 0.0

    var indices: List<Int> = emptyList()
    var values: List<Double> = emptyList()
    var colors: List<Color> = emptyList()
}

fun geometry(
    points: List<LonLatPoint>,
    isClosed: Boolean,
    isGeodesic: Boolean
): MultiPolygon<LonLat> {
    val coord = points.map(::limitCoord)

    return when {
        isClosed -> createMultiPolygon(coord)
        else -> coord
            .run { if (isGeodesic) createArcPath(this) else this }
            .run(::splitPathByAntiMeridian)
            .map { path -> Polygon(listOf(Ring(path))) }
            .run(::MultiPolygon)
    }
}

fun limitCoord(point: Vec<LonLat>): Vec<LonLat> {
    return explicitVec(
        limitLon(point.x),
        limitLat(point.y)
    )
}

private const val FULL_ANGLE = 360.0
private const val STRAIGHT_ANGLE = 180.0

fun splitPathByAntiMeridian(path: List<Vec<LonLat>>): List<List<Vec<LonLat>>> {
    val pathList = ArrayList<List<Vec<LonLat>>>()
    var currentPath = ArrayList<Vec<LonLat>>()
    if (path.isNotEmpty()) {
        currentPath.add(path[0])

        for (i in 1 until path.size) {
            val prev = path[i - 1]
            val next = path[i]
            val lonDelta = abs(next.x - prev.x)

            if (lonDelta > FULL_ANGLE - lonDelta) {
                val sign = (if (prev.x < 0.0) -1 else +1).toDouble()

                val x1 = prev.x - sign * STRAIGHT_ANGLE
                val x2 = next.x + sign * STRAIGHT_ANGLE
                val lat = (next.y - prev.y) * (if (x2 == x1) 1.0 / 2.0 else x1 / (x1 - x2)) + prev.y

                currentPath.add(explicitVec(sign * STRAIGHT_ANGLE, lat))
                pathList.add(currentPath)
                currentPath = ArrayList()
                currentPath.add(explicitVec(-sign * STRAIGHT_ANGLE, lat))
            }

            currentPath.add(next)
        }
    }

    pathList.add(currentPath)
    return pathList
}

@LiveMapDsl
class Location {
    var osmId: String? = null
        set(v) {
            field = v; mapLocation = v?.let { MapLocation.create(MapRegion.withId(it)) }
        }

    var coordinate: Vec<LonLat>? = null
        set(v) {
            field = v; mapLocation = v?.let { MapLocation.create(
                GeoRectangle(
                    it.x,
                    it.y,
                    it.x,
                    it.y
                )
            ) }
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
@kotlinx.coroutines.ObsoleteCoroutinesApi
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
            + CameraListenerComponent()
            + CenterChangedComponent()
            + ZoomFractionChangedComponent()
        }
}

class MapEntityFactory(layerEntity: EcsEntity) {
    private val myComponentManager: EcsComponentManager = layerEntity.componentManager
    private val myParentLayerComponent: ParentLayerComponent = ParentLayerComponent(layerEntity.id)
    private val myLayerEntityComponent: LayerEntitiesComponent = layerEntity.get()

    fun createMapEntity(name: String): EcsEntity {
        return mapEntity(myComponentManager, myParentLayerComponent, name)
            .also { myLayerEntityComponent.add(it.id) }
    }
}

fun liveMapConfig(block: LiveMapBuilder.() -> Unit) = LiveMapBuilder().apply(block)

fun LiveMapBuilder.layers(block: LayersBuilder.() -> Unit) {
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
        isLoopX = it.loopX
        isLoopY = it.loopY
    }
}

@kotlinx.coroutines.ObsoleteCoroutinesApi
fun liveMapVectorTiles(block: LiveMapTileServiceBuilder.() -> Unit) =
    LiveMapTileServiceBuilder().apply(block).build()

fun liveMapGeocoding(block: LiveMapGeocodingServiceBuilder.() -> Unit): GeocodingService {
    return LiveMapGeocodingServiceBuilder().apply(block).build()
}
