/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.frame

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.base.theme.PanelTheme
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.builder.*
import org.jetbrains.letsPlot.core.plot.builder.assemble.GeomContextBuilder
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotAssemblerPlotContext
import org.jetbrains.letsPlot.core.plot.builder.guide.AxisComponent
import org.jetbrains.letsPlot.core.plot.builder.guide.AxisComponent.BreaksData
import org.jetbrains.letsPlot.core.plot.builder.guide.AxisComponent.TickLabelAdjustments
import org.jetbrains.letsPlot.core.plot.builder.guide.GridComponent
import org.jetbrains.letsPlot.core.plot.builder.layout.AxisLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.GeomMarginsLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayoutInfo
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement

internal class SquareFrameOfReference(
    private val hScaleBreaks: ScaleBreaks,
    private val vScaleBreaks: ScaleBreaks,
    private val adjustedDomain: DoubleRectangle,
    private val coord: CoordinateSystem,
    private val layoutInfo: TileLayoutInfo,
    private val marginsLayout: GeomMarginsLayout,
    private val theme: Theme,
    private val flipAxis: Boolean,
) : FrameOfReference {

    var isDebugDrawing: Boolean = false

    // Rendering

    override fun drawBeforeGeomLayer(parent: SvgComponent) {
        drawPanelAndAxis(parent, beforeGeomLayer = true)
    }

    override fun drawAfterGeomLayer(parent: SvgComponent) {
        drawPanelAndAxis(parent, beforeGeomLayer = false)
    }

    private fun drawPanelAndAxis(parent: SvgComponent, beforeGeomLayer: Boolean) {
        val geomBounds: DoubleRectangle = layoutInfo.geomInnerBounds
        val panelTheme = theme.panel()

        // Flip theme
        val hAxisTheme = theme.horizontalAxis(flipAxis)
        val vAxisTheme = theme.verticalAxis(flipAxis)

        val hGridTheme = panelTheme.gridX(flipAxis)
        val vGridTheme = panelTheme.gridY(flipAxis)

        val drawPanel = panelTheme.showRect() && beforeGeomLayer
        val drawPanelBorder = panelTheme.showBorder() && !beforeGeomLayer

        @Suppress("UnnecessaryVariable")
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
            // Top/Bottom axis
            listOfNotNull(layoutInfo.axisInfos.top, layoutInfo.axisInfos.bottom).forEach { axisInfo ->
                val (labelAdjustments, breaksData) = prepareAxisData(axisInfo, hScaleBreaks, hAxisTheme)

                if (drawGridlines) {
                    val gridComponent = GridComponent(breaksData.majorGrid, breaksData.minorGrid, hGridTheme)
                    val gridBounds = geomBounds.origin
                    gridComponent.moveTo(gridBounds)
                    parent.add(gridComponent)
                }

                val axisComponent = buildAxis(
                    breaksData = breaksData,
                    info = axisInfo,
                    hideAxis = !drawHAxis,
                    hideAxisBreaks = !layoutInfo.hAxisShown,
                    axisTheme = hAxisTheme,
                    labelAdjustments = labelAdjustments,
                    isDebugDrawing,
                )

                val axisOrigin = marginsLayout.toAxisOrigin(geomBounds, axisInfo.orientation)
                axisComponent.moveTo(axisOrigin)
                parent.add(axisComponent)
            }
        }


        if (drawVAxis || drawGridlines) {
            // Left/Right axis
            listOfNotNull(layoutInfo.axisInfos.left, layoutInfo.axisInfos.right).forEach { axisInfo ->
                val (labelAdjustments, breaksData) = prepareAxisData(axisInfo, vScaleBreaks, vAxisTheme)

                if (drawGridlines) {
                    val gridComponent = GridComponent(breaksData.majorGrid, breaksData.minorGrid, vGridTheme)
                    val gridBounds = geomBounds.origin
                    gridComponent.moveTo(gridBounds)
                    parent.add(gridComponent)
                }

                val axisComponent = buildAxis(
                    breaksData = breaksData,
                    axisInfo,
                    hideAxis = !drawVAxis,
                    hideAxisBreaks = !layoutInfo.vAxisShown,
                    vAxisTheme,
                    labelAdjustments,
                    isDebugDrawing,
                )

                val axisOrigin = marginsLayout.toAxisOrigin(geomBounds, axisInfo.orientation)
                axisComponent.moveTo(axisOrigin)
                parent.add(axisComponent)
            }
        }

        if (drawPanelBorder) {
            val panelBorder = buildPanelBorderComponent(geomBounds, panelTheme)
            parent.add(panelBorder)
        }

        if (isDebugDrawing && !beforeGeomLayer) {
            drawDebugShapes(parent, geomBounds)
        }
    }

    private fun prepareAxisData(axisInfo: AxisLayoutInfo, scaleBreaks: ScaleBreaks, axisTheme: AxisTheme): Pair<TickLabelAdjustments, BreaksData> {
        val labelAdjustments = TickLabelAdjustments(
            orientation = axisInfo.orientation,
            horizontalAnchor = axisInfo.tickLabelHorizontalAnchor,
            verticalAnchor = axisInfo.tickLabelVerticalAnchor,
            rotationDegree = axisInfo.tickLabelRotationAngle,
            additionalOffsets = axisInfo.tickLabelAdditionalOffsets
        )

        val breaksData = AxisUtil.breaksData(
            scaleBreaks = scaleBreaks,
            coord = coord,
            domain = adjustedDomain,
            flipAxis = flipAxis,
            orientation = axisInfo.orientation,
            axisTheme = axisTheme,
            labelAdjustments = labelAdjustments
        )
        return Pair(labelAdjustments, breaksData)
    }

    private fun drawDebugShapes(parent: SvgComponent, geomBounds: DoubleRectangle) {
        run {
            val tileBounds = layoutInfo.geomWithAxisBounds
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
        val layerComponent = buildGeom(
            layer,
            xyAesBounds = adjustedDomain,  // positional aesthetics are the same as positional data.
            coord,
            flipAxis,
            targetCollector,
            backgroundColor = if (theme.panel().showRect()) theme.panel().rectFill() else theme.plot().backgroundFill(),
            penColor = theme.colors().pen()
        )

        val geomBounds = layoutInfo.geomInnerBounds
        layerComponent.moveTo(geomBounds.origin)
        layerComponent.clipBounds(DoubleRectangle(DoubleVector.ZERO, geomBounds.dimension))
        return layerComponent
    }


    companion object {
        private fun buildAxis(
            breaksData: BreaksData,
            info: AxisLayoutInfo,
            hideAxis: Boolean,
            hideAxisBreaks: Boolean,
            axisTheme: AxisTheme,
            labelAdjustments: TickLabelAdjustments,
            isDebugDrawing: Boolean,
        ): AxisComponent {
            val axis = AxisComponent(
                length = info.axisLength,
                orientation = info.orientation,
                breaksData = breaksData,
                labelAdjustments = labelAdjustments,
                axisTheme = axisTheme,
                hideAxis = hideAxis,
                hideAxisBreaks = hideAxisBreaks
            )

            if (isDebugDrawing) {
                fun drawDebugRect(r: DoubleRectangle, color: Color) {
                    val rect = SvgRectElement(r)
                    rect.strokeColor().set(color)
                    rect.strokeWidth().set(1.0)
                    rect.fillOpacity().set(0.0)
                    axis.add(rect)
                }
                drawDebugRect(info.tickLabelsBounds, Color.GREEN)
                info.tickLabelsTextBounds?.let { drawDebugRect(it, Color.LIGHT_BLUE) }
                info.tickLabelBoundsList?.forEach { drawDebugRect(it, Color.LIGHT_MAGENTA) }
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

        private fun buildPanelBorderComponent(bounds: DoubleRectangle, theme: PanelTheme): SvgRectElement {
            return SvgRectElement(bounds).apply {
                strokeColor().set(theme.borderColor())
                strokeWidth().set(theme.borderWidth())
                fillOpacity().set(0.0)
            }
        }

        /**
         * 'internal' access for tests.
         */
        internal fun buildGeom(
            layer: GeomLayer,
            xyAesBounds: DoubleRectangle,
            coord: CoordinateSystem,
            flippedAxis: Boolean,
            targetCollector: GeomTargetCollector,
            backgroundColor: Color,
            penColor: Color
        ): SvgComponent {
            val rendererData = LayerRendererUtil.createLayerRendererData(layer)

            @Suppress("NAME_SHADOWING")
            // val flippedAxis = layer.isYOrientation xor flippedAxis
            // (XOR issue: https://youtrack.jetbrains.com/issue/KT-52296/Kotlin-JS-the-xor-operation-sometimes-evaluates-to-int-value-ins)
            val flippedAxis = if (layer.isYOrientation) !flippedAxis else flippedAxis

            val aestheticMappers = rendererData.aestheticMappers
            val aesthetics = rendererData.aesthetics

            @Suppress("NAME_SHADOWING")
            val coord = when (layer.isYOrientation) {
                true -> coord.flip()
                false -> coord
            }

            @Suppress("NAME_SHADOWING")
            val targetCollector = targetCollector.let {
                when {
                    flippedAxis -> it.withFlippedAxis()
                    else -> it
                }
            }.let {
                when {
                    layer.isYOrientation -> it.withYOrientation()
                    else -> it
                }
            }

            val plotContext = PlotAssemblerPlotContext(listOf(listOf(layer)), layer.scaleMap)
            val ctx = GeomContextBuilder()
                .flipped(flippedAxis)
                .aesthetics(aesthetics)
                .aestheticMappers(aestheticMappers)
                .aesBounds(xyAesBounds)
                .geomTargetCollector(targetCollector)
                .fontFamilyRegistry(layer.fontFamilyRegistry)
                .annotations(rendererData.annotations)
                .backgroundColor(backgroundColor)
                .penColor(penColor)
                .plotContext(plotContext)
                .build()

            val pos = rendererData.pos
            val geom = layer.geom

            return SvgLayerRenderer(aesthetics, geom, pos, coord, ctx)
        }
    }
}
