package org.jetbrains.letsPlot.visualtesting

import org.jetbrains.letsPlot.awt.NotoFontManager
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasPeer
import org.jetbrains.letsPlot.visualtesting.plot.AllPlotTests
import kotlin.test.Test

class AwtAllPlotTests {

    @Test
    fun runAllPlotTests() {
        val canvasPeer = AwtCanvasPeer(fontManager = NotoFontManager.INSTANCE)
        val imageComparer = ImageComparer(canvasPeer, AwtBitmapIO, silent = true)

        AllPlotTests.runAllTests(canvasPeer, imageComparer)
    }
}
