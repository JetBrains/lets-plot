/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.plot.base.geom.LiveMapProvider
import jetbrains.datalore.plot.base.geom.LiveMapProvider.LiveMapData
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.livemap.LiveMapOptions
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.LayerRendererUtil
import jetbrains.livemap.LiveMapLocation
import jetbrains.livemap.api.*
import jetbrains.livemap.config.DevParams
import jetbrains.livemap.config.LiveMapCanvasFigure
import jetbrains.livemap.config.LiveMapFactory
import jetbrains.livemap.ui.Clipboard
import jetbrains.livemap.ui.CursorService

object LiveMapUtil {

    fun injectLiveMapProvider(plotTiles: List<List<GeomLayer>>, liveMapOptions: LiveMapOptions, cursorServiceConfig: CursorServiceConfig) {

        plotTiles.forEach { tileLayers ->
            if (tileLayers.any(GeomLayer::isLiveMap)) {
                require(tileLayers.count(GeomLayer::isLiveMap) == 1)
                require(tileLayers.first().isLiveMap)
                tileLayers.first().setLiveMapProvider(MyLiveMapProvider(tileLayers, liveMapOptions, cursorServiceConfig.cursorService))
            }
        }
    }

    internal fun createLayersConfigurator(
        layerKind: MapLayerKind,
        entityBuilders: List<MapEntityBuilder>
    ): LayersBuilder.() -> Unit = {
        when (layerKind) {
            MapLayerKind.POINT -> points {
                entityBuilders.forEach { it.toPointBuilder()?.run(::point) }
            }
            MapLayerKind.POLYGON -> polygons {
                entityBuilders.forEach { polygon(it.createPolygonConfigurator()) }
            }
            MapLayerKind.PATH -> paths {
                entityBuilders.forEach { it.toPathBuilder()?.let(::path) }
            }

            MapLayerKind.V_LINE -> vLines {
                entityBuilders.forEach { it.toLineBuilder()?.let(::line) }
            }

            MapLayerKind.H_LINE -> hLines {
                entityBuilders.forEach { it.toLineBuilder()?.let(::line) }
            }

            MapLayerKind.TEXT -> texts {
                entityBuilders.forEach { it.toTextBuilder()?.let(::text) }
            }

            MapLayerKind.PIE -> pies {
                entityBuilders.forEach { it.toChartBuilder()?.let(::pie) }
            }

            MapLayerKind.BAR -> bars {
                entityBuilders.forEach { it.toChartBuilder()?.let(::bar) }
            }

            else -> error("Unsupported layer kind: $layerKind")
        }
    }

    private class MyLiveMapProvider internal constructor(
        geomLayers: List<GeomLayer>,
        private val myLiveMapOptions: LiveMapOptions,
        cursorService: CursorService
    ) : LiveMapProvider {

        private val liveMapSpecBuilder: LiveMapSpecBuilder
        private val myTargetSource = HashMap<Pair<Int, Int>, ContextualMapping>()

        init {
            require(geomLayers.isNotEmpty())
            require(geomLayers.first().isLiveMap) { "geom_livemap have to be the very first geom after ggplot()" }

            // liveMap uses raw positions, so no mappings needed
            val newLiveMapRendererData = { layer: GeomLayer ->
                LayerRendererUtil.createLayerRendererData(
                    layer = layer,
                    sharedNumericMappers = emptyMap(),
                    overallNumericDomains = emptyMap()
                )
            }

            geomLayers
                .map(newLiveMapRendererData)
                .forEachIndexed {layerIndex, rendererData ->
                    rendererData.aesthetics.dataPoints().forEach {
                        myTargetSource[layerIndex to it.index()] = rendererData.contextualMapping
                    }
            }

            // feature geom layers
            val layers = geomLayers
                .drop(1) // skip geom_livemap
                .map(newLiveMapRendererData)
                .map {
                    with(it) {
                        LiveMapLayerData(
                            geom,
                            geomKind,
                            aesthetics
                        )
                    }
                }

            // LiveMap geom layer
            newLiveMapRendererData(geomLayers.first()).let {
                liveMapSpecBuilder = LiveMapSpecBuilder()
                    .liveMapOptions(myLiveMapOptions)
                    .aesthetics(it.aesthetics)
                    .dataAccess(it.dataAccess)
                    .layers(layers)
                    .devParams(DevParams(myLiveMapOptions.devParams))
                    .mapLocationConsumer { locationRect ->
                        Clipboard.copy(LiveMapLocation.getLocationString(locationRect))
                    }
                    .cursorService(cursorService)
            }
        }

        override fun createLiveMap(bounds: DoubleRectangle): LiveMapData {
            return liveMapSpecBuilder.size(bounds.dimension).build()
                .let { liveMapSpec -> LiveMapFactory(liveMapSpec).createLiveMap() }
                .let { liveMapAsync ->
                    LiveMapData(
                        LiveMapCanvasFigure(liveMapAsync)
                            .apply {
                                setBounds(Rectangle(
                                    bounds.origin.x.toInt(),
                                    bounds.origin.y.toInt(),
                                    bounds.dimension.x.toInt(),
                                    bounds.dimension.y.toInt()
                                ))
                            },
                        LiveMapTargetLocator(liveMapAsync, myTargetSource)
                    )
                }
        }
    }
}
