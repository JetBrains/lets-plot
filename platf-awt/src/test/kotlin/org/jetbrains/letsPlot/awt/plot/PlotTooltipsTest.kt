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
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.visualtesting.AwtBitmapIO
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import org.jetbrains.letsPlot.visualtesting.plot.PlotTestBase
import kotlin.test.Test

class PlotTooltipsTest : PlotTestBase() {
    override val canvasPeer: CanvasPeer = AwtCanvasPeer(fontManager = NotoFontManager.INSTANCE)
    override val imageComparer: ImageComparer = ImageComparer(canvasPeer, AwtBitmapIO(), silent = true)

    init {
        registerTest(::plot_tooltips_pointAndLine_lineTooltip)
        registerTest(::plot_tooltips_pointAndLine_pointTooltip)
        registerTest(::plot_tooltips_pointAndPoint_pointTooltip)
        registerTest(::plot_tooltips_pointAndPolygon_polygonTooltip)
        registerTest(::plot_tooltips_pointAndPolygon_pointTooltip)
        registerTest(::plot_tooltips_pointAndBar_barTooltip)
        registerTest(::plot_tooltips_pointAndBar_pointTooltip)
        registerTest(::plot_tooltips_boxplotAndPoint_pointTooltip)
        registerTest(::plot_tooltips_errorBarVertical)
        registerTest(::plot_tooltips_errorBarHorizontal)
        registerTest(::plot_tooltips_crossBarVertical)
        registerTest(::plot_tooltips_crossBarHorizontal)
        registerTest(::plot_tooltips_pointRangeVertical)
        registerTest(::plot_tooltips_pointRangeHorizontal)
        registerTest(::plot_tooltips_lineRangeVertical)
        registerTest(::plot_tooltips_lineRangeHorizontal)
        registerTest(::plot_tooltips_ribbonVertical)
        registerTest(::plot_tooltips_ribbonHorizontal)
        registerTest(::plot_tooltips_densityVertical)
        registerTest(::plot_tooltips_densityHorizontal)
        registerTest(::plot_tooltips_lollipopVertical)
        registerTest(::plot_tooltips_lollipopHorizontal)
        registerTest(::plot_tooltips_pathDistancePriority_implicitLineGroupTooltip)
        registerTest(::plot_tooltips_pathDistancePriority_separateGroupsLowerTooltip)
        registerTest(::plot_tooltips_pathDistancePriority_separateGroupsUpperTooltip)
        registerTest(::plot_tooltips_pointLineSmooth_linesTooltip)
        registerTest(::plot_tooltips_pointLineSmooth_pointTooltip)
        registerTest(::plot_tooltips_histogramDensity_densityTooltip)
        registerTest(::plot_tooltips_histogramDensity_histogramTooltip)
        registerTest(::plot_tooltips_barPositiveHeight)
        registerTest(::plot_tooltips_barPositiveHeightHorizontal)
        registerTest(::plot_tooltips_barNegativeHeight)
        registerTest(::plot_tooltips_barNegativeHeightHorizontal)
        registerTest(::plot_tooltips_polygonOverlapped)
        registerTest(::plot_tooltips_barOverlapped)
        registerTest(::plot_tooltips_rectOverlapped)
        registerTest(::plot_tooltips_pointAndPoint_withCrosshair)
        registerTest(::plot_tooltips_pointAndPoint_withCrosshair_overlapNearerPointTooltip)
        registerTest(::plot_tooltips_pointAndText_pointTooltip)
        registerTest(::plot_tooltips_pointAndLabel_pointTooltip)
        registerTest(::plot_tooltips_groupedLine_closestByXTooltip)
        registerTest(::plot_tooltips_groupedLineAndPoint_lineTooltip)
        registerTest(::plot_tooltips_groupedLineAndPoint_pointTooltip)
        registerTest(::plot_tooltips_logicalGroup_differentXAxisTooltip)
        registerTest(::plot_tooltips_logicalGroup_differentXAxisTooltip_closerToA)
        registerTest(::plot_tooltips_logicalGroup_differentXAxisTooltip_closerToB)
        registerTest(::plot_tooltips_barOverlappedMany_singleTooltip)
    }

    @Test
    fun runAllTests() {
        val failedTestsCount = runTests()
        if (failedTestsCount > 0) {
            error("$failedTestsCount tests failed!")
        }
    }

    @Test
    fun runSingleTest() {
        val testSuit = PlotTooltipsTest()
        testSuit.assertTest(testSuit::plot_tooltips_logicalGroup_differentXAxisTooltip_closerToB)
    }

