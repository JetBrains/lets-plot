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
) : PlotTestBase() {
    init {
        registerTest(::plot_interactivity_facetGrid_tooltip)
        registerTest(::plot_interactivity_panInProgress_withIncompleteBuffer)
        registerTest(::plot_interactivity_composite_tooltip)
        registerTest(::plot_interactivity_nestedComposite_tooltip)

        registerTest(::plot_interactivity_pointAndLine_lineTooltip)
        registerTest(::plot_interactivity_pointAndLine_pointTooltip)
        registerTest(::plot_interactivity_pointAndPoint_pointTooltip)
        registerTest(::plot_interactivity_pointAndPolygon_polygonTooltip)
        registerTest(::plot_interactivity_pointAndPolygon_pointTooltip)
        registerTest(::plot_interactivity_pointAndBar_barTooltip)
        registerTest(::plot_interactivity_pointAndBar_pointTooltip)
        registerTest(::plot_interactivity_boxplotAndPoint_pointTooltip)
        registerTest(::plot_interactivity_pathDistancePriority_implicitLineGroupTooltip)
        registerTest(::plot_interactivity_pathDistancePriority_separateGroupsLowerTooltip)
        registerTest(::plot_interactivity_pointLineSmooth_linesTooltip)
        registerTest(::plot_interactivity_pointLineSmooth_pointTooltip)
        registerTest(::plot_interactivity_histogramDensity_densityTooltip)
        registerTest(::plot_interactivity_histogramDensity_histogramTooltip)

        registerTest(::plot_interactivity_barPositiveHeight_tooltip)
        registerTest(::plot_interactivity_barPositiveHeightHorizontal_tooltip)
        registerTest(::plot_interactivity_barNegativeHeight_tooltip)
        registerTest(::plot_interactivity_barNegativeHeightHorizontal_tooltip)
        registerTest(::plot_interactivity_polygonOverlapped_tooltip)
        registerTest(::plot_interactivity_barOverlapped_tooltip)
        registerTest(::plot_interactivity_rectOverlapped_tooltip)

        registerTest(::plot_interactivity_pointAndPoint_withCrosshair)
        registerTest(::plot_interactivity_pointAndPoint_withCrosshair_overlapNearerPointTooltip)
        registerTest(::plot_interactivity_pointAndText_pointTooltip)
        registerTest(::plot_interactivity_pointAndLabel_pointTooltip)
        registerTest(::plot_interactivity_groupedLine_closestByXTooltip)
        registerTest(::plot_interactivity_logicalGroup_differentXAxisTooltip)
        registerTest(::plot_interactivity_logicalGroup_differentXAxisTooltip_reversedSides)
        registerTest(::plot_interactivity_barOverlappedMany_singleTooltip)

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

    fun plot_interactivity_pointAndLine_lineTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.POINT_AND_LINE))

        val cursorPos = Vector(465, 80)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_pointAndLine_pointTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.POINT_AND_LINE))

        val cursorPos = Vector(460, 135)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_pointAndPoint_pointTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.POINT_AND_POINT))

        val cursorPos = Vector(460, 135)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_pointAndPolygon_polygonTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.POINT_AND_POLYGON))

        val cursorPos = Vector(155, 195)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_pointAndPolygon_pointTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.POINT_AND_POLYGON))

        val cursorPos = Vector(167, 132)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_pointAndBar_barTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.POINT_AND_BAR))

        val cursorPos = Vector(205, 110)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_pointAndBar_pointTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.POINT_AND_BAR))

        val cursorPos = Vector(176, 159)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_boxplotAndPoint_pointTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.BOXPLOT_AND_POINT))

        val cursorPos = Vector(216, 150)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_pathDistancePriority_implicitLineGroupTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.PATH_DISTANCE_PRIORITY_IMPLICIT_LINE_GROUP))

        val cursorPos = Vector(305, 210)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_pathDistancePriority_separateGroupsLowerTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.PATH_DISTANCE_PRIORITY_SEPARATE_GROUPS))

        val cursorPos = Vector(305, 210)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_pointLineSmooth_linesTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.POINT_LINE_SMOOTH))

        val cursorPos = Vector(460, 135)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_pointLineSmooth_pointTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.POINT_LINE_SMOOTH))

        val cursorPos = Vector(445, 295)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_histogramDensity_densityTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.HISTOGRAM_DENSITY))

        val cursorPos = Vector(265, 155)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_histogramDensity_histogramTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.HISTOGRAM_DENSITY))

        val cursorPos = Vector(265, 345)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_barPositiveHeight_tooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.BAR_WITH_NEGATIVE_HEIGHT))

        val cursorPos = Vector(260, 120)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_barPositiveHeightHorizontal_tooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.BAR_WITH_NEGATIVE_HEIGHT_HORIZONTAL))

        val cursorPos = Vector(290, 90)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_barNegativeHeight_tooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.BAR_WITH_NEGATIVE_HEIGHT))

        val cursorPos = Vector(180, 220)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_barNegativeHeightHorizontal_tooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.BAR_WITH_NEGATIVE_HEIGHT_HORIZONTAL))

        val cursorPos = Vector(140, 140)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_polygonOverlapped_tooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.POLYGON_OVERLAPPED))

        val cursorPos = Vector(175, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_barOverlapped_tooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.BAR_OVERLAPPED))

        val cursorPos = Vector(175, 165)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_rectOverlapped_tooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.RECT_OVERLAPPED))

        val cursorPos = Vector(175, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_pointAndPoint_withCrosshair(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.POINT_AND_POINT_WITH_CROSSHAIR))

        val cursorPos = Vector(235, 165)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_pointAndPoint_withCrosshair_overlapNearerPointTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.POINT_AND_POINT_WITH_CROSSHAIR_OVERLAP))

        val cursorPos = Vector(331, 191)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_pointAndText_pointTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.POINT_AND_TEXT))

        val cursorPos = Vector(205, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_pointAndLabel_pointTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.POINT_AND_LABEL))

        val cursorPos = Vector(205, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_groupedLine_closestByXTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.GROUPED_LINE_CLOSEST_BY_X))

        val cursorPos = Vector(205, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_logicalGroup_differentXAxisTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.LOGICAL_GROUP_DIFFERENT_X_TOOLTIP))

        val cursorPos = Vector(205, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_logicalGroup_differentXAxisTooltip_reversedSides(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.LOGICAL_GROUP_DIFFERENT_X_TOOLTIP_REVERSED_SIDES))

        val cursorPos = Vector(103, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_interactivity_barOverlappedMany_singleTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotSpecs.BAR_OVERLAPPED_MANY))

        val cursorPos = Vector(205, 75)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

}
