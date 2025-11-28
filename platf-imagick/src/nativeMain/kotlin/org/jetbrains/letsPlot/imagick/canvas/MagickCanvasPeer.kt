package org.jetbrains.letsPlot.imagick.canvas

import org.jetbrains.letsPlot.commons.encoding.Png
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.core.canvas.TextMetrics

class MagickCanvasPeer(
    val pixelDensity: Double,
    private val fontManager: MagickFontManager,
    private val antialiasing: Boolean = true
) : CanvasPeer, Disposable {
    private val measureCanvas = MagickCanvas.create(1, 1, pixelDensity, fontManager, antialiasing)

    private val snapshots = mutableSetOf<MagickSnapshot>()

    override fun createCanvas(size: Vector): MagickCanvas {
        return MagickCanvas.create(size.x, size.y, pixelDensity, fontManager, antialiasing)
    }

    fun createCanvas(width: Number, height: Number): MagickCanvas {
        return MagickCanvas.create(width, height, pixelDensity, fontManager, antialiasing)
    }

    override fun measureText(text: String, font: Font): TextMetrics {
        val context2d = measureCanvas.context2d
        context2d.setFont(font)
        return context2d.measureText(text)
    }

    override fun createSnapshot(bitmap: Bitmap): MagickSnapshot {
        val snapshot = MagickSnapshot.fromBitmap(bitmap)
        snapshots.add(snapshot)
        return snapshot
    }

    override fun decodeDataImageUrl(dataUrl: String): Async<Canvas.Snapshot> {
        val bitmap = Png.decodeDataImage(dataUrl)
        return Asyncs.constant(MagickSnapshot.fromBitmap(bitmap))
    }

    override fun decodePng(png: ByteArray): Async<Canvas.Snapshot> {
        TODO("Not yet implemented")
    }

    override fun dispose() {
        measureCanvas.dispose()
        for (snapshot in snapshots) {
            snapshot.dispose()
        }

        snapshots.clear()
    }
}