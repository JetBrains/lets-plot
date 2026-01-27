package org.jetbrains.letsPlot.imagick.canvas

import org.jetbrains.letsPlot.commons.encoding.Png
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasPeer

class MagickCanvasPeer(
    val pixelDensity: Double,
    private val fontManager: MagickFontManager,
    private val antialiasing: Boolean = true
) : CanvasPeer {
    override fun createCanvas(size: Vector): MagickCanvas {
        return MagickCanvas.create(size.x, size.y, pixelDensity, fontManager, antialiasing)
    }

    override fun createCanvas(size: Vector, contentScale: Double): Canvas {
        return MagickCanvas.create(size.x, size.y, contentScale, fontManager, antialiasing)
    }

    override fun createSnapshot(bitmap: Bitmap): MagickSnapshot {
        return MagickSnapshot.fromBitmap(bitmap)
    }

    override fun decodeDataImageUrl(dataUrl: String): Async<Canvas.Snapshot> {
        val bitmap = Png.decodeDataImage(dataUrl)
        return Asyncs.constant(MagickSnapshot.fromBitmap(bitmap))
    }

    override fun decodePng(png: ByteArray): Async<Canvas.Snapshot> {
        TODO("Not yet implemented")
    }
}