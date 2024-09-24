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
import org.jetbrains.letsPlot.core.plot.builder.LayerRendererUtil
import org.jetbrains.letsPlot.core.plot.builder.SvgLayerRenderer
import org.jetbrains.letsPlot.core.plot.builder.assemble.GeomContextBuilder
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

    protected fun buildGeom(layer: GeomLayer, targetCollector: GeomTargetCollector): SvgComponent {
        return buildGeom(
            plotContext,
            layer,  // positional aesthetics are the same as positional data.
            xyAesBounds = adjustedDomain.flipIf(flipAxis), // Data space -> View space
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

    companion object {
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
    }
}