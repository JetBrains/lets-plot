/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.livemap

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.geom.LiveMapProvider
import org.jetbrains.letsPlot.core.plot.base.geom.LiveMapProvider.LiveMapData
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.livemap.LivemapConstants.Projection.*
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTarget
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.LayerRendererUtil.LayerRendererData
import org.jetbrains.letsPlot.core.plot.builder.LayerRendererUtil.createLayerRendererData
import org.jetbrains.letsPlot.core.spec.*
import org.jetbrains.letsPlot.core.spec.Option.Geom.LiveMap.CONST_SIZE_ZOOMIN
import org.jetbrains.letsPlot.core.spec.Option.Geom.LiveMap.DATA_SIZE_ZOOMIN
import org.jetbrains.letsPlot.core.spec.Option.Geom.LiveMap.DEV_PARAMS
import org.jetbrains.letsPlot.core.spec.Option.Geom.LiveMap.LOCATION
import org.jetbrains.letsPlot.core.spec.Option.Geom.LiveMap.PROJECTION
import org.jetbrains.letsPlot.core.spec.Option.Geom.LiveMap.TILES
import org.jetbrains.letsPlot.core.spec.Option.Geom.LiveMap.Tile
import org.jetbrains.letsPlot.core.spec.Option.Geom.LiveMap.Tile.ATTRIBUTION
import org.jetbrains.letsPlot.core.spec.Option.Geom.LiveMap.Tile.MAX_ZOOM
import org.jetbrains.letsPlot.core.spec.Option.Geom.LiveMap.Tile.MIN_ZOOM
import org.jetbrains.letsPlot.gis.tileprotocol.TileService
import org.jetbrains.letsPlot.livemap.LiveMap
import org.jetbrains.letsPlot.livemap.LiveMapLocation
import org.jetbrains.letsPlot.livemap.api.LiveMapBuilder
import org.jetbrains.letsPlot.livemap.api.Services
import org.jetbrains.letsPlot.livemap.api.liveMapGeocoding
import org.jetbrains.letsPlot.livemap.api.liveMapVectorTiles
import org.jetbrains.letsPlot.livemap.chart.HoverObject
import org.jetbrains.letsPlot.livemap.config.DevParams
import org.jetbrains.letsPlot.livemap.config.LiveMapCanvasFigure
import org.jetbrains.letsPlot.livemap.core.Clipboard
import org.jetbrains.letsPlot.livemap.core.Projections.azimuthalEqualArea
import org.jetbrains.letsPlot.livemap.core.Projections.conicEqualArea
import org.jetbrains.letsPlot.livemap.core.Projections.geographic
import org.jetbrains.letsPlot.livemap.core.Projections.mercator
import org.jetbrains.letsPlot.livemap.mapengine.basemap.BasemapTileSystemProvider
import org.jetbrains.letsPlot.livemap.mapengine.basemap.Tilesets
import org.jetbrains.letsPlot.livemap.ui.CursorService
import kotlin.math.roundToInt

object LiveMapProviderUtil {

    fun injectLiveMapProvider(
        tiles: List<List<GeomLayer>>,
        spec: Map<String, Any>,
        cursorServiceConfig: CursorServiceConfig,
    ) {
        tiles.forEach { layers: List<GeomLayer> ->
            if (layers.any(GeomLayer::isLiveMap)) {
                require(layers.count(GeomLayer::isLiveMap) == 1) { "Only one LiveMap layer is allowed per plot." }
                require(layers.first().isLiveMap) { "LiveMap layer should be the first layer in a plot." }

                // LiveMap options
                val layerSpecs = spec.getMaps(Option.Plot.LAYERS)
                check(!layerSpecs.isNullOrEmpty()) { "Layer specs not found in the plot spec: $spec" }
                val liveMapOptions = layerSpecs.first()
                check(liveMapOptions[Option.Layer.GEOM] == Option.GeomName.LIVE_MAP) {
                    "LiveMap layer spec not found in the plot spec: $spec"
                }

                layers.first().setLiveMapProvider(
                    MyLiveMapProvider(
                        layers.map(::createLayerRendererData),
                        liveMapOptions,
                        cursorServiceConfig.cursorService
                    )
                )
            }
        }
    }

