/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import org.jetbrains.letsPlot.awt.NotoFontManager
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasPeer
import org.jetbrains.letsPlot.visualtesting.AwtBitmapIO
import org.jetbrains.letsPlot.visualtesting.ImageComparer

open class VisualPlotTestBase(
    private val expectedImagesSubdir: String = ""
) {

    private fun createImageComparer(): ImageComparer {
        val canvasPeer = AwtCanvasPeer(fontManager = NotoFontManager.INSTANCE)
        val awtBitmapIO = AwtBitmapIO(subdir = expectedImagesSubdir)
        return ImageComparer(canvasPeer, awtBitmapIO, silent = true)
    }

    protected val imageComparer by lazy { createImageComparer() }
}
