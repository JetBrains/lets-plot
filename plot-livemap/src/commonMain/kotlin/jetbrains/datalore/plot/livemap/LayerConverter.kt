/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.GeomKind.*
import jetbrains.datalore.plot.base.aes.AestheticsUtil
import jetbrains.datalore.plot.base.geom.LiveMapGeom
import jetbrains.datalore.plot.base.livemap.LivemapConstants.DisplayMode
import jetbrains.datalore.plot.builder.LayerRendererUtil.LayerRendererData
import jetbrains.livemap.api.*


object LayerConverter {
    fun convert(
        letsPlotLayers: List<LayerRendererData>,
        aesScalingLimit: Int,
        constScalingLimit: Int,
        geodesic: Boolean
    ): List<LayersBuilder.() -> Unit> {
        return letsPlotLayers
            .mapIndexed { index, layer ->
            val dataPointsConverter = DataPointsConverter(
                layerIndex = index,
                aesthetics = layer.aesthetics,
                geodesic = geodesic
            )

            val (layerKind, dataPointLiveMapAesthetics) = when (layer.geomKind) {
                POINT -> MapLayerKind.POINT to dataPointsConverter.toPoint(layer.geom)
                H_LINE -> MapLayerKind.H_LINE to dataPointsConverter.toHorizontalLine()
                V_LINE -> MapLayerKind.V_LINE to dataPointsConverter.toVerticalLine()
                SEGMENT -> MapLayerKind.PATH to dataPointsConverter.toSegment(layer.geom)
                RECT -> MapLayerKind.POLYGON to dataPointsConverter.toRect()
                TILE, BIN_2D -> MapLayerKind.POLYGON to dataPointsConverter.toTile()
                DENSITY2D, CONTOUR, PATH -> MapLayerKind.PATH to dataPointsConverter.toPath(layer.geom)
                TEXT, LABEL -> MapLayerKind.TEXT to dataPointsConverter.toText(layer.geom)
                DENSITY2DF, CONTOURF, POLYGON, MAP -> MapLayerKind.POLYGON to dataPointsConverter.toPolygon()
                LIVE_MAP -> when ((layer.geom as LiveMapGeom).displayMode) {
                    DisplayMode.POINT -> MapLayerKind.POINT to dataPointsConverter.toPoint(layer.geom)
                    DisplayMode.PIE -> MapLayerKind.PIE to dataPointsConverter.toPie()
                    DisplayMode.BAR -> MapLayerKind.BAR to dataPointsConverter.toBar()
                    else -> error("Unexpected livemap display mode.")
                }
                else -> throw IllegalArgumentException("Layer '" + layer.geomKind.name + "' is not supported on Live Map.")
            }

            val positiveScalingLimit = when (Aes.SIZE in layer.mappedAes) {
                true -> aesScalingLimit
                false -> constScalingLimit
            }

            val sizeScalingRange = when (positiveScalingLimit) {
                -1 -> -2..Int.MAX_VALUE
                else -> -2..positiveScalingLimit
            }

            createLayerBuilder(
                index,
                layerKind,
                layer.geomKind,
                dataPointLiveMapAesthetics,
                sizeScalingRange,
                alphaScalingEnabled = sizeScalingRange.last != 0
            )
        }
    }

