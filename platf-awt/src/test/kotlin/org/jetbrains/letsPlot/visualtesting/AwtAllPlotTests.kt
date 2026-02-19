package org.jetbrains.letsPlot.visualtesting

import org.jetbrains.letsPlot.awt.NotoFontManager
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasPeer
import org.jetbrains.letsPlot.visualtesting.plot.AllPlotTests
import kotlin.test.Test

class AwtAllPlotTests {

    @Test
    fun runAllPlotTests() {
        val canvasPeer = AwtCanvasPeer(fontManager = NotoFontManager.INSTANCE)
        val awtBitmapIO = AwtBitmapIO(
            expectedImagesDir = "/src/test/resources/expected-images/plot",
            outputDir = "/build/reports/actual-images/plot"
        )

        val imageComparer = ImageComparer(canvasPeer, awtBitmapIO, silent = true)

        AllPlotTests.runAllTests(canvasPeer, imageComparer)
    }
}
