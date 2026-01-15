package org.jetbrains.letsPlot.visualtesting.canvas

import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.visualtesting.ImageComparer

object CanvasTck {
    fun runAllTests(canvasPeer: CanvasPeer, imageComparer: ImageComparer) {
        var failedTestsCount = 0

        failedTestsCount += CanvasClipTest(canvasPeer, imageComparer).runTests()
        failedTestsCount += CanvasPathTest(canvasPeer, imageComparer).runTests()
        failedTestsCount += CanvasDrawImageTest(canvasPeer, imageComparer).runTests()
        failedTestsCount += CanvasTextTest(canvasPeer, imageComparer).runTests()

        if (failedTestsCount > 0) {
            error("$failedTestsCount tests failed!")
        }
    }
}
