package org.jetbrains.letsPlot.visualtesting

import org.jetbrains.letsPlot.awt.NotoFontManager
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasPeer
import org.jetbrains.letsPlot.visualtesting.canvas.CanvasTck
import java.awt.Font
import kotlin.test.Test

typealias AwtFont = Font

class AwtCanvasTck {

    @Test
    fun runAllCanvasClipTests() {
        val canvasPeer = AwtCanvasPeer(fontManager = NotoFontManager.INSTANCE)
        val imageComparer = ImageComparer(canvasPeer, AwtBitmapIO, silent = true)

        CanvasTck.runAllTests(canvasPeer, imageComparer)
    }
}
