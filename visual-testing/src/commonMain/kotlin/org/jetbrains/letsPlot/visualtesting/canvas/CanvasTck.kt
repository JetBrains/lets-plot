package org.jetbrains.letsPlot.visualtesting.canvas

import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.visualtesting.ImageComparer

object CanvasTck {
    fun runAllTests(canvasPeer: CanvasPeer, imageComparer: ImageComparer) {
        CanvasClipTest(canvasPeer, imageComparer).runTests()
        CanvasPathTest(canvasPeer, imageComparer).runTests()
        CanvasDrawImageTest(canvasPeer, imageComparer).runTests()
        CanvasTextTest(canvasPeer, imageComparer).runTests()
    }
}
