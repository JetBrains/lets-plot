package org.jetbrains.letsPlot.pythonExtension.interop

import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasPeer
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import org.jetbrains.letsPlot.visualtesting.plot.AllPlotTests
import kotlin.test.Test

class MagickAllPlotTests {
    companion object {
        private val embeddedFontsManager by lazy { newEmbeddedFontsManager() }
    }

    @Test
    fun runAllPlotTests() {
        val canvasPeer = MagickCanvasPeer(pixelDensity = 1.0, fontManager = embeddedFontsManager)

        val imageComparer = ImageComparer(canvasPeer, NativeBitmapIO, silent = true)

        AllPlotTests.runAllTests(canvasPeer, imageComparer)
    }

}