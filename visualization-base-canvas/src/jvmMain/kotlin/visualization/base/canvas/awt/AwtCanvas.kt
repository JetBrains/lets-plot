package jetbrains.datalore.visualization.base.canvas.awt

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.datalore.visualization.base.canvas.Context2d
import jetbrains.datalore.visualization.base.canvas.ScaledCanvas

import java.awt.image.BufferedImage

import java.awt.image.BufferedImage.TYPE_INT_ARGB

internal class AwtCanvas private constructor(val image: BufferedImage, size: Vector, pixelRatio: Double) : ScaledCanvas(createContext(image), size, pixelRatio) {

    override fun takeSnapshot(): Async<Canvas.Snapshot> {
        return Asyncs.constant(AwtSnapshot())
    }

    internal inner class AwtSnapshot : Canvas.Snapshot {
        val image: BufferedImage
            get() = this@AwtCanvas.image

        val size: Vector
            get() = this@AwtCanvas.size
    }

    companion object {
        fun create(size: Vector, pixelRatio: Double): AwtCanvas {
            val image = BufferedImage((size.x * pixelRatio).toInt(), (size.y * pixelRatio).toInt(), TYPE_INT_ARGB)
            return AwtCanvas(image, size, pixelRatio)
        }

        private fun createContext(image: BufferedImage): Context2d {
            return AwtContext2d(image.createGraphics())
        }
    }
}