    private fun createLayerBuilder(
        layerIdx: Int,
        layerKind: MapLayerKind,
        plotLayerKind: GeomKind,
        liveMapDataPoints: List<DataPointLiveMapAesthetics>,
        sizeScalingRange: IntRange?,
        alphaScalingEnabled: Boolean,
    ): LayersBuilder.() -> Unit = {
        when (layerKind) {
            MapLayerKind.POINT -> points {
                liveMapDataPoints.forEach {
                    point {
                        this.sizeScalingRange = sizeScalingRange
                        this.alphaScalingEnabled = alphaScalingEnabled
                        layerIndex = layerIdx
                        index = it.index
                        point = it.point
                        label = it.label
                        animation = it.animation
                        shape = it.shape
                        radius = it.radius
                        fillColor = it.fillColor
                        strokeColor = it.strokeColor
                        strokeWidth = 1.0
                    }
                }
            }

            MapLayerKind.POLYGON -> polygons {
                liveMapDataPoints.forEach {
                    polygon {
                        this.sizeScalingRange = sizeScalingRange
                        this.alphaScalingEnabled = alphaScalingEnabled
                        layerIndex = layerIdx
                        index = it.index
                        multiPolygon = it.geometry
                        geoObject = it.geoObject
                        lineDash = it.lineDash
                        fillColor = it.fillColor
                        strokeColor = it.strokeColor
                        strokeWidth = AestheticsUtil.strokeWidth(it.myP)
                    }
                }
            }

            MapLayerKind.PATH -> paths {
                liveMapDataPoints.forEach {
                    if (it.geometry != null) {
                        path {
                            this.sizeScalingRange = sizeScalingRange
                            this.alphaScalingEnabled = alphaScalingEnabled
                            layerIndex = layerIdx
                            index = it.index
                            multiPolygon = it.geometry!!
                            lineDash = it.lineDash
                            strokeColor = it.strokeColor
                            strokeWidth = AestheticsUtil.strokeWidth(it.myP)
                            animation = it.animation
                            speed = it.speed
                            flow = it.flow
                            arrowAngle = it.arrowAngle
                            arrowLength = it.arrowLength
                            arrowAtEnds = it.arrowAtEnds
                            arrowType = it.arrowType
                        }
                    }
                }
            }

            MapLayerKind.V_LINE -> vLines {
                liveMapDataPoints.forEach {
                    line {
                        this.sizeScalingRange = sizeScalingRange
                        this.alphaScalingEnabled = alphaScalingEnabled
                        point = it.point
                        lineDash = it.lineDash
                        strokeColor = it.strokeColor
                        strokeWidth = AestheticsUtil.strokeWidth(it.myP)
                    }
                }
            }

            MapLayerKind.H_LINE -> hLines {
                liveMapDataPoints.forEach {
                    line {
                        this.sizeScalingRange = sizeScalingRange
                        this.alphaScalingEnabled = alphaScalingEnabled
                        point = it.point
                        lineDash = it.lineDash
                        strokeColor = it.strokeColor
                        strokeWidth = AestheticsUtil.strokeWidth(it.myP)
                    }
                }
            }

            MapLayerKind.TEXT -> texts {
                liveMapDataPoints.forEach {
                    text {
                        index = it.index
                        point = it.point
                        fillColor = if (plotLayerKind == LABEL) it.fillColor else Color.TRANSPARENT
                        strokeColor = it.strokeColor
                        strokeWidth = 0.0
                        label = it.label
                        size = it.size
                        family = it.family
                        fontface = it.fontface
                        hjust = it.hjust
                        vjust = it.vjust
                        angle = it.angle
                        drawBorder = plotLayerKind == LABEL
                        labelPadding = it.labelPadding
                        labelRadius = it.labelRadius
                        labelSize = it.labelSize
                    }
                }
            }

            MapLayerKind.PIE -> pies {
                liveMapDataPoints.forEach {
                    pie {
                        this.sizeScalingRange = sizeScalingRange
                        this.alphaScalingEnabled = alphaScalingEnabled
                        layerIndex = layerIdx
                        fromDataPoint(it)
                    }
                }
            }

            MapLayerKind.BAR -> bars {
                liveMapDataPoints.forEach {
                    bar {
                        this.sizeScalingRange = sizeScalingRange
                        this.alphaScalingEnabled = alphaScalingEnabled
                        layerIndex = layerIdx
                        fromDataPoint(it)
                    }
                }
            }
        }
    }

    private fun Symbol.fromDataPoint(p: DataPointLiveMapAesthetics) {
        point = p.point
        radius = p.radius
        strokeColor = p.strokeColor
        strokeWidth = 1.0
        indices = p.indices
        values = p.valueArray
        colors = p.colorArray
    }
}
