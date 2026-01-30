/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.visualtesting.plot

//internal abstract class PlotTestBase : TestSuit() {
//
//}
/*


import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.canvas.CanvasProvider
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.util.PlotExportCommon.SizeUnit
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import kotlin.collections.emptyMap
import kotlin.jvm.JvmStatic

class PlotTestBase(
    val canvasProvider: CanvasProvider,
    val bitmapIO: ImageComparer.BitmapIO,
    val outputDir: String,
    val expectedDir: String
) {

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
            canvasProvider = canvasProvider,
            bitmapIO = bitmapIO,
            expectedDir = expectedDir,
            outDir = outputDir
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
*/