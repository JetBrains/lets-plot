/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.geom.LiveMapProvider
import jetbrains.datalore.plot.base.geom.LiveMapProvider.LiveMapData
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.livemap.LiveMapOptions
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.LayerRendererUtil
import jetbrains.livemap.LiveMapLocation
import jetbrains.livemap.api.*
import jetbrains.livemap.config.DevParams
import jetbrains.livemap.config.LiveMapCanvasFigure
import jetbrains.livemap.config.LiveMapFactory
import jetbrains.livemap.core.Clipboard
import jetbrains.livemap.ui.CursorService

object LiveMapUtil {

    fun injectLiveMapProvider(
        plotTiles: List<List<GeomLayer>>,
        liveMapOptions: LiveMapOptions,
        cursorServiceConfig: CursorServiceConfig,
    ) {
        plotTiles.forEach { tileLayers ->
            if (tileLayers.any(GeomLayer::isLiveMap)) {
                require(tileLayers.count(GeomLayer::isLiveMap) == 1)
                require(tileLayers.first().isLiveMap)
                tileLayers.first().setLiveMapProvider(
                    MyLiveMapProvider(
                        tileLayers,
                        liveMapOptions,
                        cursorServiceConfig.cursorService
                    )
                )
            }
        }
    }

    internal fun createLayerBuilder(
        layerKind: MapLayerKind,
        liveMapDataPoints: List<DataPointLiveMapAesthetics>,
        mappedAes: Set<Aes<*>>,
    ): LayersBuilder.() -> Unit = {
        fun getScaleRange(scalableStroke: Boolean): ClosedRange<Int>? {
            val dimensionAes = Aes.SIZE
            return when {
                scalableStroke -> when {
                    dimensionAes in mappedAes -> -1..0
                    dimensionAes !in mappedAes -> -1..1
                    else -> null
                }
                else -> when {
                    dimensionAes in mappedAes -> -2..0
                    dimensionAes !in mappedAes -> -2..2
                    else -> null
                }
            }
        }

        when (layerKind) {
            MapLayerKind.POINT -> points {
                liveMapDataPoints.forEach {
                    point {
                        scaleRange = getScaleRange(scalableStroke = false)
                        layerIndex = it.layerIndex
                        index = it.index
                        point = it.point
                        label = it.label
                        animation = it.animation
                        shape = it.shape
                        radius = it.radius
                        fillColor = it.fillColor
                        strokeColor = it.strokeColor
                        strokeWidth = it.strokeWidth
                    }
                }
            }
            MapLayerKind.POLYGON -> polygons {
                liveMapDataPoints.forEach {
                    polygon {
                        scaleRange = getScaleRange(scalableStroke = true)
                        layerIndex = it.layerIndex
                        index = it.index
                        multiPolygon = it.geometry
                        geoObject = it.geoObject
                        lineDash = it.lineDash
                        fillColor = it.fillColor
                        strokeColor = it.strokeColor
                        strokeWidth = it.strokeWidth
                    }
                }
            }
            MapLayerKind.PATH -> paths {
                liveMapDataPoints.forEach {
                    if (it.geometry != null) {
                        path {
                            scaleRange = getScaleRange(scalableStroke = true)
                            layerIndex = it.layerIndex
                            index = it.index
                            multiPolygon = it.geometry!!
                            lineDash = it.lineDash
                            strokeColor = it.strokeColor
                            strokeWidth = it.strokeWidth
                            animation = it.animation
                            speed = it.speed
                            flow = it.flow
                        }
                    }
                }
            }

            MapLayerKind.V_LINE -> vLines {
                liveMapDataPoints.forEach {
                    line {
                        scaleRange = getScaleRange(scalableStroke = true)
                        point = it.point
                        lineDash = it.lineDash
                        strokeColor = it.strokeColor
                        strokeWidth = it.strokeWidth
                    }
                }
            }

            MapLayerKind.H_LINE -> hLines {
                liveMapDataPoints.forEach {
                    line {
                        scaleRange = getScaleRange(scalableStroke = true)
                        point = it.point
                        lineDash = it.lineDash
                        strokeColor = it.strokeColor
                        strokeWidth = it.strokeWidth
                    }
                }
            }

            MapLayerKind.TEXT -> texts {
                liveMapDataPoints.forEach {
                    text {
                        index = it.index
                        point = it.point
                        fillColor = it.strokeColor // Text is filled by strokeColor
                        strokeColor = it.strokeColor
                        strokeWidth = 0.0
                        label = it.label
                        size = it.size
                        family = it.family
                        fontface = it.fontface
                        hjust = it.hjust
                        vjust = it.vjust
                        angle = it.angle
                    }
                }
            }

            MapLayerKind.PIE -> pies {
                liveMapDataPoints.forEach {
                    pie {
                        scaleRange = getScaleRange(scalableStroke = false)
                        fromDataPoint(it)
                    }
                }
            }

            MapLayerKind.BAR -> bars {
                liveMapDataPoints.forEach {
                    bar {
                        scaleRange = getScaleRange(scalableStroke = false)
                        fromDataPoint(it)
                    }
                }
            }

            else -> error("Unsupported layer kind: $layerKind")
        }
    }

    private fun Symbol.fromDataPoint(p: DataPointLiveMapAesthetics) {
        layerIndex = p.layerIndex
        point = p.point
        radius = p.radius
        strokeColor = p.strokeColor
        strokeWidth = 1.0
        indices = p.indices
        values = p.valueArray
        colors = p.colorArray
    }


    private class MyLiveMapProvider internal constructor(
        geomLayers: List<GeomLayer>,
        private val myLiveMapOptions: LiveMapOptions,
        cursorService: CursorService,
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
                    Mappers.IDENTITY,   // Not used with "livemap".
                    Mappers.IDENTITY,
                )
            }

            geomLayers
                .map(newLiveMapRendererData)
                .forEachIndexed { layerIndex, rendererData ->
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
                            aesthetics,
                            mappedAes
                        )
                    }
                }

            // LiveMap geom layer
            newLiveMapRendererData(geomLayers.first()).let {
                liveMapSpecBuilder = LiveMapSpecBuilder()
                    .liveMapOptions(myLiveMapOptions)
                    .aesthetics(it.aesthetics)
                    .mappedAes(it.mappedAes)
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
                                setBounds(
                                    Rectangle(
                                        bounds.origin.x.toInt(),
                                        bounds.origin.y.toInt(),
                                        bounds.dimension.x.toInt(),
                                        bounds.dimension.y.toInt()
                                    )
                                )
                            },
                        LiveMapTargetLocator(liveMapAsync, myTargetSource)
                    )
                }
        }
    }
}
