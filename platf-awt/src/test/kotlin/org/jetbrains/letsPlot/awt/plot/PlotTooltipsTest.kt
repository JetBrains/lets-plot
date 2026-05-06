/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:Suppress("FunctionName")

package org.jetbrains.letsPlot.awt.plot

import org.jetbrains.letsPlot.awt.NotoFontManager
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasPeer
import org.jetbrains.letsPlot.commons.event.MouseEvent.Companion.noButton
import org.jetbrains.letsPlot.commons.event.MouseEventSpec.MOUSE_MOVED
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.visualtesting.AwtBitmapIO
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import org.jetbrains.letsPlot.visualtesting.plot.PlotVisualTestBase
import org.junit.Rule
import org.junit.rules.TestName
import kotlin.test.Test

class PlotTooltipsTest : PlotVisualTestBase() {
    @get:Rule
    var currentTest = TestName()

    override val canvasPeer: CanvasPeer = AwtCanvasPeer(fontManager = NotoFontManager.INSTANCE)
    override val imageComparer: ImageComparer = ImageComparer(canvasPeer, AwtBitmapIO(subdir = "tooltips"), silent = true)

    override fun currentTestName(): String? = currentTest.methodName

    @Test
    fun plot_tooltips_anchorBar_axisTooltipHidden_noCrosshair() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.BAR_ANCHOR_WITH_AXIS_TOOLTIP_HIDDEN))

        val cursorPos = Vector(235, 120)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_anchorBar_axisTooltipVisible_showCrosshair() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.BAR_ANCHOR_WITH_AXIS_TOOLTIP_VISIBLE))

        val cursorPos = Vector(235, 120)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_anchorRect_withoutAxisTooltip_noCrosshair() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.RECT_ANCHOR_WITHOUT_AXIS_TOOLTIP))

        val cursorPos = Vector(205, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_pointAndLine_lineTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_LINE))

        val cursorPos = Vector(465, 80)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_pointAndLine_pointTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_LINE))

        val cursorPos = Vector(460, 135)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_pointAndPoint_pointTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_POINT))

        val cursorPos = Vector(460, 135)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_pointAndPolygon_polygonTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_POLYGON))

        val cursorPos = Vector(155, 195)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_pointAndPolygon_pointTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_POLYGON))

        val cursorPos = Vector(167, 132)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_pointAndBar_barTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_BAR))

        val cursorPos = Vector(205, 110)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_pointAndBar_pointTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_BAR))

        val cursorPos = Vector(176, 159)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_boxplotAndPoint_pointTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.BOXPLOT_AND_POINT))

        val cursorPos = Vector(216, 150)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_errorBarVertical() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.ERRORBAR_VERTICAL))

        val cursorPos = Vector(205, 150)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_errorBarHorizontal() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.ERRORBAR_HORIZONTAL))

        val cursorPos = Vector(205, 150)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_crossBarVertical() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.CROSSBAR_VERTICAL))

        val cursorPos = Vector(205, 150)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_crossBarHorizontal() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.CROSSBAR_HORIZONTAL))

        val cursorPos = Vector(205, 150)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_pointRangeVertical() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINTRANGE_VERTICAL))

        val cursorPos = Vector(205, 150)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_pointRangeHorizontal() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINTRANGE_HORIZONTAL))

        val cursorPos = Vector(205, 132)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_lineRangeVertical() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.LINERANGE_VERTICAL))

        val cursorPos = Vector(215, 150)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_lineRangeHorizontal() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.LINERANGE_HORIZONTAL))

        val cursorPos = Vector(205, 132)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_ribbonVertical() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.RIBBON_VERTICAL))

        val cursorPos = Vector(205, 150)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_ribbonHorizontal() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.RIBBON_HORIZONTAL))

        val cursorPos = Vector(246, 150)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_densityVertical() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.DENSITY_VERTICAL))

        val cursorPos = Vector(205, 120)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_densityHorizontal() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.DENSITY_HORIZONTAL))

        val cursorPos = Vector(230, 120)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_lollipopVertical() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.LOLLIPOP_VERTICAL))

        val cursorPos = Vector(215, 96)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_lollipopHorizontal() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.LOLLIPOP_HORIZONTAL))

        val cursorPos = Vector(260, 112)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_pathDistancePriority_implicitLineGroupTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.PATH_DISTANCE_PRIORITY_IMPLICIT_LINE_GROUP))

        val cursorPos = Vector(305, 210)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_pathDistancePriority_separateGroupsLowerTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.PATH_DISTANCE_PRIORITY_SEPARATE_GROUPS))

        val cursorPos = Vector(305, 210)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_pathDistancePriority_separateGroupsUpperTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.PATH_DISTANCE_PRIORITY_SEPARATE_GROUPS))

        val cursorPos = Vector(305, 110)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_pointLineSmooth_linesTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_LINE_SMOOTH))

        val cursorPos = Vector(460, 135)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_pointLineSmooth_pointTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_LINE_SMOOTH))

        val cursorPos = Vector(445, 295)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_histogramDensity_densityTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.HISTOGRAM_DENSITY))

        val cursorPos = Vector(265, 155)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_histogramDensity_histogramTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.HISTOGRAM_DENSITY))

        val cursorPos = Vector(265, 345)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_barPositiveHeight() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.BAR_WITH_NEGATIVE_HEIGHT))

        val cursorPos = Vector(260, 120)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_barPositiveHeightHorizontal() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.BAR_WITH_NEGATIVE_HEIGHT_HORIZONTAL))

        val cursorPos = Vector(290, 90)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_barNegativeHeight() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.BAR_WITH_NEGATIVE_HEIGHT))

        val cursorPos = Vector(180, 220)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_barNegativeHeightHorizontal() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.BAR_WITH_NEGATIVE_HEIGHT_HORIZONTAL))

        val cursorPos = Vector(140, 140)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_polygonOverlapped() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POLYGON_OVERLAPPED))

        val cursorPos = Vector(175, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_barOverlapped() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.BAR_OVERLAPPED))

        val cursorPos = Vector(175, 165)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_rectOverlapped() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.RECT_OVERLAPPED))

        val cursorPos = Vector(175, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_pointAndPoint_withCrosshair() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_POINT_WITH_CROSSHAIR))

        val cursorPos = Vector(235, 165)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_pointAndPoint_withCrosshair_overlapNearerPointTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_POINT_WITH_CROSSHAIR_OVERLAP))

        val cursorPos = Vector(331, 191)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_pointAndText_pointTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_TEXT))

        val cursorPos = Vector(205, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_pointAndLabel_pointTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_LABEL))

        val cursorPos = Vector(205, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_groupedLine_closestByXTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.GROUPED_LINE_CLOSEST_BY_X))

        val cursorPos = Vector(205, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_groupedLineAndPoint_lineTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.GROUPED_LINE_AND_POINT))

        val cursorPos = Vector(205, 205)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_groupedLineAndPoint_pointTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.GROUPED_LINE_AND_POINT))

        val cursorPos = Vector(205, 157)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_logicalGroup_differentXAxisTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.LOGICAL_GROUP_DIFFERENT_X_TOOLTIP))

        val cursorPos = Vector(205, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_logicalGroup_differentXAxisTooltip_closerToB() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.LOGICAL_GROUP_DIFFERENT_X_TOOLTIP_REVERSED_SIDES))

        val cursorPos = Vector(103, 245)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_logicalGroup_differentXAxisTooltip_closerToA() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.LOGICAL_GROUP_DIFFERENT_X_TOOLTIP_REVERSED_SIDES))

        val cursorPos = Vector(80, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_barOverlappedMany_singleTooltip() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.BAR_OVERLAPPED_MANY))

        val cursorPos = Vector(205, 75)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }

    @Test
    fun plot_tooltips_pointRangeNearest() {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.ANCHOR_FOR_RECT_LIKE_GEOM))

        val cursorPos = Vector(205, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        assertBitmap(plotCanvasDrawable, cursorPos)
    }
}
