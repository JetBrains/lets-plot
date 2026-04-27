/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.awt.NotoFontManager
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasPeer
import org.jetbrains.letsPlot.commons.event.MouseEvent.Companion.noButton
import org.jetbrains.letsPlot.commons.event.MouseEventSpec.MOUSE_MOVED
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelBase
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy.Companion.keepFigureDefaultSize
import org.jetbrains.letsPlot.raster.view.PlotCanvasDrawable
import kotlin.test.Test

class InteractivityTest : VisualPlotTestBase(expectedImagesSubdir = "tooltips") {
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

        val rawPlotSpec = parsePlotSpec(spec)
        val processedPlotSpec = MonolithicCommon.processRawSpecs(rawPlotSpec, frontendOnly = false)
        val plotCanvasDrawable = PlotCanvasDrawable()
        plotCanvasDrawable.update(
            processedSpec = processedPlotSpec,
            sizingPolicy = keepFigureDefaultSize(),
            computationMessagesHandler = { }
        )

        val awtCanvasPeer = AwtCanvasPeer(fontManager = NotoFontManager.INSTANCE)
        plotCanvasDrawable.mapToCanvas(awtCanvasPeer)

        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(200, 100))

        val snapshot = plotCanvasDrawable.takeSnapshot(awtCanvasPeer)

        imageComparer.assertBitmapEquals("interactivity_simple_tooltip.png", snapshot.bitmap)
    }

    @Test
    fun `gggrid tooltip`() {
        val spec = """
            |{
            |  "kind": "subplots",
            |  "ggsize": {
            |    "width": 600.0,
            |    "height": 150.0
            |  },
            |  "layout": { "ncol": 2.0, "nrow": 1.0, "name": "grid" },
            |  "figures": [
            |    {
            |      "data": {
            |        "x": [ 1.0, 2.0, 3.0 ],
            |        "c": [ "a", "b", "c" ],
            |        "g": [ 0.0, 0.0, 0.0 ]
            |      },
            |      "data_meta": {
            |        "series_annotations": [ 
            |          { "type": "int", "column": "x" },
            |          { "type": "str", "column": "c" },
            |          { "type": "int", "column": "g" }
            |        ]
            |      },
            |      "kind": "plot",
            |      "layers": [
            |        {
            |          "geom": "point",
            |          "mapping": { "x": "x", "color": "c" },
            |          "tooltips": {
            |            "formats": [],
            |            "lines": [ "^color" ]
            |          }
            |        }
            |      ]
            |    },
            |    {
            |      "data": {
            |        "x": [ 4.0, 5.0, 6.0 ],
            |        "c": [ "d", "e", "f" ],
            |        "g": [ 1.0, 1.0, 1.0 ]
            |      },
            |      "data_meta": {
            |        "series_annotations": [
            |          { "type": "int", "column": "x" },
            |          { "type": "str", "column": "c" },
            |          { "type": "int", "column": "g" }
            |        ]
            |      },
            |      "kind": "plot",
            |      "layers": [
            |        {
            |          "geom": "point",
            |          "mapping": { "x": "x", "color": "c" },
            |          "tooltips": {
            |            "formats": [],
            |            "lines": [ "^color" ]
            |          }
            |        }
            |      ]
            |    }
            |  ]
            |}
        """.trimMargin()

        val rawPlotSpec = parsePlotSpec(spec)
        val processedPlotSpec = MonolithicCommon.processRawSpecs(rawPlotSpec, frontendOnly = false)
        val plotCanvasDrawable = PlotCanvasDrawable()
        plotCanvasDrawable.update(
            processedSpec = processedPlotSpec,
            sizingPolicy = keepFigureDefaultSize(),
            computationMessagesHandler = { }
        )

        val awtCanvasPeer = AwtCanvasPeer(fontManager = NotoFontManager.INSTANCE)
        plotCanvasDrawable.mapToCanvas(awtCanvasPeer)

        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(450, 80))

        val snapshot = plotCanvasDrawable.takeSnapshot(awtCanvasPeer)

        imageComparer.assertBitmapEquals("interactivity_gggrid_tooltip.png", snapshot.bitmap)
    }

    private fun PlotCanvasDrawable.takeSnapshot(canvasPeer: CanvasPeer): Canvas.Snapshot {
        val canvas = canvasPeer.createCanvas(size)
        paint(canvas.context2d)
        return canvas.takeSnapshot()
    }

    class TestingFigureModel(
        val onUpdateView: () -> Unit
    ) : FigureModelBase() {

        override fun updateSpecOverride(specOverride: Map<String, Any>?) {
            // No-op in tests
        }

        override fun updateView() {
            onUpdateView()
        }
    }
}
