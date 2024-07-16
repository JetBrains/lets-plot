/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.frame

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.interact.InteractionUtil
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.Transform
import org.jetbrains.letsPlot.core.plot.base.coord.TransformedCoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.render.svg.StrokeDashArraySupport
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.base.theme.PanelGridTheme
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

internal class SquareFrameOfReference(
    plotContext: PlotContext,
    hScaleBreaks: ScaleBreaks,
    vScaleBreaks: ScaleBreaks,
    adjustedDomain: DoubleRectangle,         // Transformed and adjusted XY data ranges.
    protected override val coord: CoordinateSystem,
    layoutInfo: TileLayoutInfo,
    marginsLayout: GeomMarginsLayout,
    theme: Theme,
    flipAxis: Boolean,
) : FrameOfReferenceBase(
    plotContext,
    adjustedDomain,
    layoutInfo,
    marginsLayout,
    theme,
    flipAxis,
) {

    override val transientState: TransientState = TransientState(
        hScaleBreaks,
        vScaleBreaks,
        adjustedDomain
    )

    override fun doDrawPanelBorder(parent: SvgComponent) {
        val panelBorder = SvgRectElement(layoutInfo.geomContentBounds).apply {
            strokeColor().set(theme.panel().borderColor())
            strokeWidth().set(theme.panel().borderWidth())
            StrokeDashArraySupport.apply(this, theme.panel().borderWidth(), theme.panel().borderLineType())
            fillOpacity().set(0.0)
        }
        parent.add(panelBorder)
    }

    override fun doDrawVAxis(parent: SvgComponent) {
        listOfNotNull(layoutInfo.axisInfos.left, layoutInfo.axisInfos.right).forEach { axisInfo ->
            val (labelAdjustments, breaksData) = prepareAxisData(
                axisInfo,
                transientState.vBreaksTransformedValues,
                transientState.vBreaksLabels,
                vAxisTheme
            )

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

    override fun doDrawHAxis(parent: SvgComponent) {
        listOfNotNull(layoutInfo.axisInfos.top, layoutInfo.axisInfos.bottom).forEach { axisInfo ->
            val (labelAdjustments, breaksData) = prepareAxisData(
                axisInfo,
                transientState.hBreaksTransformedValues,
                transientState.hBreaksLabels,
                hAxisTheme
            )

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

    override fun doDrawHGrid(gridTheme: PanelGridTheme, parent: SvgComponent) {
        (layoutInfo.axisInfos.left ?: layoutInfo.axisInfos.right)?.let { axisInfo ->
            val (_, breaksData) = prepareAxisData(
                axisInfo,
                transientState.vBreaksTransformedValues,
                transientState.vBreaksLabels,
                vAxisTheme
            )

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

    override fun doDrawVGrid(gridTheme: PanelGridTheme, parent: SvgComponent) {
        (layoutInfo.axisInfos.top ?: layoutInfo.axisInfos.bottom)?.let { axisInfo ->
            val (_, breaksData) = prepareAxisData(
                axisInfo,
                transientState.hBreaksTransformedValues,
                transientState.hBreaksLabels,
                hAxisTheme
            )

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

    override fun doFillBkgr(parent: SvgComponent) {
        val panel = SvgRectElement(layoutInfo.geomContentBounds).apply {
            fillColor().set(theme.panel().rectFill())
        }
        parent.add(panel)
    }

    override fun doStrokeBkgr(parent: SvgComponent) {
        val panelRectStroke = SvgRectElement(layoutInfo.geomContentBounds).apply {
            strokeColor().set(theme.panel().rectColor())
            strokeWidth().set(theme.panel().rectStrokeWidth())
            StrokeDashArraySupport.apply(this, theme.panel().rectStrokeWidth(), theme.panel().rectLineType())
            fillOpacity().set(0.0)
        }
        parent.add(panelRectStroke)
    }

    private fun prepareAxisData(
        axisInfo: AxisLayoutInfo,
        breakTransformedValues: List<Double>,
        breakLabels: List<String>,
        axisTheme: AxisTheme,
    ): Pair<TickLabelAdjustments, BreaksData> {
        val labelAdjustments = TickLabelAdjustments(
            orientation = axisInfo.orientation,
            horizontalAnchor = axisInfo.tickLabelHorizontalAnchor,
            verticalAnchor = axisInfo.tickLabelVerticalAnchor,
            rotationDegree = axisInfo.tickLabelRotationAngle,
            additionalOffsets = axisInfo.tickLabelAdditionalOffsets
        )

        val breaksData = AxisUtil.breaksData(
            breakTransformedValues,
            breakLabels,
            coord = TransformedCoordinateSystem(
                coord,
                translate = transientState.offset,
                scale = transientState.scale
            ),
            domain = transientState.dataBounds,
            flipAxis = flipAxis,
            orientation = axisInfo.orientation,
            axisTheme = axisTheme,
            labelAdjustments = labelAdjustments
        )
        return Pair(labelAdjustments, breaksData)
    }

    override fun buildGeomComponent(layer: GeomLayer, targetCollector: GeomTargetCollector): SvgComponent {
        return buildGeom(layer, targetCollector)
    }

    override fun setClip(element: SvgComponent) {
        element.clipBounds(layoutInfo.geomContentBounds)
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
                .defaultFormatters(layer.defaultFormatters)
                .annotation(rendererData.annotation)
                .backgroundColor(backgroundColor)
                .plotContext(plotContext)
                .build()

            val pos = rendererData.pos
            val geom = layer.geom

            return SvgLayerRenderer(aesthetics, geom, pos, coord, ctx)
        }

        private fun calculateTransientBounds(
            bounds: DoubleRectangle, // component bounds in px
            scale: DoubleVector,
            offset: DoubleVector
        ): DoubleRectangle {
            val viewport = InteractionUtil.viewportFromTransform(
                rect = bounds,
                scale = scale,
                translate = offset
            )
            return viewport
        }
    }


    inner class TransientState(
        private val hScaleBreaks: ScaleBreaks,
        private val vScaleBreaks: ScaleBreaks,
        dataBounds: DoubleRectangle  // transformed domain
    ) : ComponentTransientState(
        viewBounds = layoutInfo.geomContentBounds  // px
    ) {
        val hBreaksTransformedValues = hScaleBreaks.transformedValues.toMutableList()
        val vBreaksTransformedValues = vScaleBreaks.transformedValues.toMutableList()
        val hBreaksLabels = hScaleBreaks.labels.toMutableList()
        val vBreaksLabels = vScaleBreaks.labels.toMutableList()

        override var dataBounds: DoubleRectangle = dataBounds
            private set

        override fun transformView(scale: DoubleVector, offset: DoubleVector) {
            val transientBounds = calculateTransientBounds(viewBounds, scale, offset)
            this.dataBounds = toDataBounds(transientBounds.subtract(viewBounds.origin))
            super.transformView(scale, offset)
        }

        override fun repaint() {
            validateHorizontalBreaks()
            validateVerticalBreaks()

            // Repaint axis and grid.
            repaintFrame()
        }

        private fun validateHorizontalBreaks() {
            if (hScaleBreaks.fixed || hBreaksTransformedValues.size < 2) {
                // not generated - don't validate.
                return
            }

            val dataRange: DoubleSpan = dataBounds.xRange()
            validateBreaksIntern(
                dataRange,
                hBreaksTransformedValues,
                hBreaksLabels,
                hScaleBreaks.transform,
                hScaleBreaks.formatter
            )
        }

        private fun validateVerticalBreaks() {
            if (vScaleBreaks.fixed || vBreaksTransformedValues.size < 2) {
                // not generated - don't validate.
                return
            }

            val dataRange: DoubleSpan = dataBounds.yRange()
            validateBreaksIntern(
                dataRange,
                vBreaksTransformedValues,
                vBreaksLabels,
                vScaleBreaks.transform,
                vScaleBreaks.formatter
            )
        }

        private fun validateBreaksIntern(
            dataRange: DoubleSpan,
            transformedBreaks: MutableList<Double>,
            labels: MutableList<String>,
            transform: Transform,
            formatter: (Any) -> String
        ) {
            if (dataRange.contains(transformedBreaks.first())) {
                val newFirstBreak = transformedBreaks[0] - (transformedBreaks[1] - transformedBreaks[0])
                transformedBreaks.add(0, newFirstBreak)
                val domainV = transform.applyInverse(newFirstBreak)
                labels.add(0, domainV?.let { formatter(domainV) } ?: "---")
            }
            if (dataRange.contains(transformedBreaks.last())) {
                val newLastBreak =
                    transformedBreaks.last() + (transformedBreaks.last() - transformedBreaks[transformedBreaks.size - 2])
                transformedBreaks.add(newLastBreak)
                val domainV = transform.applyInverse(newLastBreak)
                labels.add(domainV?.let { formatter(domainV) } ?: "---")
            }
        }
    }
}
