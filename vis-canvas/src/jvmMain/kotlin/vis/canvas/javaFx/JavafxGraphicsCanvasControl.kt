/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.javaFx

import javafx.embed.swing.SwingFXUtils
import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.AnimationProvider.AnimationEventHandler
import jetbrains.datalore.vis.canvas.AnimationProvider.AnimationTimer
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.GraphicsCanvasControl
import jetbrains.datalore.vis.canvas.javaFx.JavafxCanvasUtil.takeSnapshotImage
import java.awt.image.BufferedImage

internal class JavafxGraphicsCanvasControl(
        size: Vector,
        private val myRepaint: Runnable,
        pixelRatio: Double
) :
    GraphicsCanvasControl {

    private val myJavafxCanvasControl = JavafxCanvasControl(size, pixelRatio)
    override var image: BufferedImage? = null

    override val size: Vector
        get() = myJavafxCanvasControl.size

    override fun createAnimationTimer(eventHandler: AnimationEventHandler): AnimationTimer {
        return myJavafxCanvasControl.createAnimationTimer(object : AnimationEventHandler {
            override fun onEvent(millisTime: Long): Boolean {
                if (eventHandler.onEvent(millisTime)) {
                    redraw()
                    return true
                }
                return false
            }
        })
    }

    private fun redraw() {
        image = SwingFXUtils.fromFXImage(takeSnapshotImage(myJavafxCanvasControl.javafxRoot), null)
        myRepaint.run()
    }

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return myJavafxCanvasControl.addEventHandler(eventSpec, eventHandler)
    }

    override fun createCanvas(size: Vector): Canvas {
        return myJavafxCanvasControl.createCanvas(size)
    }

    override fun createSnapshot(dataUrl: String): Async<Canvas.Snapshot> {
        return myJavafxCanvasControl.createSnapshot(dataUrl)
    }

    override fun createSnapshot(bytes: ByteArray): Async<Canvas.Snapshot> {
        return myJavafxCanvasControl.createSnapshot(bytes)
    }

    override fun addChild(canvas: Canvas) {
        myJavafxCanvasControl.addChild(canvas)
    }

    override fun addChild(index: Int, canvas: Canvas) {
        myJavafxCanvasControl.addChild(index, canvas)
    }

    override fun removeChild(canvas: Canvas) {
        myJavafxCanvasControl.removeChild(canvas)
    }

    override fun <T> schedule(f: () -> T) {
        myJavafxCanvasControl.schedule(f)
    }

}
