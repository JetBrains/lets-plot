/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs.constant
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.limitLat
import jetbrains.datalore.base.spatial.limitLon
import jetbrains.datalore.base.typedGeometry.*
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.base.values.Color
import jetbrains.gis.geoprotocol.*
import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.gis.tileprotocol.TileService
import jetbrains.gis.tileprotocol.socket.Socket
import jetbrains.gis.tileprotocol.socket.SocketBuilder
import jetbrains.gis.tileprotocol.socket.SocketHandler
import jetbrains.gis.tileprotocol.socket.TileWebSocketBuilder
import jetbrains.livemap.*
import jetbrains.livemap.LayerProvider.EmptyLayerProvider
import jetbrains.livemap.LayerProvider.LayerProviderImpl
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.rendering.layers.LayerManager
import jetbrains.livemap.entities.rendering.TextMeasurer
import jetbrains.livemap.projections.LonLatPoint
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.ProjectionType
import jetbrains.livemap.projections.createArcPath
import kotlin.math.abs

@DslMarker
annotation class LiveMapDsl

@LiveMapDsl
class LiveMapBuilder {
    lateinit var size: DoubleVector
    lateinit var geocodingService: GeocodingService
    lateinit var tileService: TileService
    var layerProvider: LayerProvider = EmptyLayerProvider()

    var zoom: Int? = null
    var interactive: Boolean = true
    var mapLocation: MapLocation? = null
    var level: FeatureLevel? = null
    var parent: MapRegion? = null

    var projectionType: ProjectionType = ProjectionType.MERCATOR
    var isLoopX: Boolean = true
    var isLoopY: Boolean = false

    var mapLocationConsumer: (DoubleRectangle) -> Unit = { _ -> Unit }
    var devParams: DevParams = DevParams(HashMap<String, Any>())


    fun params(vararg values: Pair<String, Any>) {
        devParams = DevParams(mapOf(*values))
    }

    fun build(): LiveMapSpec {
        return LiveMapSpec(
            size = size,
            zoom = zoom,
            isInteractive = interactive,
            layerProvider = layerProvider,

            level = level,
            location = mapLocation,
            parent = parent,

            projectionType = projectionType,
            isLoopX = isLoopX,
            isLoopY = isLoopY,

            geocodingService = geocodingService,

            mapLocationConsumer = mapLocationConsumer,

            devParams = devParams,

            // deprecated
            isClustering = false,
            isEnableMagnifier = false,
            isLabels = true,
            isScaled = false,
            isTiles = true,
            isUseFrame = true
        )
    }
}

@LiveMapDsl
class LayersBuilder(
    val myComponentManager: EcsComponentManager,
    val layerManager: LayerManager,
    val mapProjection: MapProjection,
    val pointScaling: Boolean,
    val textMeasurer: TextMeasurer
)

@LiveMapDsl
class ChartSource {
    var mapId: String? = null
    var point: Vec<LonLat>? = null

    var radius: Double = 0.0

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

    return if (isClosed) {
        createMultiPolygon(coord)
    } else {
        coord
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
    var name: String? = null
        set(v) {
            field = v; mapLocation = v?.let { MapLocation.create(MapRegion.withName(it)) }
        }
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
    var kind = ProjectionType.MERCATOR
    var loopX = true
    var loopY = false
}

@LiveMapDsl
class LiveMapTileServiceBuilder {
    var host = "localhost"
    var port: Int? = null
    var theme = TileService.Theme.COLOR

    fun build(): TileService {
        return TileService(TileWebSocketBuilder(host, port), theme)
    }
}

@LiveMapDsl
class LiveMapGeocodingServiceBuilder {
    private val subUrl = "/map_data/geocoding"

    var host = "localhost"
    var port: Int? = null

    fun build(): GeocodingService {
        return GeocodingService(GeoTransportImpl(host, port, subUrl))
    }
}

fun liveMapConfig(block: LiveMapBuilder.() -> Unit) = LiveMapBuilder().apply(block)

fun LiveMapBuilder.layers(block: LayersBuilder.() -> Unit) {
    layerProvider = LayerProviderImpl(devParams, block)
}

fun LiveMapBuilder.location(block: Location.() -> Unit) {
    Location().apply(block).let { location ->
        level = location.hint?.level
        parent = location.hint?.parent
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
        projectionType = it.kind
        isLoopX = it.loopX
        isLoopY = it.loopY
    }
}

fun internalTiles(): TileService {
    return liveMapTiles {
            theme = TileService.Theme.COLOR
            host = "10.0.0.127"
            port = 3933
        }
}

fun liveMapTiles(block: LiveMapTileServiceBuilder.() -> Unit) =
    LiveMapTileServiceBuilder().apply(block).build()

fun liveMapGeocoding(block: LiveMapGeocodingServiceBuilder.() -> Unit): GeocodingService {
    return LiveMapGeocodingServiceBuilder().apply(block).build()
}

val dummyGeocodingService: GeocodingService = GeocodingService(
    object : GeoTransport {
        override fun send(request: GeoRequest): Async<GeoResponse> {
            UNSUPPORTED("dummyGeocodingService.send")
        }
    }
)

val dummyTileService: TileService = object : TileService(DummySocketBuilder(), Theme.COLOR) {
    override fun getTileData(bbox: Rect<LonLat>, zoom: Int): Async<List<TileLayer>> {
        return constant(emptyList())
    }
}

internal class DummySocketBuilder : SocketBuilder {
    override fun build(handler: SocketHandler): Socket {
        return object : Socket {
            override fun connect() {
                UNSUPPORTED("DummySocketBuilder.connect")
            }

            override fun close() {
                UNSUPPORTED("DummySocketBuilder.close")
            }

            override fun send(msg: String) {
                UNSUPPORTED("DummySocketBuilder.send")
            }
        }
    }
}

