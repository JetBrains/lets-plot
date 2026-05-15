/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.visualtesting

import org.jetbrains.letsPlot.awt.NotoFontManager
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasPeer
import org.jetbrains.letsPlot.visualtesting.plot.AllPlotTests
import org.jetbrains.letsPlot.visualtesting.plot.PlotInteractivityTest
import org.jetbrains.letsPlot.visualtesting.plot.PlotLatexAwtTest
import org.jetbrains.letsPlot.visualtesting.plot.PlotThemeAwtTest
import kotlin.test.Test

class AwtAllPlotTests {
    val canvasPeer = AwtCanvasPeer(fontManager = NotoFontManager.INSTANCE)
    val awtBitmapIO = AwtBitmapIO(subdir = "visual-testing/plot")

    val imageComparer = ImageComparer(canvasPeer, awtBitmapIO, silent = true)

    @Test
    fun runAllPlotTests() {
        AllPlotTests.runAllTests(canvasPeer, imageComparer)
    }

    @Test
    fun runAwtOnlyPlotTests() {
        var failedTestsCount = 0

        failedTestsCount += PlotLatexAwtTest(canvasPeer, imageComparer).runTests()
        failedTestsCount += PlotThemeAwtTest(canvasPeer, imageComparer).runTests()

        if (failedTestsCount > 0) {
            error("$failedTestsCount tests failed!")
        }
    }

    @Test
    fun runSinglePlotTest() {
        val testSuit = PlotInteractivityTest(canvasPeer, imageComparer)
        testSuit.assertTest(testSuit::plot_interactivity_nestedComposite_tooltip)
    }
}
