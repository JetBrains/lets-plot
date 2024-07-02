/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.frame

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.coord.TransformedCoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.render.svg.StrokeDashArraySupport
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.base.theme.PanelGridTheme
import org.jetbrains.letsPlot.core.plot.base.theme.PanelTheme
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.builder.*
import org.jetbrains.letsPlot.core.plot.builder.assemble.GeomContextBuilder
import org.jetbrains.letsPlot.core.plot.builder.guide.AxisComponent
import org.jetbrains.letsPlot.core.plot.builder.guide.AxisComponent.BreaksData
import org.jetbrains.letsPlot.core.plot.builder.guide.AxisComponent.TickLabelAdjustments
import org.jetbrains.letsPlot.core.plot.builder.guide.GridComponent
import org.jetbrains.letsPlot.core.plot.builder.layout.AxisLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.GeomMarginsLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayoutInfo
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement

internal open class SquareFrameOfReference(
    private val hScaleBreaks: ScaleBreaks,
    private val vScaleBreaks: ScaleBreaks,
    private val adjustedDomain: DoubleRectangle,         // Transformed and adjusted XY data ranges.
    private val coord: CoordinateSystem,
    private val layoutInfo: TileLayoutInfo,
    private val marginsLayout: GeomMarginsLayout,
    private val theme: Theme,
    private val flipAxis: Boolean,
    private val plotContext: PlotContext
) : FrameOfReference() {

    var isDebugDrawing: Boolean = false

    // Flip theme
    protected val hAxisTheme = theme.horizontalAxis(flipAxis)
    protected val vAxisTheme = theme.verticalAxis(flipAxis)

    override val transientState: ComponentTransientState = TransientState()

    override fun toDataBounds(clientRect: DoubleRectangle): DoubleRectangle {
        val domainPoint0 = coord.fromClient(clientRect.origin)
            ?: error("Can't translate client ${clientRect.origin} to data domain.")
        val clientBottomRight = clientRect.origin.add(clientRect.dimension)
        val domainPoint1 = coord.fromClient(clientBottomRight)
            ?: error("Can't translate client $clientBottomRight to data domain.")
        return DoubleRectangle.span(domainPoint0, domainPoint1)
    }

    // Rendering

    override fun drawBeforeGeomLayer(parent: SvgComponent) {
        drawPanelAndAxis(parent, beforeGeomLayer = true)
    }

    override fun drawAfterGeomLayer(parent: SvgComponent) {
        drawPanelAndAxis(parent, beforeGeomLayer = false)
    }

    protected open fun drawPanelAndAxis(parent: SvgComponent, beforeGeomLayer: Boolean) {
        val geomInnerBounds: DoubleRectangle = layoutInfo.geomInnerBounds
        val panelTheme = theme.panel()

        val vGridTheme = panelTheme.verticalGrid(flipAxis)
        val hGridTheme = panelTheme.horizontalGrid(flipAxis)

        val fillBkgr = panelTheme.showRect() && beforeGeomLayer
        val strokeBkgr = panelTheme.showRect() && (panelTheme.borderIsOntop() xor beforeGeomLayer)
        val drawPanelBorder = panelTheme.showBorder() && (panelTheme.borderIsOntop() xor beforeGeomLayer)

        val drawVGrid = beforeGeomLayer xor vGridTheme.isOntop()
        val drawHGrid = beforeGeomLayer xor hGridTheme.isOntop()
        val drawHAxis = beforeGeomLayer xor hAxisTheme.isOntop()
        val drawVAxis = beforeGeomLayer xor vAxisTheme.isOntop()

        if (fillBkgr) {
            doFillBkgr(parent)
        }

        if (drawVGrid) {
            doDrawVGrid(vGridTheme, parent)
        }

        if (drawHGrid) {
            doDrawHGrid(hGridTheme, parent)
        }

        if (drawHAxis) {
            doDrawHAxis(parent)
        }

        if (drawVAxis) {
            doDrawVAxis(parent)
        }

        if (strokeBkgr) {
            doStrokeBkgr(parent)
        }

        if (drawPanelBorder) {
            doDrawPanelBorder(parent)
        }

        if (isDebugDrawing && !beforeGeomLayer) {
            drawDebugShapes(parent, geomInnerBounds)
        }
    }

    protected open fun doDrawPanelBorder(parent: SvgComponent) {
        val panelBorder = SvgRectElement(layoutInfo.geomContentBounds).apply {
            strokeColor().set(theme.panel().borderColor())
            strokeWidth().set(theme.panel().borderWidth())
            StrokeDashArraySupport.apply(this, theme.panel().borderWidth(), theme.panel().borderLineType())
            fillOpacity().set(0.0)
        }
        parent.add(panelBorder)
    }

    protected open fun doDrawVAxis(parent: SvgComponent) {
        listOfNotNull(layoutInfo.axisInfos.left, layoutInfo.axisInfos.right).forEach { axisInfo ->
            val (labelAdjustments, breaksData) = prepareAxisData(axisInfo, vScaleBreaks, vAxisTheme, theme.panel())

            val axisComponent = buildAxis(
                breaksData = breaksData,
                axisInfo,
                hideAxis = false,
                hideAxisBreaks = !layoutInfo.vAxisShown,
                axisTheme = vAxisTheme,
                labelAdjustments = labelAdjustments,
                isDebugDrawing = isDebugDrawing,
            )

            val axisOrigin = marginsLayout.toAxisOrigin(
                layoutInfo.geomInnerBounds,
                axisInfo.orientation,
                coord.isPolar,
                theme.panel().inset()
            )
            axisComponent.moveTo(axisOrigin)
            parent.add(axisComponent)
        }
    }

    protected open fun doDrawHAxis(parent: SvgComponent) {
        listOfNotNull(layoutInfo.axisInfos.top, layoutInfo.axisInfos.bottom).forEach { axisInfo ->
            val (labelAdjustments, breaksData) = prepareAxisData(axisInfo, hScaleBreaks, hAxisTheme, theme.panel())

            val axisComponent = buildAxis(
                breaksData = breaksData,
                info = axisInfo,
                hideAxis = false,
                hideAxisBreaks = !layoutInfo.hAxisShown,
                axisTheme = hAxisTheme,
                labelAdjustments = labelAdjustments,
                isDebugDrawing = isDebugDrawing,
            )

            val axisOrigin = marginsLayout.toAxisOrigin(
                layoutInfo.geomInnerBounds,
                axisInfo.orientation,
                coord.isPolar,
                theme.panel().inset()
            )
            axisComponent.moveTo(axisOrigin)
            parent.add(axisComponent)
        }
    }

    protected open fun doDrawHGrid(gridTheme: PanelGridTheme, parent: SvgComponent) {
        listOfNotNull(layoutInfo.axisInfos.left, layoutInfo.axisInfos.right).forEach { axisInfo ->
            val (_, breaksData) = prepareAxisData(axisInfo, vScaleBreaks, vAxisTheme, theme.panel())

            val gridComponent = GridComponent(
                majorGrid = breaksData.majorGrid,
                minorGrid = breaksData.minorGrid,
                isHorizontal = true,
                isOrthogonal = true,
                geomContentBounds = layoutInfo.geomContentBounds,
                gridTheme = gridTheme,
                panelTheme = theme.panel(),
            )
            val gridOrigin = layoutInfo.geomContentBounds.origin
            gridComponent.moveTo(gridOrigin)
            parent.add(gridComponent)
        }
    }

    protected open fun doDrawVGrid(gridTheme: PanelGridTheme, parent: SvgComponent) {
        listOfNotNull(layoutInfo.axisInfos.top, layoutInfo.axisInfos.bottom).forEach { axisInfo ->
            val (_, breaksData) = prepareAxisData(axisInfo, hScaleBreaks, hAxisTheme, theme.panel())

            val gridComponent = GridComponent(
                majorGrid = breaksData.majorGrid,
                minorGrid = breaksData.minorGrid,
                isHorizontal = false,
                isOrthogonal = true,
                geomContentBounds = layoutInfo.geomContentBounds,
                gridTheme = gridTheme,
                panelTheme = theme.panel(),
            )
            val gridOrigin = layoutInfo.geomContentBounds.origin
            gridComponent.moveTo(gridOrigin)
            parent.add(gridComponent)
        }
    }

    protected open fun doFillBkgr(parent: SvgComponent) {
        val panel = SvgRectElement(layoutInfo.geomContentBounds).apply {
            fillColor().set(theme.panel().rectFill())
        }
        parent.add(panel)
    }

    protected open fun doStrokeBkgr(parent: SvgComponent) {
        val panelRectStroke = SvgRectElement(layoutInfo.geomContentBounds).apply {
            strokeColor().set(theme.panel().rectColor())
            strokeWidth().set(theme.panel().rectStrokeWidth())
            StrokeDashArraySupport.apply(this, theme.panel().rectStrokeWidth(), theme.panel().rectLineType())
            fillOpacity().set(0.0)
        }
        parent.add(panelRectStroke)
    }


    protected open fun prepareAxisData(
        axisInfo: AxisLayoutInfo,
        scaleBreaks: ScaleBreaks,
        axisTheme: AxisTheme,
        panelTheme: PanelTheme
    ): Pair<TickLabelAdjustments, BreaksData> {
        val labelAdjustments = TickLabelAdjustments(
            orientation = axisInfo.orientation,
            horizontalAnchor = axisInfo.tickLabelHorizontalAnchor,
            verticalAnchor = axisInfo.tickLabelVerticalAnchor,
            rotationDegree = axisInfo.tickLabelRotationAngle,
            additionalOffsets = axisInfo.tickLabelAdditionalOffsets
        )

        val breaksData = AxisUtil.breaksData(
            scaleBreaks = scaleBreaks,
            coord = TransformedCoordinateSystem(
                coord,
                translate = transientState.offset,
                scale = transientState.scale
            ),
            domain = adjustedDomain,
            flipAxis = flipAxis,
            orientation = axisInfo.orientation,
            axisTheme = axisTheme,
            labelAdjustments = labelAdjustments
        )
        return Pair(labelAdjustments, breaksData)
    }

    protected fun drawDebugShapes(parent: SvgComponent, geomBounds: DoubleRectangle) {
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
        return buildGeom(layer, targetCollector)
    }

    override fun setClip(element: SvgComponent) {
        element.clipBounds(layoutInfo.geomContentBounds)
    }

    protected fun buildGeom(layer: GeomLayer, targetCollector: GeomTargetCollector): SvgComponent {
        return buildGeom(
            plotContext,
            layer,  // positional aesthetics are the same as positional data.
            xyAesBounds = adjustedDomain,
            coord,
            flipAxis,
            targetCollector,
            backgroundColor = if (theme.panel().showRect()) theme.panel().rectFill() else theme.plot().backgroundFill()
        )
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
        ): SvgComponent {
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

        /**
         * 'internal' access for tests.
         */
        internal fun buildGeom(
            plotContext: PlotContext,
            layer: GeomLayer,
            xyAesBounds: DoubleRectangle,
            coord: CoordinateSystem,
            flippedAxis: Boolean,
            targetCollector: GeomTargetCollector,
            backgroundColor: Color
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

            val ctx = GeomContextBuilder()
                .flipped(flippedAxis)
                .aesthetics(aesthetics)
                .aestheticMappers(aestheticMappers)
                .aesBounds(xyAesBounds)
                .geomTargetCollector(targetCollector)
                .fontFamilyRegistry(layer.fontFamilyRegistry)
                .annotation(rendererData.annotation)
                .backgroundColor(backgroundColor)
                .plotContext(plotContext)
                .build()

            val pos = rendererData.pos
            val geom = layer.geom

            return SvgLayerRenderer(aesthetics, geom, pos, coord, ctx)
        }
    }

    inner class TransientState : ComponentTransientState(
        bounds = layoutInfo.geomContentBounds
    ) {
        override fun toDataBounds(clientRect: DoubleRectangle): DoubleRectangle {
            return this@SquareFrameOfReference.toDataBounds(clientRect)
        }

        override fun repaint() {
            // Repaint axis and grid.
            repaintFrame()
        }
    }
}
