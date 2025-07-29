/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.pythonExtension.interop

import demoAndTestShared.ImageComparer
import demoAndTestShared.NativeBitmapIO
import org.jetbrains.letsPlot.commons.intern.io.Native
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.core.canvas.FontStyle
import org.jetbrains.letsPlot.core.canvas.FontWeight
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasProvider
import org.jetbrains.letsPlot.imagick.canvas.MagickFontManager

fun newEmbeddedFontsManager() = MagickFontManager().apply {
    val fontsDir = Native.getCurrentDir() + "/src/nativeTest/resources/fonts/"

    registerFont(Font(fontFamily = "sans"), "$fontsDir/NotoSans-Regular.ttf")
    registerFont(Font(fontFamily = "sans", fontWeight = FontWeight.BOLD), "$fontsDir/NotoSans-Bold.ttf")
    registerFont(Font(fontFamily = "sans", fontStyle = FontStyle.ITALIC), "$fontsDir/NotoSans-Italic.ttf")
    registerFont(Font(fontFamily = "sans", fontWeight = FontWeight.BOLD, fontStyle = FontStyle.ITALIC), "$fontsDir/NotoSans-BoldItalic.ttf")

    registerFont(Font(fontFamily = "sans-serif"), "$fontsDir/NotoSans-Regular.ttf")
    registerFont(Font(fontFamily = "sans-serif", fontWeight = FontWeight.BOLD), "$fontsDir/NotoSans-Bold.ttf")
    registerFont(Font(fontFamily = "sans-serif", fontStyle = FontStyle.ITALIC), "$fontsDir/NotoSans-Italic.ttf")
    registerFont(Font(fontFamily = "sans-serif", fontWeight = FontWeight.BOLD, fontStyle = FontStyle.ITALIC), "$fontsDir/NotoSans-BoldItalic.ttf")

    registerFont(Font(fontFamily = "serif"), "$fontsDir/NotoSerif-Regular.ttf")
    registerFont(Font(fontFamily = "serif", fontWeight = FontWeight.BOLD), "$fontsDir/NotoSerif-Bold.ttf")
    registerFont(Font(fontFamily = "serif", fontWeight = FontWeight.BOLD, fontStyle = FontStyle.ITALIC), "$fontsDir/NotoSerif-BoldItalic.ttf")
    registerFont(Font(fontFamily = "serif", fontStyle = FontStyle.ITALIC), "$fontsDir/NotoSerif-Italic.ttf")

    registerFont(Font(fontFamily = "monospace", fontWeight = FontWeight.BOLD), "$fontsDir/NotoSansMono-Bold.ttf")
    registerFont(Font(fontFamily = "monospace"), "$fontsDir/NotoSansMono-Regular.ttf")
}

fun createImageComparer(fontManager: MagickFontManager): ImageComparer {
    return ImageComparer(
        expectedDir = Native.getCurrentDir() + "/src/nativeTest/resources/expected/",
        outDir = Native.getCurrentDir() + "/build/reports/",
        canvasProvider = MagickCanvasProvider(fontManager),
        bitmapIO = NativeBitmapIO,
        tol = 1
    )
}
