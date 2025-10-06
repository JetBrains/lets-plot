package org.jetbrains.letsPlot.raster.test

import demoAndTestShared.AwtBitmapIO
import demoAndTestShared.AwtTestCanvasProvider
import demoAndTestShared.ImageComparer
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.awt.BitmapUtil
import org.jetbrains.letsPlot.core.plot.export.PlotImageExport
import org.jetbrains.letsPlot.core.util.PlotExportCommon
import java.awt.Font
import java.awt.FontFormatException
import java.awt.GraphicsEnvironment
import java.io.IOException
import java.io.InputStream
import javax.imageio.ImageIO

actual fun createImageComparer(fonts: List<String>): ImageComparer {
    fonts.forEach(::registerFont)
    return ImageComparer(
        canvasProvider = AwtTestCanvasProvider(),
        bitmapIO = AwtBitmapIO,
        expectedDir = System.getProperty("user.dir") + "/src/jvmTest/resources/expected/",
        outDir = System.getProperty("user.dir") + "/build/reports/"
    )
}


fun registerFont(resourceName: String) {
    val fontStream: InputStream? = ExportVisualTest::class.java.getClassLoader().getResourceAsStream(resourceName)
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


actual fun assertPlot(
    imageComparer: ImageComparer,
    expectedFileName: String,
    plotSpec: MutableMap<String, Any>,
    width: Number?,
    height: Number?,
    unit: PlotExportCommon.SizeUnit?,
    dpi: Number?,
    scale: Number?
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
