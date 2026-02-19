package org.jetbrains.letsPlot.visualtesting

import org.jetbrains.letsPlot.awt.NotoFontManager
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasPeer
import org.jetbrains.letsPlot.visualtesting.canvas.AllCanvasTests
import java.awt.Font
import kotlin.test.Test

typealias AwtFont = Font

class AwtAllCanvasTests {

    @Test
    fun runAllCanvasTests() {
        val canvasPeer = AwtCanvasPeer(fontManager = NotoFontManager.INSTANCE)
        val imageComparer = ImageComparer(canvasPeer, AwtBitmapIO, silent = true)

        AllCanvasTests.runAllTests(canvasPeer, imageComparer)
    }
}
