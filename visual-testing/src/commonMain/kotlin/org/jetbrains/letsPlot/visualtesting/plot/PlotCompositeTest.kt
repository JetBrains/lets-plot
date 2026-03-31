@file:Suppress("FunctionName")

package org.jetbrains.letsPlot.visualtesting.plot

import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.visualtesting.ImageComparer

internal class PlotCompositeTest(
    override val canvasPeer: CanvasPeer,
    override val imageComparer: ImageComparer,
) : PlotTestBase() {

    init {
        registerTest(::plot_composite_nested)
    }

    private fun plot_composite_nested(): Bitmap {
        return paint(parseJson(PlotSpecs.COMPOSITE_NESTED))
    }
}