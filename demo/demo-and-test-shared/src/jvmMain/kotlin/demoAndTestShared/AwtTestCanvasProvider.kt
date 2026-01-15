package demoAndTestShared

import org.jetbrains.letsPlot.awt.canvas.AwtCanvas
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasProvider

class AwtTestCanvasProvider(
    val pixelDensity: Double = 1.0 // Default pixel density for testing
) : CanvasProvider {
    override fun createCanvas(size: Vector): Canvas {
        return AwtCanvas.create(size, pixelDensity)
    }

    override fun createSnapshot(bitmap: Bitmap): Canvas.Snapshot {
        return AwtCanvas.AwtSnapshot.fromBitmap(bitmap)
    }

    override fun decodeDataImageUrl(dataUrl: String): Async<Canvas.Snapshot> {
        TODO("Not yet implemented")
    }

    override fun decodePng(png: ByteArray): Async<Canvas.Snapshot> {
        TODO("Not yet implemented")
    }
}