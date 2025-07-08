import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import org.jetbrains.letsPlot.imagick.canvas.MagickFontManager

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

open class CanvasTestBase {
    companion object {
        // Lazy in a companion to prevent multiple initializations caused by JUnit test runner
        private val embeddedFontsManager by lazy { embeddedFontsManager() }
        private val imageComparer by lazy { createImageComparer(embeddedFontsManager) }
    }

    fun assertCanvas(expectedFileName: String, canvas: MagickCanvas) {
        imageComparer.assertBitmapEquals(expectedFileName, canvas.takeSnapshot().bitmap)
    }


    fun createCanvas(width: Number = 100, height: Number = 100, pixelDensity: Double = 1.0, fontManager: MagickFontManager = embeddedFontsManager): Pair<MagickCanvas, Context2d> {
        val canvas = MagickCanvas.create(width = width, height = height, pixelDensity = pixelDensity, fontManager = fontManager)
        val context2d = canvas.context2d
        return canvas to context2d
    }
}
