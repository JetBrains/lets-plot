/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes.Companion.COLOR
import jetbrains.datalore.plot.base.Aes.Companion.FILL
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.geom.LiveMapProvider
import jetbrains.datalore.plot.base.geom.LiveMapProvider.LiveMapData
import jetbrains.datalore.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.livemap.LivemapConstants.Projection.*
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.LayerRendererUtil.LayerRendererData
import jetbrains.datalore.plot.builder.LayerRendererUtil.createLayerRendererData
import jetbrains.datalore.plot.config.*
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.CONST_SIZE_ZOOMIN
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.DATA_SIZE_ZOOMIN
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.DEV_PARAMS
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.LOCATION
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.PROJECTION
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.TILES
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.Tile
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.Tile.ATTRIBUTION
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.Tile.MAX_ZOOM
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.Tile.MIN_ZOOM
import jetbrains.gis.tileprotocol.TileService
import jetbrains.livemap.LiveMapLocation
import jetbrains.livemap.api.LiveMapBuilder
import jetbrains.livemap.api.Services
import jetbrains.livemap.api.liveMapGeocoding
import jetbrains.livemap.api.liveMapVectorTiles
import jetbrains.livemap.config.DevParams
import jetbrains.livemap.config.LiveMapCanvasFigure
import jetbrains.livemap.core.Clipboard
import jetbrains.livemap.core.projections.Projections.azimuthalEqualArea
import jetbrains.livemap.core.projections.Projections.conicEqualArea
import jetbrains.livemap.core.projections.Projections.geographic
import jetbrains.livemap.core.projections.Projections.mercator
import jetbrains.livemap.mapengine.basemap.BasemapTileSystemProvider
import jetbrains.livemap.mapengine.basemap.Tilesets
import jetbrains.livemap.ui.CursorService

object LiveMapProvider {

    fun injectLiveMapProvider(
        plotTiles: List<List<GeomLayer>>,
        liveMapOptions: Map<*, *>,
        cursorServiceConfig: CursorServiceConfig,
    ) {
        plotTiles.forEach { tileLayers ->
            if (tileLayers.any(GeomLayer::isLiveMap)) {
                require(tileLayers.count(GeomLayer::isLiveMap) == 1)
                require(tileLayers.first().isLiveMap)
                tileLayers.first().setLiveMapProvider(
                    MyLiveMapProvider(
                        tileLayers.map { createLayerRendererData(it) },
                        liveMapOptions,
                        cursorServiceConfig.cursorService
                    )
                )
            }
        }
    }

