/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import demoAndTestShared.AwtBitmapIO
import demoAndTestShared.AwtTestCanvasProvider
import demoAndTestShared.ImageComparer
import org.jetbrains.letsPlot.awt.canvas.FontManager
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.awt.BitmapUtil
import org.jetbrains.letsPlot.core.canvas.FontStyle.ITALIC
import org.jetbrains.letsPlot.core.canvas.FontWeight.BOLD
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit
import org.jetbrains.letsPlot.visualtesting.AwtCanvasTck
import org.jetbrains.letsPlot.visualtesting.AwtFont
import org.junit.BeforeClass
import java.awt.Font
import java.awt.FontFormatException
import java.awt.GraphicsEnvironment
import java.io.IOException
import java.io.InputStream
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

        val imageData = PlotImageExport.buildImageFromRawSpecsInternal(
            plotSpec = plotSpec,
            format = PlotImageExport.Format.PNG,
            scalingFactor = scale ?: 1.0,
            targetDPI = dpi,
            plotSize = plotSize,
            unit = unit,
            fontManager = fontManager
        )
        val image = ImageIO.read(imageData.bytes.inputStream())
        val bitmap = BitmapUtil.fromBufferedImage(image)


        imageComparer.assertBitmapEquals(expectedFileName, bitmap)
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun setUp() {
            registerFont("fonts/NotoSans-Regular.ttf")
            registerFont("fonts/NotoSans-Bold.ttf")
            registerFont("fonts/NotoSans-Italic.ttf")
            registerFont("fonts/NotoSans-BoldItalic.ttf")
            registerFont("fonts/NotoSerif-Regular.ttf")
            registerFont("fonts/NotoSansMono-Regular.ttf")
            registerFont("fonts/NotoSansMono-Bold.ttf")
        }

        val fontManager = FontManager().apply {
            register("Noto Sans", createFont("fonts/NotoSans-Regular.ttf"))
            register("Noto Sans", createFont("fonts/NotoSans-Bold.ttf"), weight = BOLD)
            register("Noto Sans", createFont("fonts/NotoSans-Italic.ttf"), style = ITALIC)
            register("Noto Sans", createFont("fonts/NotoSans-BoldItalic.ttf"), weight = BOLD, style = ITALIC)

            register("Noto Serif", createFont("fonts/NotoSerif-Regular.ttf"))
            register("Noto Serif", createFont("fonts/NotoSerif-Bold.ttf"), weight = BOLD)
            register("Noto Serif", createFont("fonts/NotoSerif-Italic.ttf"), style = ITALIC)
            register("Noto Serif", createFont("fonts/NotoSerif-BoldItalic.ttf"), weight = BOLD, style = ITALIC)

            register("Noto Sans Mono", createFont("fonts/NotoSansMono-Regular.ttf"))
            register("Noto Sans Mono", createFont("fonts/NotoSansMono-Regular.ttf"), style = ITALIC)
            register("Noto Sans Mono", createFont("fonts/NotoSansMono-Bold.ttf"), weight = BOLD)
            register("Noto Sans Mono", createFont("fonts/NotoSansMono-Bold.ttf"), weight = BOLD, style = ITALIC)
        }

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

        private fun registerFont(resourceName: String) {
            val fontStream: InputStream? = PlotImageExportVisualTest::class.java.getClassLoader().getResourceAsStream(resourceName)
            try {
                val customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream)
                val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
                ge.registerFont(customFont)
            } catch (e: FontFormatException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (fontStream != null) {
                    try {
                        fontStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}