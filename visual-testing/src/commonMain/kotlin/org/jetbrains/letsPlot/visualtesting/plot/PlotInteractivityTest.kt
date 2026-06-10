/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

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
) : PlotTestSuitBase() {
    init {
        registerTest(::plot_interactivity_facetGrid_tooltip)
        registerTest(::plot_interactivity_panInProgress_withIncompleteBuffer)
        registerTest(::plot_interactivity_composite_tooltip)
        registerTest(::plot_interactivity_nestedComposite_tooltip)
        registerTest(::plot_interactivity_latexTooltip)
        registerTest(::plot_interactivity_mergedTooltip)
        registerTest(::plot_interactivity_mergedTooltipWithTitle)
        registerTest(::plot_interactivity_mergedTooltipFlipped)
        registerTest(::plot_interactivity_mergedTooltipFlippedWithTitle)
        registerTest(::plot_interactivity_mergedTooltipDisableSplitting)
        registerTest(::plot_interactivity_mergedTooltipDisableSplittingWithTitle)
        registerTest(::plot_interactivity_mergedTooltipDisableSplittingExplicitLines)
        registerTest(::plot_interactivity_mergedTooltipDisableSplittingExplicitLinesWithTitle)
        registerTest(::plot_interactivity_smoothTooltipDisableSplittingExplicitLines)

        // TODO: fix it
        //registerTest(::plot_interactivity_panNestedComposite)
    }

    fun plot_interactivity_facetGrid_tooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.FACET_GRID_TOOLTIP))

        val cursorPos = Vector(500, 80)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_panInProgress_withIncompleteBuffer(): Bitmap {
        // With xlim = [20, 40] and overscan factor = 1, the repaint manager creates a buffer covering the range [20, 40].
        // Dragging left by half the plot width (200 px out of 400 px) shifts the visible range to [30, 50].
        // This makes a buffer incomplete and triggers update to ensure the range [30, 50] is fully covered.
        val plotCanvasDrawable = createPlot(
            plotSpec = parseJson(PlotSpecs.PAN_IN_PROGRESS_WITH_INCOMPLETE_BUFFER),
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

    fun plot_interactivity_composite_tooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.COMPOSITE_TOOLTIP))

        val cursorPos = Vector(450, 80)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_nestedComposite_tooltip(): Bitmap {
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

    fun plot_interactivity_latexTooltip(): Bitmap {
        val spec = """
            |{
            |  "data": {
            |    "x": [ 0.5 ],
            |    "y": [ 0.5 ],
            |    "label": [ "\\( \\frac{A}{B} \\)" ]
            |  },
            |  "mapping": {
            |    "x": "x",
            |    "y": "y"
            |  },
            |  "ggsize": {
            |    "width": 300.0,
            |    "height": 200.0
            |  },
            |  "kind": "plot",
            |  "theme": {
            |    "axis": "blank",
            |    "panel_grid": "blank"
            |  },
            |  "layers": [
            |    {
            |      "geom": "point",
            |      "size": 8.0,
            |      "tooltips": {
            |        "lines": [ "@|@label" ],
            |        "title": "@label"
            |      }
            |    }
            |  ]
            |}
        """.trimMargin()

        val plotCanvasDrawable = createPlot(parseJson(spec))

        val cursorPos = Vector(150, 100)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_mergedTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.MERGED_TOOLTIP))

        val cursorPos = Vector(160, 120)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_mergedTooltipWithTitle(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.MERGED_TOOLTIP_WITH_TITLE))

        val cursorPos = Vector(160, 120)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_mergedTooltipFlipped(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.MERGED_TOOLTIP_FLIPPED))

        val cursorPos = Vector(160, 120)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_mergedTooltipFlippedWithTitle(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.MERGED_TOOLTIP_FLIPPED_WITH_TITLE))

        val cursorPos = Vector(160, 120)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_mergedTooltipDisableSplitting(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.MERGED_TOOLTIP_DISABLE_SPLITTING))

        val cursorPos = Vector(160, 120)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_mergedTooltipDisableSplittingWithTitle(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.MERGED_TOOLTIP_DISABLE_SPLITTING_WITH_TITLE))

        val cursorPos = Vector(160, 120)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_mergedTooltipDisableSplittingExplicitLines(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.MERGED_TOOLTIP_DISABLE_SPLITTING_EXPLICIT_LINES))

        val cursorPos = Vector(160, 120)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_mergedTooltipDisableSplittingExplicitLinesWithTitle(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.MERGED_TOOLTIP_DISABLE_SPLITTING_EXPLICIT_LINES_WITH_TITLE))

        val cursorPos = Vector(160, 120)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_smoothTooltipDisableSplittingExplicitLines(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.SMOOTH_TOOLTIP_DISABLE_SPLITTING_EXPLICIT_LINES))

        val cursorPos = Vector(160, 120)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }
}
