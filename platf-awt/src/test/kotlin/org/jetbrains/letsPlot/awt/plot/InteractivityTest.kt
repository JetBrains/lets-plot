package org.jetbrains.letsPlot.awt.plot

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasPeer
import org.jetbrains.letsPlot.commons.event.MouseEvent.Companion.leftButton
import org.jetbrains.letsPlot.commons.event.MouseEvent.Companion.noButton
import org.jetbrains.letsPlot.commons.event.MouseEventSpec.*
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.interact.InteractionSpec
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.DefaultFigureToolsController
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelHelper
import org.jetbrains.letsPlot.core.spec.front.SpecOverrideUtil.applySpecOverride
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy.Companion.keepFigureDefaultSize
import org.jetbrains.letsPlot.raster.view.PlotCanvasFigure
import org.jetbrains.letsPlot.raster.view.PlotFigureModel
import org.jetbrains.letsPlot.raster.view.RenderingHints
import kotlin.test.Test

class InteractivityTest : VisualPlotTestBase() {
    @Test
    fun `simple tooltip`() {
        val spec = """
            |{
            |  "kind": "plot",
            |  "ggsize": { "width": 400, "height": 200 },
            |  "data": {
            |    "x": [ 1.0, 2.0, 3.0, 4.0, 5.0 ],
            |    "c": [ "a", "b", "c", "d", "e" ]
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "type": "int", "column": "x" },
            |      { "type": "str", "column": "c" }
            |    ]
            |  },
            |  "layers": [
            |    {
            |      "geom": "point",
            |      "mapping": { "x": "x", "color": "c" }
            |    }
            |  ]
            |}            
        """.trimMargin()

        val rawPlotSpec = parsePlotSpec(spec).themeTextNotoSans()
        val processedPlotSpec = MonolithicCommon.processRawSpecs(rawPlotSpec, frontendOnly = false)
        val plotCanvasFigure = PlotCanvasFigure()
        plotCanvasFigure.update(
            processedSpec = processedPlotSpec,
            sizingPolicy = keepFigureDefaultSize(),
            computationMessagesHandler = { }
        )

        val awtCanvasPeer = AwtCanvasPeer()
        plotCanvasFigure.mapToCanvas(awtCanvasPeer)

        plotCanvasFigure.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(200, 100))

        val snapshot = plotCanvasFigure.takeSnapshot(awtCanvasPeer)

        imageComparer.assertBitmapEquals("interactivity_simple_tooltip.png", snapshot.bitmap)
    }

    @Test
    fun `repaint manager updates buffer during pan when cached buffer incomplete`() {
        // With xlim = [20, 40] and overscan factor = 1, the repaint manager creates a buffer covering the range [20, 40].
        // Dragging left by half the plot width (200 px out of 400 px) shifts the visible range to [30, 50].
        // This makes a buffer incomplete and triggers update to ensure the range [30, 50] is fully covered.

        val plotCanvasFigure = PlotCanvasFigure()
        plotCanvasFigure.setRenderingHint(RenderingHints.KEY_OVERSCAN_FACTOR, 1.0)

        val spec = """
            |{
            |  "kind": "plot",
            |  "ggsize": { "width": 400, "height": 400 },
            |  "coord": {
            |    "name": "cartesian",
            |    "xlim": [ 20.0, 40.0 ],
            |    "ylim": [ 0.0, 100.0 ]
            |  },
            |  "layers": [
            |    {
            |      "geom": "segment",
            |      "x": 0.0,
            |      "y": 0.0,
            |      "xend": 100.0,
            |      "yend": 100.0
            |    }
            |  ]
            |}
        """.trimMargin()

        val rawPlotSpec = parsePlotSpec(spec).themeTextNotoSans()
        val processedPlotSpec = MonolithicCommon.processRawSpecs(rawPlotSpec, frontendOnly = false)

        var specOverrideList = emptyList<Map<String, Any>>()

        val plotFigureModel = PlotFigureModel(
            onUpdateView = { specOverride ->
                specOverrideList = FigureModelHelper.updateSpecOverrideList(
                    specOverrideList = specOverrideList,
                    newSpecOverride = specOverride
                )

                plotCanvasFigure.update(
                    processedSpec = applySpecOverride(processedPlotSpec, specOverrideList),
                    sizingPolicy = keepFigureDefaultSize(),
                    computationMessagesHandler = { })
            }
        )

        val controller = DefaultFigureToolsController(plotFigureModel, errorMessageHandler = ::println)
        plotFigureModel.setDefaultInteractions(listOf(InteractionSpec(InteractionSpec.Name.DRAG_PAN)))
        plotFigureModel.addToolEventCallback(controller::handleToolFeedback)

        plotCanvasFigure.update(processedPlotSpec, keepFigureDefaultSize(), computationMessagesHandler = { })

        val awtCanvasPeer = AwtCanvasPeer()
        plotCanvasFigure.mapToCanvas(awtCanvasPeer)

        // IMPORTANT: should be set after mapping to canvas
        plotFigureModel.toolEventDispatcher = plotCanvasFigure.toolEventDispatcher

        plotCanvasFigure.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(200, 200))
        plotCanvasFigure.mouseEventPeer.dispatch(MOUSE_PRESSED, leftButton(200, 200))
        plotCanvasFigure.mouseEventPeer.dispatch(MOUSE_DRAGGED, leftButton(200, 200))

        // Paint to create the initial buffer covering the range [20, 40]
        plotCanvasFigure.takeSnapshot(awtCanvasPeer)

        // Drag left by 200 px - this makes the buffer incomplete (visible range is now [30, 50])
        plotCanvasFigure.mouseEventPeer.dispatch(MOUSE_DRAGGED, leftButton(0, 200))

        val snapshot = plotCanvasFigure.takeSnapshot(awtCanvasPeer)
        imageComparer.assertBitmapEquals("interactivity_pan_in_progress_with_incomplete_buffer.png", snapshot.bitmap)
    }

    private fun PlotCanvasFigure.takeSnapshot(canvasPeer: CanvasPeer): Canvas.Snapshot {
        val canvas = canvasPeer.createCanvas(size)
        paint(canvas.context2d)
        return canvas.takeSnapshot()
    }
}