    fun plot_tooltips_pointAndLine_lineTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_LINE))

        val cursorPos = Vector(465, 80)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_pointAndLine_pointTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_LINE))

        val cursorPos = Vector(460, 135)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_pointAndPoint_pointTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_POINT))

        val cursorPos = Vector(460, 135)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_pointAndPolygon_polygonTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_POLYGON))

        val cursorPos = Vector(155, 195)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_pointAndPolygon_pointTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_POLYGON))

        val cursorPos = Vector(167, 132)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_pointAndBar_barTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_BAR))

        val cursorPos = Vector(205, 110)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_pointAndBar_pointTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_BAR))

        val cursorPos = Vector(176, 159)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_boxplotAndPoint_pointTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.BOXPLOT_AND_POINT))

        val cursorPos = Vector(216, 150)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_errorBarVertical(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.ERRORBAR_VERTICAL))

        val cursorPos = Vector(205, 150)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_errorBarHorizontal(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.ERRORBAR_HORIZONTAL))

        val cursorPos = Vector(205, 150)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_crossBarVertical(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.CROSSBAR_VERTICAL))

        val cursorPos = Vector(205, 150)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_crossBarHorizontal(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.CROSSBAR_HORIZONTAL))

        val cursorPos = Vector(205, 150)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_pointRangeVertical(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINTRANGE_VERTICAL))

        val cursorPos = Vector(205, 150)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_pointRangeHorizontal(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINTRANGE_HORIZONTAL))

        val cursorPos = Vector(205, 132)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_lineRangeVertical(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.LINERANGE_VERTICAL))

        val cursorPos = Vector(215, 150)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_lineRangeHorizontal(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.LINERANGE_HORIZONTAL))

        val cursorPos = Vector(205, 132)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_ribbonVertical(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.RIBBON_VERTICAL))

        val cursorPos = Vector(205, 150)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_ribbonHorizontal(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.RIBBON_HORIZONTAL))

        val cursorPos = Vector(246, 150)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_densityVertical(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.DENSITY_VERTICAL))

        val cursorPos = Vector(205, 120)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_densityHorizontal(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.DENSITY_HORIZONTAL))

        val cursorPos = Vector(230, 120)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_lollipopVertical(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.LOLLIPOP_VERTICAL))

        val cursorPos = Vector(215, 96)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_lollipopHorizontal(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.LOLLIPOP_HORIZONTAL))

        val cursorPos = Vector(260, 112)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_pathDistancePriority_implicitLineGroupTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.PATH_DISTANCE_PRIORITY_IMPLICIT_LINE_GROUP))

        val cursorPos = Vector(305, 210)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_pathDistancePriority_separateGroupsLowerTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.PATH_DISTANCE_PRIORITY_SEPARATE_GROUPS))

        val cursorPos = Vector(305, 210)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_pathDistancePriority_separateGroupsUpperTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.PATH_DISTANCE_PRIORITY_SEPARATE_GROUPS))

        val cursorPos = Vector(305, 110)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_pointLineSmooth_linesTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_LINE_SMOOTH))

        val cursorPos = Vector(460, 135)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_pointLineSmooth_pointTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_LINE_SMOOTH))

        val cursorPos = Vector(445, 295)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_histogramDensity_densityTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.HISTOGRAM_DENSITY))

        val cursorPos = Vector(265, 155)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_histogramDensity_histogramTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.HISTOGRAM_DENSITY))

        val cursorPos = Vector(265, 345)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_barPositiveHeight(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.BAR_WITH_NEGATIVE_HEIGHT))

        val cursorPos = Vector(260, 120)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_barPositiveHeightHorizontal(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.BAR_WITH_NEGATIVE_HEIGHT_HORIZONTAL))

        val cursorPos = Vector(290, 90)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_barNegativeHeight(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.BAR_WITH_NEGATIVE_HEIGHT))

        val cursorPos = Vector(180, 220)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_barNegativeHeightHorizontal(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.BAR_WITH_NEGATIVE_HEIGHT_HORIZONTAL))

        val cursorPos = Vector(140, 140)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_polygonOverlapped(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POLYGON_OVERLAPPED))

        val cursorPos = Vector(175, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_barOverlapped(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.BAR_OVERLAPPED))

        val cursorPos = Vector(175, 165)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_rectOverlapped(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.RECT_OVERLAPPED))

        val cursorPos = Vector(175, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_pointAndPoint_withCrosshair(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_POINT_WITH_CROSSHAIR))

        val cursorPos = Vector(235, 165)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_pointAndPoint_withCrosshair_overlapNearerPointTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_POINT_WITH_CROSSHAIR_OVERLAP))

        val cursorPos = Vector(331, 191)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_pointAndText_pointTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_TEXT))

        val cursorPos = Vector(205, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_pointAndLabel_pointTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.POINT_AND_LABEL))

        val cursorPos = Vector(205, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_groupedLine_closestByXTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.GROUPED_LINE_CLOSEST_BY_X))

        val cursorPos = Vector(205, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_groupedLineAndPoint_lineTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.GROUPED_LINE_AND_POINT))

        val cursorPos = Vector(205, 205)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_groupedLineAndPoint_pointTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.GROUPED_LINE_AND_POINT))

        val cursorPos = Vector(205, 157)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_logicalGroup_differentXAxisTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.LOGICAL_GROUP_DIFFERENT_X_TOOLTIP))

        val cursorPos = Vector(205, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_logicalGroup_differentXAxisTooltip_closerToB(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.LOGICAL_GROUP_DIFFERENT_X_TOOLTIP_REVERSED_SIDES))

        val cursorPos = Vector(103, 245)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_logicalGroup_differentXAxisTooltip_closerToA(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.LOGICAL_GROUP_DIFFERENT_X_TOOLTIP_REVERSED_SIDES))

        val cursorPos = Vector(80, 145)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

    fun plot_tooltips_barOverlappedMany_singleTooltip(): Bitmap {
        val plotCanvasDrawable = createPlot(parseJson(PlotTooltipsSpecs.BAR_OVERLAPPED_MANY))

        val cursorPos = Vector(205, 75)
        plotCanvasDrawable.mouseEventPeer.dispatch(MOUSE_MOVED, noButton(cursorPos))

        return paint(plotCanvasDrawable, cursorPos)
    }

}
