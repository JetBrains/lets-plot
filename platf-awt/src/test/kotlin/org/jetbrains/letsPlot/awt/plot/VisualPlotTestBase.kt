/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import demoAndTestShared.AwtBitmapIO
import demoAndTestShared.AwtTestCanvasProvider
import demoAndTestShared.ImageComparer
import kotlinx.coroutines.runBlocking
import org.jetbrains.letsPlot.awt.canvas.FontManager
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.awt.BitmapUtil
import org.jetbrains.letsPlot.core.canvas.FontStyle
import org.jetbrains.letsPlot.core.canvas.FontStyle.ITALIC
import org.jetbrains.letsPlot.core.canvas.FontWeight
import org.jetbrains.letsPlot.core.canvas.FontWeight.BOLD
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit
import org.jetbrains.letsPlot.visualtesting.AwtCanvasTck
import org.jetbrains.letsPlot.visualtesting.AwtFont
import java.io.IOException
import javax.imageio.ImageIO

open class VisualPlotTestBase {

    protected fun MutableMap<String, Any>.themeTextNotoSans(): MutableMap<String, Any> {
        val theme = getMap("theme") ?: emptyMap()
        this[Option.Plot.THEME] =  theme + mapOf(
            "text" to mapOf(
                "blank" to false,
                "family" to "Noto Sans"
            ),
            "axis_title_y" to mapOf(
                "blank" to true // hide rotated text - antialiasing may cause image differences
            )
        )
        return this
    }

    private fun createImageComparer(): ImageComparer {
        return ImageComparer(
            tol = 5,
            canvasProvider = AwtTestCanvasProvider(),
            bitmapIO = AwtBitmapIO,
            expectedDir = System.getProperty("user.dir") + "/src/test/resources/expected-images/",
            outDir = System.getProperty("user.dir") + "/build/reports/"
        )
    }

    protected val imageComparer by lazy { createImageComparer() }

    fun assertPlot(
        expectedFileName: String,
        plotSpec: MutableMap<String, Any>,
        width: Number? = null,
        height: Number? = null,
        unit: SizeUnit? = null,
        dpi: Number? = null,
        scale: Number? = null,
        fontManager: FontManager = FontManager.EMPTY
    ) {
        val plotSize = if (width != null && height != null) DoubleVector(width, height) else null

        val imageData = runBlocking {
            PlotImageExport.buildImageFromRawSpecsInternal(
                plotSpec = plotSpec,
                format = PlotImageExport.Format.PNG,
                scalingFactor = scale ?: 1.0,
                targetDPI = dpi,
                plotSize = plotSize,
                unit = unit,
                fontManager = fontManager
            )
        }
        val image = ImageIO.read(imageData.bytes.inputStream())
        val bitmap = BitmapUtil.fromBufferedImage(image)


        imageComparer.assertBitmapEquals(expectedFileName, bitmap)
    }

    companion object {
        val fonts = mapOf(
            Triple("Noto Sans", FontWeight.NORMAL, FontStyle.NORMAL) to createFont("fonts/NotoSans-Regular.ttf"),
            Triple("Noto Sans", BOLD, FontStyle.NORMAL) to createFont("fonts/NotoSans-Bold.ttf"),
            Triple("Noto Sans", FontWeight.NORMAL, ITALIC) to createFont("fonts/NotoSans-Italic.ttf"),
            Triple("Noto Sans", BOLD, ITALIC) to createFont("fonts/NotoSans-BoldItalic.ttf"),

            Triple("Noto Serif", FontWeight.NORMAL, FontStyle.NORMAL) to createFont("fonts/NotoSerif-Regular.ttf"),
            Triple("Noto Serif", BOLD, FontStyle.NORMAL) to createFont("fonts/NotoSerif-Bold.ttf"),
            Triple("Noto Serif", FontWeight.NORMAL, ITALIC) to createFont("fonts/NotoSerif-Italic.ttf"),
            Triple("Noto Serif", BOLD, ITALIC) to createFont("fonts/NotoSerif-BoldItalic.ttf"),

            Triple("Noto Sans Mono", FontWeight.NORMAL, FontStyle.NORMAL) to createFont("fonts/NotoSansMono-Regular.ttf"),
            Triple("Noto Sans Mono", FontWeight.NORMAL, ITALIC) to createFont("fonts/NotoSansMono-Regular.ttf"),
            Triple("Noto Sans Mono", BOLD, FontStyle.NORMAL) to createFont("fonts/NotoSansMono-Bold.ttf"),
            Triple("Noto Sans Mono", BOLD, ITALIC) to createFont("fonts/NotoSansMono-Bold.ttf"),
        )
        val regularFont = createFont("fonts/NotoSans-Regular.ttf")
        val boldFont = createFont("fonts/NotoSans-Bold.ttf")
        val italicFont = createFont("fonts/NotoSans-Italic.ttf")
        val boldItalicFont = createFont("fonts/NotoSans-BoldItalic.ttf")

        val fontManager = FontManager(
            fontResolver = { font ->
                val resolvedFont = fonts[Triple(font.fontFamily, font.fontWeight, font.fontStyle)]
                if (resolvedFont != null) {
                    return@FontManager resolvedFont
                }

                when {
                    font.isBold && font.isItalic -> return@FontManager boldItalicFont
                    font.isBold -> return@FontManager boldFont
                    font.isItalic -> return@FontManager italicFont
                    else -> return@FontManager regularFont
                }

                return@FontManager null
            }
        )

        private fun createFont(resourceName: String): AwtFont {
            val fontStream = AwtCanvasTck::class.java.getClassLoader().getResourceAsStream(resourceName)
                ?: error("Font resource not found: $resourceName")
            try {
                return AwtFont.createFont(AwtFont.TRUETYPE_FONT, fontStream)
                    ?: error("Cannot create font from resource: $resourceName")
            } finally {
                try {
                    fontStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}