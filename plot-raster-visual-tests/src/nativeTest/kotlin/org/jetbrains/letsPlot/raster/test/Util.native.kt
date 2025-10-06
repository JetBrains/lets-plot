@file:OptIn(ExperimentalForeignApi::class)

package org.jetbrains.letsPlot.raster.test

import demoAndTestShared.ImageComparer
import demoAndTestShared.NativeBitmapIO
import kotlinx.cinterop.ExperimentalForeignApi
import org.jetbrains.letsPlot.commons.encoding.Png
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.intern.io.Native
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasProvider
import org.jetbrains.letsPlot.core.util.PlotExportCommon
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import org.jetbrains.letsPlot.imagick.canvas.MagickFontManager
import org.jetbrains.letsPlot.imagick.canvas.MagickSnapshot
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil


actual fun createImageComparer(fonts: List<String>): ImageComparer {
    return ImageComparer(
        canvasProvider = MagickCanvasProvider(fontManager),
        bitmapIO = NativeBitmapIO,
        expectedDir = Native.getCurrentDir() + "/src/nativeTest/resources/expected/",
        outDir = Native.getCurrentDir() + "/build/reports/",
    )
}


actual fun registerFont(resourceName: String) {
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

class MagickCanvasProvider(
    private val magickFontManager: MagickFontManager,
) : CanvasProvider {
    override fun createCanvas(size: Vector): MagickCanvas {
        return MagickCanvas.create(size.x, size.y, 1.0, magickFontManager)
    }

    override fun createSnapshot(bitmap: Bitmap): MagickSnapshot {
        return MagickSnapshot.fromBitmap(bitmap)
    }

    override fun decodeDataImageUrl(dataUrl: String): Async<Canvas.Snapshot> {
        println("MagickCanvasControl.createSnapshot(dataUrl): dataUrl.size = ${dataUrl.length}")
        val bitmap = Png.decodeDataImage(dataUrl)
        return Asyncs.constant(MagickSnapshot.fromBitmap(bitmap))
    }

    override fun decodePng(png: ByteArray): Async<Canvas.Snapshot> {
        val img = MagickUtil.fromBitmap(Png.decode(png))
        return Asyncs.constant(MagickSnapshot(img))
    }
}
