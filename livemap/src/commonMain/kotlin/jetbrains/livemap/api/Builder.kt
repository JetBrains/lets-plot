/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs.constant
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoRectangle
import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.Rect
import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.base.values.Color
import jetbrains.gis.geoprotocol.*
import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.gis.tileprotocol.TileService
import jetbrains.gis.tileprotocol.socket.Socket
import jetbrains.gis.tileprotocol.socket.SocketBuilder
import jetbrains.gis.tileprotocol.socket.SocketHandler
import jetbrains.gis.tileprotocol.socket.TileWebSocketBuilder
import jetbrains.livemap.DevParams
import jetbrains.livemap.LayerProvider
import jetbrains.livemap.LiveMapSpec
import jetbrains.livemap.MapLocation
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.rendering.layers.LayerManager
import jetbrains.livemap.obj2entity.TextMeasurer
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.ProjectionType

@DslMarker
annotation class LiveMapDsl {}

@LiveMapDsl
class LiveMapBuilder {
    lateinit var size: DoubleVector
    lateinit var geocodingService: GeocodingService
    lateinit var tileService: TileService
    lateinit var layerProvider: LayerProvider

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
    val devParams: DevParams,
    val textMeasurer: TextMeasurer
)

@LiveMapDsl
class ChartSource {
    var lon: Double = 0.0
    var lat: Double = 0.0
    var radius: Double = 0.0

    var strokeColor: Color = Color.BLACK
    var strokeWidth: Double = 0.0

    var indices: List<Int> = emptyList()
    var values: List<Double> = emptyList()
    var colors: List<Color> = emptyList()
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
            field = v; mapLocation = v?.let { MapLocation.create(GeoRectangle(it.x, it.y, it.x, it.y)) }
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
    layerProvider = DemoLayerProvider(devParams, block)
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

fun internalTiles(block: LiveMapTileServiceBuilder.() -> Unit): TileService {
    return LiveMapTileServiceBuilder()
        .apply {
            theme = TileService.Theme.COLOR
            host = "10.0.0.127"
            port = 3933
        }
        .apply(block).build()
}

fun liveMapTiles(block: LiveMapTileServiceBuilder.() -> Unit) = LiveMapTileServiceBuilder().apply(block).build()

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

