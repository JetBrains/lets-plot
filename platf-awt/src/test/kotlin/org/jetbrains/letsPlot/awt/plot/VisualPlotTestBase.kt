/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import demoAndTestShared.AwtBitmapIO
import demoAndTestShared.AwtTestCanvasProvider
import demoAndTestShared.ImageComparer
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.awt.BitmapUtil
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit
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
        scale: Number? = null
    ) {
        val plotSize = if (width != null && height != null) DoubleVector(width, height) else null

        val imageData = PlotImageExport.buildImageFromRawSpecs(
            plotSpec = plotSpec,
            format = PlotImageExport.Format.PNG,
            scalingFactor = scale ?: 1.0,
            targetDPI = dpi,
            plotSize = plotSize,
            unit = unit
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