package jetbrains.datalore.visualization.base.canvas.javaFx

import javafx.embed.swing.SwingFXUtils
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.datalore.visualization.base.canvas.CanvasControl
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

    override fun createAnimationTimer(eventHandler: CanvasControl.AnimationEventHandler): CanvasControl.AnimationTimer {
        return myJavafxCanvasControl.createAnimationTimer(object : CanvasControl.AnimationEventHandler {
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

    override fun addMouseEventHandler(eventSpec: CanvasControl.EventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return myJavafxCanvasControl.addMouseEventHandler(eventSpec, eventHandler)
    }

    override fun createCanvas(size: Vector): Canvas {
        return myJavafxCanvasControl.createCanvas(size)
    }

    override fun addChildren(canvas: Canvas) {
        myJavafxCanvasControl.addChildren(canvas)
    }

    override fun removeChild(canvas: Canvas) {
        myJavafxCanvasControl.removeChild(canvas)
    }
}
