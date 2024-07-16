/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.frame

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.theme.PanelGridTheme
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.builder.FrameOfReference
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.layout.GeomMarginsLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayoutInfo
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement

internal abstract class FrameOfReferenceBase(
    protected val plotContext: PlotContext,
    protected val adjustedDomain: DoubleRectangle,         // Transformed and adjusted XY data ranges.
    protected val layoutInfo: TileLayoutInfo,
    protected val marginsLayout: GeomMarginsLayout,
    protected val theme: Theme,
    protected val flipAxis: Boolean,
) : FrameOfReference() {

    protected abstract val coord: CoordinateSystem

    // Flip theme
    protected val hAxisTheme = theme.horizontalAxis(flipAxis)
    protected val vAxisTheme = theme.verticalAxis(flipAxis)

    var isDebugDrawing: Boolean = false

    abstract fun doDrawVAxis(parent: SvgComponent)
    abstract fun doDrawHAxis(parent: SvgComponent)
    abstract fun doDrawHGrid(gridTheme: PanelGridTheme, parent: SvgComponent)
    abstract fun doDrawVGrid(gridTheme: PanelGridTheme, parent: SvgComponent)
    abstract fun doFillBkgr(parent: SvgComponent)
    abstract fun doStrokeBkgr(parent: SvgComponent)
    abstract fun doDrawPanelBorder(parent: SvgComponent)

    override fun drawBeforeGeomLayer(parent: SvgComponent) {
        drawPanelAndAxis(parent, beforeGeomLayer = true)
    }

    override fun drawAfterGeomLayer(parent: SvgComponent) {
        drawPanelAndAxis(parent, beforeGeomLayer = false)
    }

    override fun toDataBounds(clientRect: DoubleRectangle): DoubleRectangle {
        val domainPoint0 = coord.fromClient(clientRect.origin)
            ?: error("Can't translate client ${clientRect.origin} to data domain.")
        val clientBottomRight = clientRect.origin.add(clientRect.dimension)
        val domainPoint1 = coord.fromClient(clientBottomRight)
            ?: error("Can't translate client $clientBottomRight to data domain.")
        return DoubleRectangle.span(domainPoint0, domainPoint1)
    }

    protected fun buildGeom(layer: GeomLayer, targetCollector: GeomTargetCollector): SvgComponent {
        return SquareFrameOfReference.buildGeom(
            plotContext,
            layer,  // positional aesthetics are the same as positional data.
            xyAesBounds = adjustedDomain,
            coord,
            flipAxis,
            targetCollector,
            backgroundColor = if (theme.panel().showRect()) theme.panel().rectFill() else theme.plot().backgroundFill()
        )
    }

    private fun drawPanelAndAxis(parent: SvgComponent, beforeGeomLayer: Boolean) {
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
}