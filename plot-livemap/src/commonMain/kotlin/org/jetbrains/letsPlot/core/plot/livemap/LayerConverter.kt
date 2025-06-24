/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.livemap

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.createMultiPolygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.plus
import org.jetbrains.letsPlot.commons.intern.typedGeometry.toVec
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.FontFace
import org.jetbrains.letsPlot.core.canvas.FontStyle
import org.jetbrains.letsPlot.core.canvas.FontWeight
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.GeomKind.*
import org.jetbrains.letsPlot.core.plot.base.PositionAdjustment
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsUtil
import org.jetbrains.letsPlot.core.plot.base.geom.*
import org.jetbrains.letsPlot.core.plot.base.pos.NudgePos
import org.jetbrains.letsPlot.core.plot.builder.LayerRendererUtil.LayerRendererData
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.api.*


object LayerConverter {
    fun convert(
        letsPlotLayers: List<LayerRendererData>,
        aesScalingLimit: Int,
        constScalingLimit: Int
    ): List<FeatureLayerBuilder.() -> Unit> {
        return letsPlotLayers
            .mapIndexed { index, layer ->
            val dataPointsConverter = DataPointsConverter(
                layerIndex = index,
                aesthetics = layer.aesthetics
            )

            val (layerKind, dataPointLiveMapAesthetics) = when (layer.geomKind) {
                POINT -> MapLayerKind.POINT to dataPointsConverter.toPoint(layer.geom as PointGeom)
                H_LINE -> MapLayerKind.H_LINE to dataPointsConverter.toHorizontalLine()
                V_LINE -> MapLayerKind.V_LINE to dataPointsConverter.toVerticalLine()
                SEGMENT -> MapLayerKind.PATH to dataPointsConverter.toSegment(layer.geom as SegmentGeom)
                CURVE -> MapLayerKind.PATH to dataPointsConverter.toCurve(layer.geom as CurveGeom)
                RECT -> MapLayerKind.POLYGON to dataPointsConverter.toRect()
                TILE, BIN_2D -> MapLayerKind.POLYGON to dataPointsConverter.toTile()
                SPOKE -> MapLayerKind.PATH to dataPointsConverter.toSpoke(layer.geom as SpokeGeom)
                DENSITY2D, CONTOUR, PATH -> MapLayerKind.PATH to dataPointsConverter.toPath(layer.geom)
                TEXT, LABEL, TEXT_REPEL, LABEL_REPEL -> MapLayerKind.TEXT to dataPointsConverter.toText(layer.geom)
                DENSITY2DF, CONTOURF, POLYGON, MAP -> MapLayerKind.POLYGON to dataPointsConverter.toPolygon()
                PIE -> MapLayerKind.PIE to dataPointsConverter.toPie(layer.geom as PieGeom)
                HEX -> MapLayerKind.POLYGON to dataPointsConverter.toHex()
                else -> throw IllegalArgumentException("Layer '" + layer.geomKind.name + "' is not supported on Live Map.")
            }

            val positiveScalingLimit = when (
                listOf(Aes.SIZE, Aes.SIZE_START, Aes.SIZE_END, Aes.POINT_SIZE, Aes.POINT_STROKE, Aes.SEGMENT_SIZE).any { it in layer.mappedAes }
            ) {
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
                layer.pos,
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
        layerPositionAdjustment: PositionAdjustment,
        liveMapDataPoints: List<DataPointLiveMapAesthetics>,
        sizeScalingRange: IntRange?,
        alphaScalingEnabled: Boolean,
    ): FeatureLayerBuilder.() -> Unit = {
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
                        angle = it.angle
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
                        this.sizeScalingRange = sizeScalingRange
                        this.alphaScalingEnabled = alphaScalingEnabled
                        layerIndex = layerIdx
                        index = it.index
                        geometry = createMultiPolygon(it.geometry!!)
                        geoObject = it.geoObject
                        lineDash = it.lineDash
                        lineDashOffset = it.lineDashOffset
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
                            points = it.geometry!!
                            flat = it.flat
                            geodesic = it.geodesic
                            lineDash = it.lineDash
                            lineDashOffset = it.lineDashOffset
                            strokeColor = it.strokeColor
                            strokeWidth = AestheticsUtil.strokeWidth(it.myP)
                            animation = it.animation
                            speed = it.speed
                            flow = it.flow
                            arrowSpec = it.arrowSpec
                            isCurve = it.isCurve
                            sizeStart = it.sizeStart
                            sizeEnd = it.sizeEnd
                            strokeStart = it.strokeStart
                            strokeEnd = it.strokeEnd
                            spacer = it.spacer
                        }
                    }
                }
            }

            MapLayerKind.V_LINE -> vLines {
                liveMapDataPoints.forEach {
                    line {
                        this.sizeScalingRange = sizeScalingRange
                        this.alphaScalingEnabled = alphaScalingEnabled
                        layerIndex = layerIdx
                        index = it.index
                        point = it.point
                        lineDash = it.lineDash
                        lineDashOffset = it.lineDashOffset
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
                        layerIndex = layerIdx
                        index = it.index
                        point = it.point
                        lineDash = it.lineDash
                        lineDashOffset = it.lineDashOffset
                        strokeColor = it.strokeColor
                        strokeWidth = AestheticsUtil.strokeWidth(it.myP)
                    }
                }
            }

            MapLayerKind.TEXT -> texts {
                liveMapDataPoints.forEach {
                    text {
                        this.sizeScalingRange = sizeScalingRange
                        this.alphaScalingEnabled = alphaScalingEnabled
                        index = it.index
                        point = nudgePoint(layerPositionAdjustment, it.point)
                        fillColor = if (plotLayerKind == LABEL) it.fillColor else Color.TRANSPARENT
                        strokeColor = if (plotLayerKind == LABEL && !it.alphaStroke) {
                            it.myP.color()!!
                        } else {
                            it.strokeColor
                        }
                        strokeWidth = 0.0
                        label = it.label
                        size = it.size
                        family = it.family
                        hjust = it.hjust
                        vjust = it.vjust
                        angle = it.angle
                        drawBorder = plotLayerKind == LABEL
                        labelPadding = it.labelPadding
                        labelRadius = it.labelRadius
                        labelSize = it.labelSize
                        lineheight = it.lineheight

                        nudgeClient = nudgeClient(layerPositionAdjustment)
                        enableNudgeScaling = nudgeScaling(layerPositionAdjustment)

                        val fontFace = FontFace.fromString(it.fontface)
                        fontStyle = FontStyle.ITALIC.takeIf { fontFace.italic } ?: FontStyle.NORMAL
                        fontWeight = FontWeight.BOLD.takeIf { fontFace.bold } ?: FontWeight.NORMAL
                    }
                }
            }

            MapLayerKind.PIE -> pies {
                liveMapDataPoints.forEach {
                    pie {
                        this.sizeScalingRange = sizeScalingRange
                        this.alphaScalingEnabled = alphaScalingEnabled
                        layerIndex = layerIdx
                        point = it.point
                        radius = it.radius
                        indices = it.indices
                        values = it.valueArray
                        fillColors = it.fillArray
                        explodes = it.explodeArray
                        strokeColors = it.colorArray
                        strokeWidths = it.strokeArray
                        strokeSide = it.strokeSide
                        holeSize = it.holeRatio
                        spacerColor = it.spacerColor
                        spacerWidth = it.spacerWidth
                        startAngle = it.startAngle
                        clockwise = it.clockwise
                    }
                }
            }
        }
    }

    private fun nudgePoint(position: PositionAdjustment, point: Vec<LonLat>): Vec<LonLat> {
       if (position is NudgePos && position.unit == DimensionUnit.IDENTITY) {
            return point.plus(position.adjustedDimension.toVec())
        }
        return point
    }

    private fun nudgeClient(position: PositionAdjustment): Vec<Client> {
        if (position is NudgePos && (position.unit == DimensionUnit.SIZE || position.unit == DimensionUnit.PIXEL)) {
            return position.adjustedDimension.toVec()
        }
        return Vec(0.0, 0.0)
    }

    private fun nudgeScaling(position: PositionAdjustment): Boolean {
        return position is NudgePos && position.unit == DimensionUnit.SIZE
    }
}
