package org.jetbraibs.letsPlot.visualtesting

import org.jetbrains.letsPlot.commons.intern.io.Native
import org.jetbrains.letsPlot.imagick.canvas.MagickFontManager

object MagickFontManager {
    val fontsDir = Native.getCurrentDir() + "/src/nativeTest/resources/fonts/"

    fun newEmbeddedFontsManager() = MagickFontManager.configured(
        "Noto Sans" to MagickFontManager.FontSet(embedded = true, "NotoSans",
            regularFontPath = "$fontsDir/NotoSans-Regular.ttf",
            boldFontPath = "$fontsDir/NotoSans-Bold.ttf",
            italicFontPath = "$fontsDir/NotoSans-Italic.ttf",
            boldItalicFontPath = "$fontsDir/NotoSans-BoldItalic.ttf"
        ),
        "Noto Serif" to MagickFontManager.FontSet(embedded = true, "NotoSerif",
            regularFontPath = "$fontsDir/NotoSerif-Regular.ttf",
            boldFontPath = "$fontsDir/NotoSerif-Bold.ttf",
            italicFontPath = "$fontsDir/NotoSerif-Italic.ttf",
            boldItalicFontPath = "$fontsDir/NotoSerif-BoldItalic.ttf"
        ),
        // Monospace font without italic and bold for testing faux styles
        "Noto Sans Mono" to MagickFontManager.FontSet(embedded = true, "NotoSansMono",
            regularFontPath = "$fontsDir/NotoSansMono-Regular.ttf"
        ),

        "sans" to MagickFontManager.FontSet(embedded = true, "NotoSans",
            regularFontPath = "$fontsDir/NotoSans-Regular.ttf",
            boldFontPath = "$fontsDir/NotoSans-Bold.ttf",
            italicFontPath = "$fontsDir/NotoSans-Italic.ttf",
            boldItalicFontPath = "$fontsDir/NotoSans-BoldItalic.ttf"
        ),
        "sans-serif" to MagickFontManager.FontSet(embedded = true, "NotoSans",
            regularFontPath = "$fontsDir/NotoSans-Regular.ttf",
            boldFontPath = "$fontsDir/NotoSans-Bold.ttf",
            italicFontPath = "$fontsDir/NotoSans-Italic.ttf",
            boldItalicFontPath = "$fontsDir/NotoSans-BoldItalic.ttf"
        ),
        "serif" to MagickFontManager.FontSet(embedded = true, "NotoSerif",
            regularFontPath = "$fontsDir/NotoSerif-Regular.ttf",
            boldFontPath = "$fontsDir/NotoSerif-Bold.ttf",
            italicFontPath = "$fontsDir/NotoSerif-Italic.ttf",
            boldItalicFontPath = "$fontsDir/NotoSerif-BoldItalic.ttf"
        ),
        "mono" to MagickFontManager.FontSet(embedded = true, "NotoSansMono",
            regularFontPath = "$fontsDir/NotoSansMono-Regular.ttf",
            boldFontPath = "$fontsDir/NotoSansMono-Bold.ttf"
        ),
        "regular_mono" to MagickFontManager.FontSet(embedded = true, "NotoSansMono",
            regularFontPath = "$fontsDir/NotoSansMono-Regular.ttf"
        ),
        "oblique_bold" to MagickFontManager.FontSet(embedded = true, "Noto",
            regularFontPath = "$fontsDir/NotoSans-Regular.ttf",
            boldFontPath = "$fontsDir/NotoSans-Bold.ttf",
            obliqueFontPath = "$fontsDir/NotoSans-Italic.ttf",
            boldObliqueFontPath = "$fontsDir/NotoSans-BoldItalic.ttf"
        ),
        "oblique" to MagickFontManager.FontSet(embedded = true, "Noto",
            regularFontPath = "$fontsDir/NotoSans-Regular.ttf",
            obliqueFontPath = "$fontsDir/NotoSans-Italic.ttf"
        ),
    )
}