    private class MyLiveMapProvider internal constructor(
        private val letsPlotLayers: List<LayerRendererData>,
        private val myLiveMapOptions: Map<*, *>,
        private val cursor: CursorService,
    ) : LiveMapProvider {
        init {
            require(letsPlotLayers.isNotEmpty())
            require(letsPlotLayers.first().geomKind == GeomKind.LIVE_MAP) { "geom_livemap have to be the very first geom after ggplot()" }
        }

        override fun createLiveMap(bounds: DoubleRectangle): LiveMapData {
            val plotLayers = letsPlotLayers.drop(1)

            val liveMapBuilder: LiveMapBuilder = LiveMapBuilder().apply {
                size = bounds.dimension
                projection = when (myLiveMapOptions.getEnum(PROJECTION) ?: EPSG3857) {
                    EPSG3857 -> mercator()
                    EPSG4326 -> geographic()
                    AZIMUTHAL -> azimuthalEqualArea()
                    CONIC -> conicEqualArea()
                }
                mapLocation = ConfigUtil.createMapLocation(myLiveMapOptions.read(LOCATION))
                mapLocationConsumer = { Clipboard.copy(LiveMapLocation.getLocationString(it)) }
                devParams = DevParams(myLiveMapOptions.getMap(DEV_PARAMS) ?: emptyMap<Any, Any>())
                cursorService = cursor
                attribution = myLiveMapOptions.getString(TILES, ATTRIBUTION)
                minZoom = myLiveMapOptions.getInt(TILES, MIN_ZOOM) ?: minZoom
                maxZoom = myLiveMapOptions.getInt(TILES, MAX_ZOOM) ?: maxZoom
                zoom = myLiveMapOptions.getInt(Option.Geom.LiveMap.ZOOM)
                showCoordPickTools = myLiveMapOptions.getBool(Option.Geom.LiveMap.SHOW_COORD_PICK_TOOLS) ?: false
                geocodingService = myLiveMapOptions.getMap(Option.Geom.LiveMap.GEOCODING)
                    ?.getString("url")
                    ?.let { liveMapGeocoding { url = it } }
                    ?: Services.bogusGeocodingService()

                tileSystemProvider = createTileSystemProvider(
                    myLiveMapOptions.getMap(TILES) ?: error("Tiles must be condigured"),
                    devParams.isSet(DevParams.DEBUG_TILES),
                    devParams.read(DevParams.COMPUTATION_PROJECTION_QUANT)
                )
                layers = LayerConverter.convert(
                    plotLayers,
                    myLiveMapOptions.getInt(DATA_SIZE_ZOOMIN) ?: 0,
                    myLiveMapOptions.getInt(CONST_SIZE_ZOOMIN) ?: -1
                )
            }

            val targetSource = HashMap<Pair<Int, Int>, ContextualMapping>()
            val colorMap = HashMap<Int, (Int) -> List<Color>>()
            plotLayers.onEachIndexed { layerIndex, layer ->
                val colorBarProvider = HintColorUtil.createColorMarkerMapper(
                    layer.geomKind,
                    FILL in layer.mappedAes,
                    COLOR in layer.mappedAes
                )
                colorMap[layerIndex] = { i -> colorBarProvider(layer.aesthetics.dataPointAt(i)) }

                layer.aesthetics.dataPoints().forEach { dataPoint ->
                    targetSource[layerIndex to dataPoint.index()] = layer.contextualMapping
                }
            }

            return liveMapBuilder.build()
                .let(Asyncs::constant)
                .let { liveMapAsync ->
                    LiveMapData(
                        LiveMapCanvasFigure(liveMapAsync).apply {
                            setBounds(
                                Rectangle(
                                    bounds.origin.x.toInt(),
                                    bounds.origin.y.toInt(),
                                    bounds.dimension.x.toInt(),
                                    bounds.dimension.y.toInt()
                                )
                            )
                        },
                        LiveMapTargetLocator(liveMapAsync, targetSource, colorMap)
                    )
                }
        }
    }

    private fun createTileSystemProvider(
        options: Map<*, *>,
        debugTiles: Boolean,
        quant: Int,
    ): BasemapTileSystemProvider {
        if (debugTiles) {
            return Tilesets.chessboard()
        }

        fun splitSubdomains(url: String): List<String> {
            val openBracketIndex = url.indexOfFirst { it == '[' }
            val closeBracketIndex = url.indexOfLast { it == ']' }

            if (openBracketIndex < 0 || closeBracketIndex < 0) {
                // single domain
                return listOf(url)
            }

            if (openBracketIndex > closeBracketIndex) {
                throw IllegalArgumentException("Error parsing subdomains: wrong brackets order")
            }

            val subdomains = url.substring(openBracketIndex + 1, closeBracketIndex)
            if (subdomains.isEmpty()) {
                throw IllegalArgumentException("Empty subdomains list")
            }
            if (subdomains.any { it.lowercaseChar() !in 'a'..'z' }) {
                throw IllegalArgumentException("subdomain list contains non-letter symbols")
            }

            val urlStart = url.substring(0, openBracketIndex)
            val urlEnd = url.substring(closeBracketIndex + 1, url.length)
            return subdomains.map { urlStart + it + urlEnd }
        }

        return when (options[Tile.KIND]) {
            Tile.KIND_CHESSBOARD -> Tilesets.chessboard()
            Tile.KIND_SOLID -> Tilesets.solid(Color.parseHex(options.getString(Tile.FILL_COLOR)!!))
            Tile.KIND_RASTER_ZXY -> options.getString(Tile.URL)!!.let(::splitSubdomains).let(Tilesets::raster)
            Tile.KIND_VECTOR_LETS_PLOT -> Tilesets.letsPlot(
                quantumIterations = quant,
                tileService = liveMapVectorTiles {
                    options.getString(Tile.URL)?.let { url = it }
                    options.getString(Tile.THEME)?.let { theme = TileService.Theme.valueOf(it.uppercase()) }
                }
            )
            else -> throw IllegalArgumentException("Tile provider is not set.")
        }
    }

}
