/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.canvas

import javafx.scene.Group
import javafx.scene.image.Image
import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.event.handler
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.AnimationProvider
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.jfx.canvas.JavafxCanvasUtil.imagePngBase64ToImage
import java.io.ByteArrayInputStream

class JavafxCanvasControl(
    private val myRoot: Group,
    override val size: Vector,
    override val pixelDensity: Double,
    private val myEventPeer: EventPeer<MouseEventSpec, MouseEvent> // TODO: replace with MouseEventPeer
) : CanvasControl {

    override fun createAnimationTimer(eventHandler: AnimationProvider.AnimationEventHandler): AnimationProvider.AnimationTimer {
        return object : JavafxAnimationTimer() {
            override fun handle(millisTime: Long) {
                eventHandler.onEvent(millisTime)
            }
        }
    }

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return myEventPeer.addEventHandler(
            eventSpec,
            handler {
                eventHandler.onEvent(it)
            }
        )
    }

    override fun createCanvas(size: Vector): Canvas {
        return JavafxCanvas.create(size, pixelDensity)
    }

    override fun createSnapshot(bitmap: Bitmap): Canvas.Snapshot {
        TODO("JavafxCanvasControl.createSnapshot() - NOT IMPLEMENTED")
        //val img = WritableImage(bitmap.width, bitmap.height)
        //img.pixelWriter.setPixels(...)
        //return JavafxCanvas.JavafxSnapshot(img)
    }

    override fun decodeDataImageUrl(dataUrl: String): Async<Canvas.Snapshot> {
        val image = imagePngBase64ToImage(dataUrl)
        val snapshot = JavafxCanvas.JavafxSnapshot(image)
        return Asyncs.constant(snapshot)
    }

    override fun decodePng(png: ByteArray, size: Vector): Async<Canvas.Snapshot> {
        val size1 = size * pixelDensity.toInt()
        val image = Image(ByteArrayInputStream(png), size1.x.toDouble(), size1.y.toDouble(), false, false)
        val snapshot = JavafxCanvas.JavafxSnapshot(image)
        return Asyncs.constant(snapshot)
    }

    override fun addChild(canvas: Canvas) {
        myRoot.children.add((canvas as JavafxCanvas).nativeCanvas)
    }

    override fun addChild(index: Int, canvas: Canvas) {
        myRoot.children.add(index, (canvas as JavafxCanvas).nativeCanvas)
    }

    override fun removeChild(canvas: Canvas) {
        myRoot.children.remove((canvas as JavafxCanvas).nativeCanvas)
    }

    override fun onResize(listener: (Vector) -> Unit): Registration {
        println("JavafxCanvasControl.onResize() - NOT IMPLEMENTED")
        return Registration.EMPTY
    }

    override fun snapshot(): Canvas.Snapshot {
        TODO("Not yet implemented")
    }

    override fun <T> schedule(f: () -> T) {
        JavafxCanvasUtil.runInJavafxThread(f)
    }

    private operator fun Vector.times(value: Int): Vector {
        return Vector(x * value, y * value)
    }
}