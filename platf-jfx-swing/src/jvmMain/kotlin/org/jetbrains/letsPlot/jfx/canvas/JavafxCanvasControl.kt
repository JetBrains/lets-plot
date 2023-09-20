/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.canvas

import javafx.scene.Group
import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.event.handler
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.AnimationProvider
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.jfx.canvas.JavafxCanvasUtil.imagePngBase64ToImage
import org.jetbrains.letsPlot.jfx.canvas.JavafxCanvasUtil.imagePngByteArrayToImage

class JavafxCanvasControl(
    private val myRoot: Group,
    override val size: Vector,
    private val myPixelRatio: Double,
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
        return JavafxCanvas.create(size, myPixelRatio)
    }

    override fun createSnapshot(dataUrl: String): Async<Canvas.Snapshot> {
        return Asyncs.constant(
            JavafxCanvas.JavafxSnapshot(
                imagePngBase64ToImage(
                    dataUrl
                )
            )
        )
    }

    override fun createSnapshot(bytes: ByteArray, size: Vector): Async<Canvas.Snapshot> {
        return Asyncs.constant(
            JavafxCanvas.JavafxSnapshot(
                imagePngByteArrayToImage(bytes, size * myPixelRatio.toInt())
            )
        )
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

    override fun <T> schedule(f: () -> T) {
        JavafxCanvasUtil.runInJavafxThread(f)
    }

    private operator fun Vector.times(value: Int): Vector {
        return Vector(x * value, y * value)
    }
}