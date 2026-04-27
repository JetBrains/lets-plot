/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.pythonExtension.interop

import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasPeer
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import org.jetbrains.letsPlot.visualtesting.NativeBitmapIO
import org.jetbrains.letsPlot.visualtesting.plot.AllPlotTests
import kotlin.test.Test

class MagickAllPlotTests {
    companion object {
        private val embeddedFontsManager by lazy { newEmbeddedFontsManager() }
    }

    @Test
    fun runAllPlotTests() {
        val canvasPeer = MagickCanvasPeer(pixelDensity = 1.0, fontManager = embeddedFontsManager)
        val imageComparer = ImageComparer(canvasPeer, NativeBitmapIO(subdir = "visual-testing/plot"), silent = true)

        AllPlotTests.runAllTests(canvasPeer, imageComparer)
    }

}
