@file:Suppress("FunctionName")

package org.jetbrains.letsPlot.visualtesting.plot

import org.jetbrains.letsPlot.commons.event.MouseEvent.Companion.leftButton
import org.jetbrains.letsPlot.commons.event.MouseEvent.Companion.noButton
import org.jetbrains.letsPlot.commons.event.MouseEventSpec.*
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.raster.view.RenderingHints
import org.jetbrains.letsPlot.visualtesting.ImageComparer

class PlotInteractivityTest(
    override val canvasPeer: CanvasPeer,
    override val imageComparer: ImageComparer,
) : PlotTestBase() {
    init {
        registerTest(::plot_interactivity_facetGridTooltip)
        registerTest(::plot_interactivity_panInProgressWithIncompleteBuffer)
        registerTest(::plot_interactivity_compositeTooltip)
        registerTest(::plot_interactivity_nestedCompositeTooltip)

        // TODO: fix it
        //registerTest(::plot_interactivity_panNestedComposite)
    }

    fun plot_interactivity_facetGridTooltip(): Bitmap {
        val spec = """
            |{
            |  "kind": "plot",
            |  "ggsize": { "width": 600.0, "height": 150.0 },
            |  "data": {
            |    "x": [ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 ],
            |    "c": [ "a", "b", "c", "d", "e", "f" ],
            |    "g": [ 0.0, 0.0, 0.0, 1.0, 1.0, 1.0 ]
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      { "type": "int", "column": "x" },
            |      { "type": "str", "column": "c" },
            |      { "type": "int", "column": "g" }
            |    ]
            |  },
            |  "facet": { "name": "grid", "x": "g", "x_order": 1.0, "y_order": 1.0 },
            |  "layers": [
            |    {
            |      "geom": "point",
            |      "mapping": { "x": "x", "color": "c" },
            |      "tooltips": { "lines": [ "^color" ] }
            |    }
            |  ]
            |}
        """.trimMargin()

        val plotCanvasDrawable = createPlot(parseJson(spec))

        val cursorPos = Vector(500, 80)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_panInProgressWithIncompleteBuffer(): Bitmap {
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

        // With xlim = [20, 40] and overscan factor = 1, the repaint manager creates a buffer covering the range [20, 40].
        // Dragging left by half the plot width (200 px out of 400 px) shifts the visible range to [30, 50].
        // This makes a buffer incomplete and triggers update to ensure the range [30, 50] is fully covered.
        val plotCanvasDrawable = createPlot(
            plotSpec = parseJson(spec),
            renderingHints = mapOf(RenderingHints.KEY_OVERSCAN_FACTOR to 1.0)
        )

        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(200, 200))
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_PRESSED, leftButton(200, 200))
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_DRAGGED, leftButton(200, 200))

        // Paint to create the initial buffer covering the range [20, 40]
        paint(plotCanvasDrawable)

        // Drag left by 200 px - this makes the buffer incomplete (visible range is now [30, 50])
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_DRAGGED, leftButton(0, 200))

        return paint(plotCanvasDrawable)
    }

    fun plot_interactivity_compositeTooltip(): Bitmap {
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

        val plotCanvasDrawable = createPlot(parseJson(spec))

        val cursorPos = Vector(450, 80)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_nestedCompositeTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.COMPOSITE_NESTED))

        val cursorPos = Vector(100, 180)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_panNestedComposite(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.COMPOSITE_NESTED))

        val dragStartPos = Vector(100, 180)
        val dragEndPos = dragStartPos.add(Vector(50, 0))

        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(dragStartPos))
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_PRESSED, leftButton(dragStartPos))
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_DRAGGED, leftButton(dragEndPos))
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_RELEASED, leftButton(dragEndPos))

        return paint(plotCanvasDrawable, dragEndPos)
    }
}
