/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.CPointer
import org.jetbrains.letsPlot.commons.encoding.Base64
import org.jetbrains.letsPlot.commons.encoding.Png
import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.AnimationProvider
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasControl

class MagickCanvasControl(
    val w: Int,
    val h: Int,
    override val pixelDensity: Double,
    private val fontManager: MagickFontManager,
) : CanvasControl {
    val children = mutableListOf<Canvas>()

    override val size: Vector
        get() = Vector(w, h)

    override fun addChild(canvas: Canvas) {
        children.add(canvas)
    }

    override fun addChild(index: Int, canvas: Canvas) {
        children.add(index, canvas)
    }

    override fun removeChild(canvas: Canvas) {
        children.remove(canvas)
    }

    override fun onResize(listener: (Vector) -> Unit): Registration {
        //TODO("onResize() - Not yet implemented")
        return Registration.EMPTY
    }

    override fun snapshot(): Canvas.Snapshot {
        TODO("snapshot() - Not yet implemented")
    }

    override fun createAnimationTimer(eventHandler: AnimationProvider.AnimationEventHandler): AnimationProvider.AnimationTimer {
        return object : AnimationProvider.AnimationTimer {
            override fun start() {

            }

            override fun stop() {

            }
        }
    }

    override fun createCanvas(size: Vector): Canvas {
        return MagickCanvas.create(size, pixelDensity, fontManager)
    }

    override fun createSnapshot(bitmap: Bitmap): Canvas.Snapshot {
        return MagickSnapshot.fromBitmap(bitmap)
    }

    override fun decodeDataImageUrl(dataUrl: String): Async<Canvas.Snapshot> {
        println("MagickCanvasControl.createSnapshot(dataUrl): dataUrl.size = ${dataUrl.length}")
        if (false) {
            if (!dataUrl.startsWith("data:image/png;base64,")) {
                throw IllegalArgumentException("Unsupported data URL format: $dataUrl")
            }
            val data = dataUrl.removePrefix("data:image/png;base64,")
            val pngData = Base64.decode(data)

            val img = loadImageFromPngBytes(pngData)

            return Asyncs.constant(MagickSnapshot(img))
        } else {
            val bitmap = Png.decodeDataImage(dataUrl)
            return Asyncs.constant(MagickSnapshot.fromBitmap(bitmap))
        }
    }

    override fun decodePng(
        png: ByteArray,
        size: Vector
    ): Async<Canvas.Snapshot> {
        TODO("Not yet implemented")
    }

    override fun addEventHandler(
        eventSpec: MouseEventSpec,
        eventHandler: EventHandler<MouseEvent>
    ): Registration {
        return Registration.EMPTY
    }

    override fun <T> schedule(f: () -> T) {
        TODO("Not yet implemented")
    }

    fun loadImageFromPngBytes(bytes: ByteArray): CPointer<ImageMagick.MagickWand> {
        println("MagickCanvasControl.loadImageFromPngBytes: bytes.size = ${bytes.size}")
        val png = Png.decode(bytes)
        return MagickUtil.fromBitmap(png)
    }
}
