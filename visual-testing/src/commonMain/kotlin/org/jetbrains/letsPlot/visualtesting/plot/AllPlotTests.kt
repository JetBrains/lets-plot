/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.visualtesting.plot

import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.visualtesting.ImageComparer

object AllPlotTests {
    fun runAllTests(canvasPeer: CanvasPeer, imageComparer: ImageComparer) {
        var failedTestsCount = 0

        failedTestsCount += PlotCompositeTest(canvasPeer, imageComparer).runTests()
        failedTestsCount += PlotInteractivityTest(canvasPeer, imageComparer).runTests()
        failedTestsCount += PlotThemeTest(canvasPeer, imageComparer).runTests()
        //failedTestsCount += PlotAxisTest().runTests()
        //failedTestsCount += PlotFacetTest().runTests()
        //failedTestsCount += PlotThemeTest().runTests()

        if (failedTestsCount > 0) {
            error("$failedTestsCount tests failed!")
        }
    }
}