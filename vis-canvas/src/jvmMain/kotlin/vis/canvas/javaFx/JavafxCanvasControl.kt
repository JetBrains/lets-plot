/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.javaFx

import javafx.scene.Group
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.handler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.AnimationProvider.AnimationEventHandler
import jetbrains.datalore.vis.canvas.AnimationProvider.AnimationTimer
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.datalore.vis.canvas.EventPeer
import jetbrains.datalore.vis.canvas.javaFx.JavafxCanvasUtil.imagePngBase64ToImage
import jetbrains.datalore.vis.canvas.javaFx.JavafxCanvasUtil.imagePngByteArrayToImage


class JavafxCanvasControl(
    private val myRoot: Group,
    override val size: Vector,
    private val myPixelRatio: Double,
    private val myEventPeer: EventPeer<MouseEventSpec, MouseEvent>
) : CanvasControl {

    override fun createAnimationTimer(eventHandler: AnimationEventHandler): AnimationTimer {
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