    private class MyLiveMapProvider(
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

            return liveMapBuilder.build()
                .let(Asyncs::constant)
                .let { liveMapAsync ->
                    LiveMapData(
                        LiveMapCanvasFigure(liveMapAsync).apply {
                            setBounds(
                                Rectangle(
                                    bounds.origin.x.roundToInt(),
                                    bounds.origin.y.roundToInt(),
                                    bounds.dimension.x.roundToInt(),
                                    bounds.dimension.y.roundToInt()
                                )
                            )
                        },
                        createTargetLocators(plotLayers, liveMapAsync)
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

            require(openBracketIndex <= closeBracketIndex) { "Error parsing subdomains: wrong brackets order" }

            val subdomains = url.substring(openBracketIndex + 1, closeBracketIndex)
            require(subdomains.isNotEmpty()) { "Empty subdomains list" }
            require(subdomains.all { it.lowercaseChar() in 'a'..'z' }) { "subdomain list contains non-letter symbols" }

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

    private fun createTargetLocators(plotLayers: List<LayerRendererData>, liveMapAsync: Async<LiveMap>): List<GeomTargetLocator> {
        class LiveMapInteractionAdapter {
            private var myLiveMap: LiveMap? = null
            private val adapters: List<GeomTargetLocatorAdapter> = plotLayers.mapIndexed(::GeomTargetLocatorAdapter)
            private var lastCoord: DoubleVector? = null
            private var lastResult: Map<Int, GeomTargetLocator.LookupResult> = emptyMap()

            val geomTargetLocators: List<GeomTargetLocator> = adapters

            init {
                liveMapAsync.map { myLiveMap = it }
            }

            // called n-times with same coord (where n - number of "layers").
            // Search only if coord changed, return cached result for the rest calls.
            private fun search(layerIndex: Int, coord: DoubleVector): GeomTargetLocator.LookupResult? {
                if (lastCoord != coord) {
                    lastResult = myLiveMap
                        ?.hoverObjects()
                        ?.groupBy(HoverObject::layerIndex)
                        ?.mapValues { (layerIndex, hoverObjects) ->
                            adapters[layerIndex].buildLookupResult(coord, hoverObjects)
                        } ?: emptyMap()
                }
                return lastResult[layerIndex]
            }

            inner class GeomTargetLocatorAdapter(
                private val layerIndex: Int,
                private val layer: LayerRendererData
            ) : GeomTargetLocator {
                override fun search(coord: DoubleVector) = search(layerIndex, coord)

                private val colorMarkerMapper = HintColorUtil.createColorMarkerMapper(
                    layer.geomKind,
                    isMappedFill = { p: DataPointAesthetics -> p.fillAes in layer.mappedAes },
                    isMappedColor = { p: DataPointAesthetics -> p.colorAes in layer.mappedAes }
                )

                fun buildLookupResult(coord: DoubleVector, hoverObjects: List<HoverObject>): GeomTargetLocator.LookupResult {
                    return GeomTargetLocator.LookupResult(
                        targets = hoverObjects.map { hoverObject ->
                            require(layerIndex == hoverObject.layerIndex)
                            GeomTarget(
                                hitIndex = hoverObject.index,
                                tipLayoutHint = TipLayoutHint.cursorTooltip(
                                    coord,
                                    markerColors = colorMarkerMapper(layer.aesthetics.dataPointAt(hoverObject.index))
                                ),
                                aesTipLayoutHints = emptyMap()
                            )
                        },
                        distance = 0.0, // livemap shows tooltip only on hover
                        geomKind = layer.geomKind,
                        contextualMapping = layer.contextualMapping,
                        isCrosshairEnabled = false // no crosshair on livemap
                    )
                }
            }
        }

        return LiveMapInteractionAdapter().geomTargetLocators
    }
}
