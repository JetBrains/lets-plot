package jetbrains.livemap.api

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.event.MouseEventSource
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.base.geom.LivemapGeom
import jetbrains.gis.geoprotocol.*
import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.gis.tileprotocol.TileService
import jetbrains.gis.tileprotocol.socket.Socket
import jetbrains.gis.tileprotocol.socket.SocketBuilder
import jetbrains.gis.tileprotocol.socket.SocketHandler
import jetbrains.gis.tileprotocol.socket.TileWebSocketBuilder
import jetbrains.livemap.DevParams
import jetbrains.livemap.LiveMapSpec
import jetbrains.livemap.MapLocation
import jetbrains.livemap.mapobjects.MapLayer
import jetbrains.livemap.mapobjects.MapLayerKind
import jetbrains.livemap.mapobjects.MapPoint
import jetbrains.livemap.projections.ProjectionType

@LiveMapDsl
class LiveMapBuilder {
    lateinit var size: DoubleVector
    lateinit var geocodingService: GeocodingService
    lateinit var tileService: TileService
    lateinit var mouseEventSource: MouseEventSource

    var zoom: Int? = null
    var interactive: Boolean = true
    var mapLocation: MapLocation? = null
    var level: FeatureLevel? = null
    var parent: MapRegion? = null
    var layers: MutableList<MapLayer> = ArrayList()
    var theme: LivemapGeom.Theme = LivemapGeom.Theme.COLOR

    var projectionType: ProjectionType = ProjectionType.MERCATOR
    var isLoopX: Boolean = true
    var isLoopY: Boolean = false

    var mapLocationConsumer: (DoubleRectangle) -> Unit = { _ -> Unit}
    var devParams: Map<String, Any> = HashMap()


    fun params(vararg vals: Pair<String, Any>) {
        this.devParams = mapOf(*vals)
    }

    fun build(): LiveMapSpec {
        return LiveMapSpec(
            size = size,
            zoom = zoom,
            isInteractive = interactive,
            layers = layers,

            level = level,
            location = mapLocation,
            parent = parent,

            projectionType = projectionType,
            isLoopX = isLoopX,
            isLoopY = isLoopY,

            tileService = tileService,
            theme = theme,

            geocodingService = geocodingService,

            mapLocationConsumer = mapLocationConsumer,
            eventSource = mouseEventSource,

            devParams = DevParams(devParams),

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

@DslMarker
annotation class LiveMapDsl {}

fun liveMapConfig(block: LiveMapBuilder.() -> Unit) = LiveMapBuilder().apply(block)

@LiveMapDsl
class LayersBuilder {
    val items = ArrayList<MapLayer>()
}

fun LiveMapBuilder.layers(block: LayersBuilder.() -> Unit) {
    this.layers.addAll(LayersBuilder().apply(block).items)
}

@LiveMapDsl
class Points {
    val items = ArrayList<MapPoint>()
}

fun LayersBuilder.points(block: Points.() -> Unit) {
    items.add(MapLayer(MapLayerKind.POINT, Points().apply(block).items))
}

@LiveMapDsl
class PointBuilder {
    var animation: Int? = null
    var label: String? = null
    var shape: Int? = null
    var lat: Double? = null
    var lon: Double? = null
    var radius: Double? = null
    var fillColor: Color? = null
    var strokeColor: Color? = null
    var strokeWidth: Double? = null
    var index: Int? = null
    var mapId: String? = null
    var regionId: String? = null
    fun build(): MapPoint {
        return MapPoint( index!!, mapId, regionId, DoubleVector(lon!!, lat!!), label!!, animation!!, shape!!, radius!!, fillColor!!, strokeColor!!, strokeWidth!!)
    }
}

fun point(block: PointBuilder.() -> Unit) {
    PointBuilder().apply(block)
}

@LiveMapDsl
class Location {
    var name: String? = null
        set(v) { field = v; mapRegion = v?.let { MapRegion.withName(it) } }
    var osmId: String? = null
        set(v) { field = v; mapRegion = v?.let { MapRegion.withId(it) } }

    var mapRegion: MapRegion? = null
    var hint: GeocodingHint? = null
}

fun LiveMapBuilder.location(block: Location.() -> Unit) {
    Location().apply(block).let { location ->
        this.level = location.hint?.level
        this.parent = location.hint?.parent
        this.mapLocation = location.mapRegion?.run(MapLocation.Companion::create)
    }
}

@LiveMapDsl
class GeocodingHint {
    var level: FeatureLevel? = null
    var parent: MapRegion? = null
}

fun Location.geocodingHint(block: GeocodingHint.() -> Unit) {
    GeocodingHint().apply(block).let {
        this.hint = it
    }
}


@LiveMapDsl
class Projection {
    var kind = ProjectionType.MERCATOR
    var loopX = true
    var loopY = false
}

fun LiveMapBuilder.projection(block: Projection.() -> Unit) {
    Projection().apply(block).let {
        this.projectionType = it.kind
        this.isLoopX = it.loopX
        this.isLoopY = it.loopY
    }
}


val dummyGeocodingService: GeocodingService = GeocodingService(
    object : GeoTransport {
        override fun send(request: GeoRequest): Async<GeoResponse> {
            TODO("not implemented")
        }
    }
)

val dummyTileService: TileService = object : TileService(DummySocketBuilder(), LivemapGeom.Theme.COLOR.name) {
    override fun getTileData(bbox: DoubleRectangle, zoom: Int): Async<List<TileLayer>> {
        return Asyncs.constant(emptyList())
    }
}

fun internalTiles(block: LiveMapTileServiceBuilder.() -> Unit): TileService {
    return LiveMapTileServiceBuilder()
        .apply{
            theme = LivemapGeom.Theme.COLOR
            host = "10.0.0.127"
            port = 3933
        }
        .apply(block).build()
}
fun liveMapTiles(block: LiveMapTileServiceBuilder.() -> Unit) = LiveMapTileServiceBuilder().apply(block).build()

@LiveMapDsl
class LiveMapTileServiceBuilder {
    var theme = LivemapGeom.Theme.COLOR
    var host = "localhost"
    var port = 3012

    fun build(): TileService {
        return TileService(TileWebSocketBuilder(host, port), theme.name.toLowerCase())
    }
}

internal class DummySocketBuilder : SocketBuilder {
    override fun build(handler: SocketHandler): Socket {
        return object : Socket {
            override fun connect() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun close() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun send(msg: String) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
    }
}

