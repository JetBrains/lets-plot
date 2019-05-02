package jetbrains.datalore.visualization.base.canvas.awt

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.datalore.visualization.base.canvas.CanvasControl
import jetbrains.datalore.visualization.base.canvas.GraphicsCanvasControl

import java.awt.image.BufferedImage

internal class AwtGraphicsCanvasControl(
        override val size: Vector,
        private val myRepaint: Runnable,
        private val myPixelRatio: Double) :
        GraphicsCanvasControl {

    override val image: BufferedImage
        get() = throw IllegalStateException()

    override fun createAnimationTimer(eventHandler: CanvasControl.AnimationEventHandler): CanvasControl.AnimationTimer {
        return object : AwtAnimationTimer() {
            internal override fun handle(millisTime: Long) {
                if (eventHandler.onEvent(millisTime)) {
                    myRepaint.run()
                }
            }
        }
    }

    override fun addMouseEventHandler(eventSpec: CanvasControl.EventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        throw IllegalStateException("Not supported in AWT")
    }

    override fun createCanvas(size: Vector): Canvas {
        return AwtCanvas.create(size, myPixelRatio)
    }

    override fun addChildren(canvas: Canvas) {
        throw IllegalStateException()
    }

    override fun removeChild(canvas: Canvas) {
        throw IllegalStateException()
    }
}
