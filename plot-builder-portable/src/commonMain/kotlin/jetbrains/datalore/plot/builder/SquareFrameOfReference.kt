/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.plot.builder.assemble.GeomContextBuilder
import jetbrains.datalore.plot.builder.guide.AxisComponent
import jetbrains.datalore.plot.builder.layout.AxisLayoutInfo
import jetbrains.datalore.plot.builder.layout.TileLayoutInfo
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme.PanelGridTheme
import jetbrains.datalore.plot.builder.theme.PanelTheme
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.vis.svg.SvgRectElement

internal class SquareFrameOfReference(
    private val hScale: Scale<Double>,
    private val vScale: Scale<Double>,
    private val coord: CoordinateSystem,
    private val layoutInfo: TileLayoutInfo,
    private val theme: Theme,
    private val flipAxis: Boolean,
) : TileFrameOfReference {

    var isDebugDrawing: Boolean = false

    private val geomMapperX: (Double?) -> Double?
    private val geomMapperY: (Double?) -> Double?
    private val geomCoord: CoordinateSystem

    init {
        if (flipAxis) {
            // flip mappers to 'fool' geom.
            geomMapperX = vScale.mapper
            geomMapperY = hScale.mapper
            geomCoord = coord.flip()
        } else {
            geomMapperX = hScale.mapper
            geomMapperY = vScale.mapper
            geomCoord = coord
        }
    }

    // Rendering

    override fun drawBeforeGeomLayer(parent: SvgComponent) {
        drawPanelAndAxis(parent, beforeGeomLayer = true)
    }

    override fun drawAfterGeomLayer(parent: SvgComponent) {
        drawPanelAndAxis(parent, beforeGeomLayer = false)
    }

    private fun drawPanelAndAxis(parent: SvgComponent, beforeGeomLayer: Boolean) {
        val geomBounds: DoubleRectangle = layoutInfo.geomBounds
        val panelTheme = theme.panel()

        // Flip theme
        val hAxisTheme = theme.axisX(flipAxis)
        val vAxisTheme = theme.axisY(flipAxis)

        val hGridTheme = panelTheme.gridX(flipAxis)
        val vGridTheme = panelTheme.gridY(flipAxis)

        val drawPanel = panelTheme.showRect() && beforeGeomLayer
        val drawGridlines = beforeGeomLayer
        val drawHAxis = when {
            beforeGeomLayer -> !hAxisTheme.isOntop()
            else -> hAxisTheme.isOntop()
        }
        val drawVAxis = when {
            beforeGeomLayer -> !vAxisTheme.isOntop()
            else -> vAxisTheme.isOntop()
        }

        if (drawPanel) {
            val panel = buildPanelComponent(geomBounds, panelTheme)
            parent.add(panel)
        }

        if (drawHAxis || drawGridlines) {
            // X-axis
            val hAxis = buildAxis(
                hScale,
                layoutInfo.xAxisInfo!!,
                hideAxis = !drawHAxis,
                hideAxisBreaks = !layoutInfo.xAxisShown,
                hideGridlines = !drawGridlines,
                coord,
                hAxisTheme,
                hGridTheme,
                geomBounds.height,
                isDebugDrawing
            )
            hAxis.moveTo(DoubleVector(geomBounds.left, geomBounds.bottom))
            parent.add(hAxis)
        }


        if (drawVAxis || drawGridlines) {
            // Y-axis
            val vAxis = buildAxis(
                vScale,
                layoutInfo.yAxisInfo!!,
                hideAxis = !drawVAxis,
                hideAxisBreaks = !layoutInfo.yAxisShown,
                hideGridlines = !drawGridlines,
                coord,
                vAxisTheme,
                vGridTheme,
                geomBounds.width,
                isDebugDrawing
            )
            vAxis.moveTo(geomBounds.origin)
            parent.add(vAxis)
        }

        if (isDebugDrawing && !beforeGeomLayer) {
            drawDebugShapes(parent, geomBounds)
        }
    }

    private fun drawDebugShapes(parent: SvgComponent, geomBounds: DoubleRectangle) {
        run {
            val tileBounds = layoutInfo.bounds
            val rect = SvgRectElement(tileBounds)
            rect.fillColor().set(Color.BLACK)
            rect.strokeWidth().set(0.0)
            rect.fillOpacity().set(0.1)
            parent.add(rect)
        }

//        run {
//            val clipBounds = layoutInfo.clipBounds
//            val rect = SvgRectElement(clipBounds)
//            rect.fillColor().set(Color.DARK_GREEN)
//            rect.strokeWidth().set(0.0)
//            rect.fillOpacity().set(0.3)
//            parent.add(rect)
//        }

        run {
            val rect = SvgRectElement(geomBounds)
            rect.fillColor().set(Color.PINK)
            rect.strokeWidth().set(1.0)
            rect.fillOpacity().set(0.5)
            parent.add(rect)
        }
    }

    override fun buildGeomComponent(layer: GeomLayer, targetCollector: GeomTargetCollector): SvgComponent {
        val hAxisMapper = hScale.mapper
        val vAxisMapper = vScale.mapper

        val hAxisDomain = layoutInfo.xAxisInfo!!.axisDomain
        val vAxisDomain = layoutInfo.yAxisInfo!!.axisDomain
        val aesBounds = DoubleRectangle(
            xRange = ClosedRange(
                hAxisMapper(hAxisDomain.lowerEnd) as Double,
                hAxisMapper(hAxisDomain.upperEnd) as Double
            ),
            yRange = ClosedRange(
                vAxisMapper(vAxisDomain.lowerEnd) as Double,
                vAxisMapper(vAxisDomain.upperEnd) as Double
            )
        )

        return buildGeom(
            layer,
            geomMapperX, geomMapperY,
            xyAesBounds = aesBounds,
            geomCoord,
            flipAxis,
            targetCollector
        )
    }

    override fun applyClientLimits(clientBounds: DoubleRectangle): DoubleRectangle {
        return geomCoord.applyClientLimits(clientBounds)
    }


    companion object {
        private fun buildAxis(
            scale: Scale<Double>,
            info: AxisLayoutInfo,
            hideAxis: Boolean,
            hideAxisBreaks: Boolean,
            hideGridlines: Boolean,
            coord: CoordinateSystem,
            axisTheme: AxisTheme,
            gridTheme: PanelGridTheme,
            gridLineLength: Double,
            isDebugDrawing: Boolean
        ): AxisComponent {
            check(!(hideAxis && hideGridlines)) { "Trying to build an empty axis componenmt" }
            val orientation = info.orientation
            val labelAdjustments = AxisComponent.TickLabelAdjustments(
                orientation = orientation,
                horizontalAnchor = info.tickLabelHorizontalAnchor,
                verticalAnchor = info.tickLabelVerticalAnchor,
                rotationDegree = info.tickLabelRotationAngle,
                additionalOffsets = info.tickLabelAdditionalOffsets
            )

            val axis = AxisComponent(
                length = info.axisLength,
                orientation = orientation,
                breaksData = AxisUtil.breaksData(scale, coord, orientation.isHorizontal),
                labelAdjustments = labelAdjustments,
                gridLineLength = gridLineLength,
                axisTheme = axisTheme,
                gridTheme = gridTheme,
                useSmallFont = info.tickLabelSmallFont,
                hideAxis = hideAxis,
                hideAxisBreaks = hideAxisBreaks,
                hideGridlines = hideGridlines
            )

            if (isDebugDrawing) {
                if (info.tickLabelsBounds != null) {
                    val rect = SvgRectElement(info.tickLabelsBounds)
                    rect.strokeColor().set(Color.GREEN)
                    rect.strokeWidth().set(1.0)
                    rect.fillOpacity().set(0.0)
                    axis.add(rect)
                }
            }
            return axis
        }

        private fun buildPanelComponent(bounds: DoubleRectangle, theme: PanelTheme): SvgRectElement {
            return SvgRectElement(bounds).apply {
                strokeColor().set(theme.rectColor())
                strokeWidth().set(theme.rectStrokeWidth())
                fillColor().set(theme.rectFill())
            }
        }

        private fun buildGeom(
            layer: GeomLayer,
            xAesMapper: (Double?) -> Double?,
            yAesMapper: (Double?) -> Double?,
            xyAesBounds: DoubleRectangle,
            coord: CoordinateSystem,
            flippedAxis: Boolean,
            targetCollector: GeomTargetCollector
        ): SvgComponent {
            val rendererData = LayerRendererUtil.createLayerRendererData(
                layer,
                xAesMapper, yAesMapper
            )

            val aestheticMappers = rendererData.aestheticMappers
            val aesthetics = rendererData.aesthetics

            val ctx = GeomContextBuilder()
                .flipped(flippedAxis)
                .aesthetics(aesthetics)
                .aestheticMappers(aestheticMappers)
                .aesBounds(xyAesBounds)
                .geomTargetCollector(
                    if (flippedAxis) {
                        targetCollector.flip()
                    } else {
                        targetCollector
                    }
                )
                .build()

            val pos = rendererData.pos
            val geom = layer.geom

            return SvgLayerRenderer(aesthetics, geom, pos, coord, ctx)
        }
    }
}