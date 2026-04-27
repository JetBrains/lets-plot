/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.visualtesting.plot

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.interact.InteractionSpec
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.DefaultFigureToolsController
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelBase
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelHelper
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.TARGET_ID
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.SpecOverrideState
import org.jetbrains.letsPlot.core.spec.front.SpecOverrideUtil.applySpecOverride
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.raster.export.PlotRasterExport
import org.jetbrains.letsPlot.raster.view.PlotCanvasDrawable
import org.jetbrains.letsPlot.visualtesting.TestSuit

abstract class PlotTestBase : TestSuit() {
    fun createPlot(
        plotSpec: MutableMap<String, Any?>,
        width: Number? = null,
        height: Number? = null,
        renderingHints: Map<Any, Any> = emptyMap()
    ): PlotCanvasDrawable {
        @Suppress("UNCHECKED_CAST")
        val plotSpec = plotSpec as MutableMap<String, Any>
        val processedPlotSpec = MonolithicCommon.processRawSpecs(plotSpec, frontendOnly = false)

        val plotCanvasDrawable = PlotCanvasDrawable()
        renderingHints.forEach { (key, value) ->
            plotCanvasDrawable.setRenderingHint(key, value)
        }

        val sizingPolicy = if (width != null && height != null) {
            SizingPolicy.fixed(width.toDouble(), height.toDouble())
        } else {
            SizingPolicy.keepFigureDefaultSize()
        }

        val figureModel = TestingFigureModel(
            processedPlotSpec = processedPlotSpec,
            plotCanvasDrawable = plotCanvasDrawable,
            sizingPolicy = sizingPolicy,
        )

        val controller = DefaultFigureToolsController(figureModel, errorMessageHandler = ::println)
        figureModel.setDefaultInteractions(listOf(InteractionSpec(InteractionSpec.Name.DRAG_PAN)))
        figureModel.addToolEventCallback(controller::handleToolFeedback)

        plotCanvasDrawable.update(processedPlotSpec, sizingPolicy, computationMessagesHandler = { })

        plotCanvasDrawable.mapToCanvas(canvasPeer)

        // IMPORTANT: should be set after mapping to canvas
        figureModel.toolEventDispatcher = plotCanvasDrawable.toolEventDispatcher

        return plotCanvasDrawable
    }

    fun paint(plotCanvasDrawable: PlotCanvasDrawable, cursorPos: Vector? = null): Bitmap {
        val canvas = canvasPeer.createCanvas(plotCanvasDrawable.size)
        plotCanvasDrawable.paint(canvas.context2d)

        fun drawCrosshair(coord: Vector, crosshairSize: Double) {
            val x = coord.x.toDouble()
            val y = coord.y.toDouble()
            canvas.context2d.beginPath()
            canvas.context2d.moveTo(x - crosshairSize, y)
            canvas.context2d.lineTo(x + crosshairSize, y)
            canvas.context2d.moveTo(x, y - crosshairSize)
            canvas.context2d.lineTo(x, y + crosshairSize)
            canvas.context2d.stroke()
        }

        if (cursorPos != null) {
            // Draw crosshair cursor pointer with outline for better visibility
            val crosshairSize = 10.0
            canvas.context2d.setStrokeStyle(Color.WHITE)
            canvas.context2d.setLineWidth(3.0)
            drawCrosshair(cursorPos, crosshairSize)

            canvas.context2d.setStrokeStyle(Color.BLACK)
            canvas.context2d.setLineWidth(1.0)
            drawCrosshair(cursorPos, crosshairSize)
        }

        val snapshot = canvas.takeSnapshot()
        val bitmap = snapshot.bitmap
        snapshot.dispose()
        (canvas as? Disposable)?.dispose()
        return bitmap
    }

    fun paint(
        plotSpec: MutableMap<String, Any?>,
        width: Number? = null,
        height: Number? = null,
        unit: SizeUnit? = null,
        dpi: Number? = null,
        scale: Number? = 1.0
    ): Bitmap {
        @Suppress("UNCHECKED_CAST")
        val plotSpec = plotSpec as MutableMap<String, Any>
        val plotSize = if (width != null && height != null) DoubleVector(width, height) else null

        val (bitmap, _) = PlotRasterExport.exportBitmap(plotSpec, plotSize, unit, dpi, scale, canvasPeer)
        return bitmap
    }

    class TestingFigureModel(
        private val processedPlotSpec: Map<String, Any>,
        private val plotCanvasDrawable: PlotCanvasDrawable,
        private val sizingPolicy: SizingPolicy,
    ) : FigureModelBase() {
        private var specOverrideList = emptyList<Map<String, Any>>()
        private var currSpecOverrideState = SpecOverrideState(emptyList(), null)

        override fun updateSpecOverride(specOverride: Map<String, Any>?) {
            specOverrideList = FigureModelHelper.updateSpecOverrideList(specOverrideList, specOverride)
            val activeTargetId = specOverride?.get(TARGET_ID) as? String
            currSpecOverrideState = SpecOverrideState(specOverrideList, activeTargetId)
        }

        override fun updateView() {
            val overriddenSpec = applySpecOverride(processedPlotSpec, currSpecOverrideState)
            plotCanvasDrawable.update(
                processedSpec = overriddenSpec,
                sizingPolicy = sizingPolicy,
                computationMessagesHandler = { })
        }
    }
}
