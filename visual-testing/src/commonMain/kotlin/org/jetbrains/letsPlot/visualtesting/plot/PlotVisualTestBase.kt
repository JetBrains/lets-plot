package org.jetbrains.letsPlot.visualtesting.plot

import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.raster.view.PlotCanvasDrawable
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import org.jetbrains.letsPlot.visualtesting.VisualTestBase

abstract class PlotVisualTestBase: VisualTestBase() {
    private val plotHelper by lazy { PlotHelper(canvasPeer = canvasPeer) }

    // For regular @Test-annotated tests
    protected open fun currentTestName(): String? = null

    fun createPlot(
        plotSpec: MutableMap<String, Any?>,
        width: Number? = null,
        height: Number? = null,
        renderingHints: Map<Any, Any> = emptyMap()
    ): PlotCanvasDrawable {
        return plotHelper.createPlot(plotSpec, width, height, renderingHints)
    }

    fun assertBitmap(plotCanvasDrawable: PlotCanvasDrawable, cursorPos: Vector? = null, profile: ImageComparer.ComparisonProfile? = null) {
        val actual = plotHelper.paint(plotCanvasDrawable, cursorPos)
        assertImage(actual, currentTestName() ?: "UnnamedTest", profile)
    }
}