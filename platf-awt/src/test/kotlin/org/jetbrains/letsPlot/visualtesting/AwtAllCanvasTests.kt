package org.jetbrains.letsPlot.visualtesting

import org.jetbrains.letsPlot.awt.NotoFontManager
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasPeer
import org.jetbrains.letsPlot.visualtesting.canvas.AllCanvasTests
import kotlin.test.Test


class AwtAllCanvasTests {

    @Test
    fun runAllCanvasTests() {
        val canvasPeer = AwtCanvasPeer(fontManager = NotoFontManager.INSTANCE)
        val awtBitmapIO = AwtBitmapIO(subdir = "/canvas")
        val imageComparer = ImageComparer(canvasPeer, awtBitmapIO, silent = true)

        AllCanvasTests.runAllTests(canvasPeer, imageComparer)
    }
}
