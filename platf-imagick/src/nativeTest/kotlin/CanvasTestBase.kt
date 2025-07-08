import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas

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
}
