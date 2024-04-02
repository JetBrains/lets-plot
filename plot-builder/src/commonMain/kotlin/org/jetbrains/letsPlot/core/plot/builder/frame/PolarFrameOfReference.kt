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
    private val adjustedDomain: DoubleRectangle,
    coord: CoordinateSystem,
    private val layoutInfo: TileLayoutInfo,
    private val marginsLayout: GeomMarginsLayout,
    private val theme: Theme,
    private val flipAxis: Boolean
) : SquareFrameOfReference(
    hScaleBreaks,
    vScaleBreaks,
    adjustedDomain,
    coord,
    layoutInfo,
    marginsLayout,
    theme,
    flipAxis,
    plotContext
) {
    private val coord: PolarCoordinateSystem = coord as PolarCoordinateSystem

    override fun doDrawVAxis(parent: SvgComponent) {
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

    override fun doDrawVGrid(vGridTheme: PanelGridTheme, parent: SvgComponent) {
        listOfNotNull(layoutInfo.axisInfos.left, layoutInfo.axisInfos.right).forEach { axisInfo ->
            val (_, breaksData) = prepareAxisData(axisInfo, vScaleBreaks)

            val gridComponent = GridComponent(breaksData.majorGrid, breaksData.minorGrid, vGridTheme)
            val gridOrigin = layoutInfo.geomContentBounds.origin
            gridComponent.moveTo(gridOrigin)
            parent.add(gridComponent)
        }
    }

    override fun doDrawHGrid(hGridTheme: PanelGridTheme, parent: SvgComponent) {
        listOfNotNull(layoutInfo.axisInfos.top, layoutInfo.axisInfos.bottom).forEach { axisInfo ->
            val (_, breaksData) = prepareAxisData(axisInfo, hScaleBreaks)

            val gridComponent = GridComponent(breaksData.majorGrid, breaksData.minorGrid, hGridTheme)
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
        val layerComponent = buildGeom(layer, targetCollector)
        layerComponent.moveTo(layoutInfo.geomContentBounds.origin)

        // Compute clip circle
        val r = layoutInfo.geomContentBounds.width / 2 / (1 + R_EXPAND)
        layerComponent.clipCircle(layoutInfo.geomContentBounds.dimension.mul(0.5), r)
        return layerComponent
    }
}
