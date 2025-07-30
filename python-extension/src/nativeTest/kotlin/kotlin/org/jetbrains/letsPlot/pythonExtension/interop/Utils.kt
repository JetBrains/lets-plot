/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.pythonExtension.interop

import demoAndTestShared.ImageComparer
import demoAndTestShared.NativeBitmapIO
import org.jetbrains.letsPlot.commons.intern.io.Native
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasProvider
import org.jetbrains.letsPlot.imagick.canvas.MagickFontManager

val fontsDir = Native.getCurrentDir() + "/src/nativeTest/resources/fonts/"

fun newEmbeddedFontsManager() = MagickFontManager.configured(
    "sans" to MagickFontManager.FontSet("NotoSans",
        regularFontPath = "$fontsDir/NotoSans-Regular.ttf",
        boldFontPath = "$fontsDir/NotoSans-Bold.ttf",
        italicFontPath = "$fontsDir/NotoSans-Italic.ttf",
        boldItalicFontPath = "$fontsDir/NotoSans-BoldItalic.ttf"
    ),
    "sans-serif" to MagickFontManager.FontSet("NotoSans",
        regularFontPath = "$fontsDir/NotoSans-Regular.ttf",
        boldFontPath = "$fontsDir/NotoSans-Bold.ttf",
        italicFontPath = "$fontsDir/NotoSans-Italic.ttf",
        boldItalicFontPath = "$fontsDir/NotoSans-BoldItalic.ttf"
    ),
    "serif" to MagickFontManager.FontSet("NotoSerif",
        regularFontPath = "$fontsDir/NotoSerif-Regular.ttf",
        boldFontPath = "$fontsDir/NotoSerif-Bold.ttf",
        italicFontPath = "$fontsDir/NotoSerif-Italic.ttf",
        boldItalicFontPath = "$fontsDir/NotoSerif-BoldItalic.ttf"
    ),
    "mono" to MagickFontManager.FontSet("NotoSansMono",
        regularFontPath = "$fontsDir/NotoSansMono-Regular.ttf",
        boldFontPath = "$fontsDir/NotoSansMono-Bold.ttf"
    ),
    "regular_mono" to MagickFontManager.FontSet("NotoSansMono",
        regularFontPath = "$fontsDir/NotoSansMono-Regular.ttf"
    ),
)

fun createImageComparer(fontManager: MagickFontManager): ImageComparer {
    return ImageComparer(
        expectedDir = Native.getCurrentDir() + "/src/nativeTest/resources/expected/",
        outDir = Native.getCurrentDir() + "/build/reports/",
        canvasProvider = MagickCanvasProvider(fontManager),
        bitmapIO = NativeBitmapIO,
        tol = 1
    )
}
