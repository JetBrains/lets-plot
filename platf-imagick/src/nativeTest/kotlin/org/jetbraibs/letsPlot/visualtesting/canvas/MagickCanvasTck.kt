package org.jetbraibs.letsPlot.visualtesting.canvas

import org.jetbraibs.letsPlot.visualtesting.MagickFontManager.newEmbeddedFontsManager
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasPeer
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import org.jetbrains.letsPlot.visualtesting.NativeBitmapIO
import org.jetbrains.letsPlot.visualtesting.canvas.AllCanvasTests
import kotlin.test.Test


class MagickCanvasTck {
    @Test
    fun runAllCanvasTests() {
        val canvasPeer = MagickCanvasPeer(pixelDensity = 1.0, newEmbeddedFontsManager())
        val bitmapIO = NativeBitmapIO(
            expectedImagesDir = "/src/nativeTest/resources/expected-images/canvas",
            outputDir = "/build/reports/actual-images/canvas"
        )
        val imageComparer = ImageComparer(canvasPeer, bitmapIO, silent = true)

        AllCanvasTests.runAllTests(canvasPeer, imageComparer)
    }
}

