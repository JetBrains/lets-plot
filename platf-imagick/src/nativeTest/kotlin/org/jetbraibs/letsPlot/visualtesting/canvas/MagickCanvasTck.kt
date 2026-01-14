package org.jetbraibs.letsPlot.visualtesting.canvas

import org.jetbraibs.letsPlot.visualtesting.MagickFontManager.newEmbeddedFontsManager
import org.jetbraibs.letsPlot.visualtesting.NativeBitmapIO
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasPeer
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import org.jetbrains.letsPlot.visualtesting.canvas.CanvasTck
import kotlin.test.Test


class MagickCanvasTck {
    @Test
    fun runAllCanvasTests() {
        val canvasPeer = MagickCanvasPeer(pixelDensity = 1.0, newEmbeddedFontsManager())
        val imageComparer = ImageComparer(canvasPeer, NativeBitmapIO, silent = true)

        CanvasTck.runAllTests(canvasPeer, imageComparer)
    }
}

