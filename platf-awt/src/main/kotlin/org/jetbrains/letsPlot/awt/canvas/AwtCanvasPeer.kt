package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.commons.encoding.Png
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.commons.values.awt.BitmapUtil
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.core.canvas.TextMetrics
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

class AwtCanvasPeer(
    private val pixelDensity: Double = 1.0
) : CanvasPeer {
    private val measureCanvas = AwtCanvas.create(Vector(1, 1), pixelDensity)

    override fun createCanvas(size: Vector): AwtCanvas {
        return AwtCanvas.create(size, pixelDensity)
    }

    fun createCanvas(width: Number, height: Number): AwtCanvas {
        return AwtCanvas.create(Vector(width.toInt(), height.toInt()), pixelDensity)
    }

    override fun createSnapshot(bitmap: Bitmap): Canvas.Snapshot {
        val bufferedImage = BitmapUtil.toBufferedImage(bitmap)
        return AwtCanvas.AwtSnapshot(bufferedImage)
    }

    override fun decodeDataImageUrl(dataUrl: String): Async<Canvas.Snapshot> {
        return Asyncs.constant(
            AwtCanvas.AwtSnapshot(imagePngBase64ToImage(dataUrl))
        )
    }

    override fun decodePng(png: ByteArray): Async<Canvas.Snapshot> {
        val src = ImageIO.read(ByteArrayInputStream(png))
        val snapshot = AwtCanvas.AwtSnapshot(src)
        return Asyncs.constant(snapshot)
    }

    override fun measureText(text: String, font: Font): TextMetrics {
        val context2d = measureCanvas.context2d
        context2d.setFont(font)
        return context2d.measureText(text)
    }

    private fun imagePngBase64ToImage(dataUrl: String): BufferedImage {
        val bitmap = Png.decodeDataImage(dataUrl)
        val bufferedImage = BitmapUtil.toBufferedImage(bitmap)
        return bufferedImage
    }

    override fun dispose() {

    }
}