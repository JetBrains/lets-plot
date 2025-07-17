/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

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
        require(canvas is MagickCanvas) { "Canvas must be of type MagickCanvas" }

        children.remove(canvas)
        canvas.dispose()
    }

    override fun onResize(listener: (Vector) -> Unit): Registration {
        //TODO("onResize() - Not yet implemented")
        return Registration.EMPTY
    }

    override fun snapshot(): MagickSnapshot {
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

    override fun createCanvas(size: Vector): MagickCanvas {
        return MagickCanvas.create(size, pixelDensity, fontManager)
    }

    override fun createSnapshot(bitmap: Bitmap): MagickSnapshot {
        return MagickSnapshot.fromBitmap(bitmap)
    }

    override fun decodeDataImageUrl(dataUrl: String): Async<Canvas.Snapshot> {
        val bitmap = Png.decodeDataImage(dataUrl)
        return Asyncs.constant(MagickSnapshot.fromBitmap(bitmap))
    }

    override fun decodePng(png: ByteArray, size: Vector): Async<Canvas.Snapshot> {
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

}
