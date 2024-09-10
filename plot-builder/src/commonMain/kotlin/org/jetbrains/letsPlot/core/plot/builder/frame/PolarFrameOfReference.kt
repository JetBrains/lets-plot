/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.frame

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.render.svg.StrokeDashArraySupport
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.theme.PanelGridTheme
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.PolarAxisUtil
import org.jetbrains.letsPlot.core.plot.builder.PolarAxisUtil.PolarBreaksData
import org.jetbrains.letsPlot.core.plot.builder.coord.PolarCoordinateSystem
import org.jetbrains.letsPlot.core.plot.builder.coord.R_EXPAND
import org.jetbrains.letsPlot.core.plot.builder.guide.AxisComponent
import org.jetbrains.letsPlot.core.plot.builder.guide.GridComponent
import org.jetbrains.letsPlot.core.plot.builder.guide.PolarAxisComponent
import org.jetbrains.letsPlot.core.plot.builder.layout.AxisLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.GeomMarginsLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayoutInfo
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgCircleElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape

internal class PolarFrameOfReference(
    plotContext: PlotContext,
    private val hScaleBreaks: ScaleBreaks,
    private val vScaleBreaks: ScaleBreaks,
    adjustedDomain: DoubleRectangle,
    coord: PolarCoordinateSystem,
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

    protected override val coord: PolarCoordinateSystem = coord

    // ToDo: implement polar transient state
    override val transientState = DummyTransientState()

    override fun doDrawVAxis(parent: SvgComponent) {
        @Suppress("DuplicatedCode")
        listOfNotNull(layoutInfo.axisInfos.left, layoutInfo.axisInfos.right).forEach { axisInfo ->
            val (labelAdjustments, breaksData) = prepareAxisData(axisInfo, vScaleBreaks)

            val axisComponent = PolarAxisComponent(
                length = axisInfo.axisLength,
                orientation = axisInfo.orientation,
                breaksData = breaksData,
                labelAdjustments = labelAdjustments,
                axisTheme = vAxisTheme,
                hideAxisBreaks = !layoutInfo.vAxisShown
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
        @Suppress("DuplicatedCode")
        listOfNotNull(layoutInfo.axisInfos.top, layoutInfo.axisInfos.bottom).forEach { axisInfo ->
            val (labelAdjustments, breaksData) = prepareAxisData(axisInfo, hScaleBreaks)

            val axisComponent = PolarAxisComponent(
                length = axisInfo.axisLength,
                orientation = axisInfo.orientation,
                breaksData = breaksData,
                labelAdjustments = labelAdjustments,
                axisTheme = hAxisTheme,
                hideAxisBreaks = !layoutInfo.hAxisShown
            )

            val axisOrigin = marginsLayout.toAxisOrigin(
                layoutInfo.geomContentBounds,
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
            val (_, breaksData) = prepareAxisData(axisInfo, vScaleBreaks)

            val gridComponent = GridComponent(
                majorGrid = breaksData.majorGrid,
                minorGrid = breaksData.minorGrid,
                isHorizontal = true,
                isOrthogonal = false,
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
            val (_, breaksData) = prepareAxisData(axisInfo, hScaleBreaks)

            val gridComponent = GridComponent(
                majorGrid = breaksData.majorGrid,
                minorGrid = breaksData.minorGrid,
                isHorizontal = false,
                isOrthogonal = false,
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
        val fillBkgr = createPanelElement() {
            it.fillColor().set(theme.panel().rectFill())
        }

        parent.add(fillBkgr)
    }

    override fun doStrokeBkgr(parent: SvgComponent) {
        val strokeBkgr = createPanelElement() {
            it.strokeColor().set(theme.panel().rectColor())
            it.strokeWidth().set(theme.panel().rectStrokeWidth())
            StrokeDashArraySupport.apply(it, theme.panel().rectStrokeWidth(), theme.panel().rectLineType())
            it.fillOpacity().set(0.0)
        }

        parent.add(strokeBkgr)
    }

    override fun doDrawPanelBorder(parent: SvgComponent) {
        val border = createPanelElement() {
            it.strokeColor().set(theme.panel().borderColor())
            it.strokeWidth().set(theme.panel().borderWidth())
            StrokeDashArraySupport.apply(it, theme.panel().borderWidth(), theme.panel().borderLineType())
            it.fillOpacity().set(0.0)
        }

        parent.add(border)
    }

    private fun createPanelElement(block: (SvgShape) -> Unit): SvgNode {
        val shape = when (coord.transformBkgr) {
            true -> SvgCircleElement().apply {
                cx().set(layoutInfo.geomContentBounds.center.x)
                cy().set(layoutInfo.geomContentBounds.center.y)
                r().set((layoutInfo.geomContentBounds.width / 2) / (1 + R_EXPAND))
            }

            false -> SvgRectElement(layoutInfo.geomInnerBounds)
        }

        block(shape)

        return shape
    }

    private fun prepareAxisData(
        axisInfo: AxisLayoutInfo,
        scaleBreaks: ScaleBreaks,
    ): Pair<AxisComponent.TickLabelAdjustments, PolarBreaksData> {
        val labelAdjustments = AxisComponent.TickLabelAdjustments(
            orientation = axisInfo.orientation,
            horizontalAnchor = axisInfo.tickLabelHorizontalAnchor,
            verticalAnchor = axisInfo.tickLabelVerticalAnchor,
            rotationDegree = axisInfo.tickLabelRotationAngle,
            additionalOffsets = axisInfo.tickLabelAdditionalOffsets
        )

        val breaksData = PolarAxisUtil.breaksData(
            scaleBreaks = scaleBreaks,
            coord = coord,
            gridDomain = adjustedDomain,
            flipAxis = flipAxis,
            orientation = axisInfo.orientation,
            labelAdjustments = labelAdjustments
        )
        return Pair(labelAdjustments, breaksData)
    }

    override fun buildGeomComponent(layer: GeomLayer, targetCollector: GeomTargetCollector): SvgComponent {
        return buildGeom(layer, targetCollector)
    }

    override fun setClip(element: SvgComponent) {
        // Compute clip circle
        val r = layoutInfo.geomContentBounds.dimension.x / 2
        element.clipCircle(layoutInfo.geomContentBounds.center, r / (1 + R_EXPAND))
    }
}
