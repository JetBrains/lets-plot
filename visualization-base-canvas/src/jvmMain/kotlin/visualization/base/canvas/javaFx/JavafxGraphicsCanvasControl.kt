package jetbrains.datalore.visualization.base.canvas.javaFx

import javafx.embed.swing.SwingFXUtils
import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.AnimationProvider.AnimationEventHandler
import jetbrains.datalore.visualization.base.canvas.AnimationProvider.AnimationTimer
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.datalore.visualization.base.canvas.GraphicsCanvasControl
import jetbrains.datalore.visualization.base.canvas.javaFx.JavafxCanvasUtil.takeSnapshotImage
import java.awt.image.BufferedImage

internal class JavafxGraphicsCanvasControl(
        size: Vector,
        private val myRepaint: Runnable,
        pixelRatio: Double) :
        GraphicsCanvasControl {

    private val myJavafxCanvasControl: JavafxCanvasControl = JavafxCanvasControl(size, pixelRatio)

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

    override fun addChild(canvas: Canvas) {
        myJavafxCanvasControl.addChild(canvas)
    }

    override fun removeChild(canvas: Canvas) {
        myJavafxCanvasControl.removeChild(canvas)
    }
}
