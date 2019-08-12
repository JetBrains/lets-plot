package jetbrains.datalore.visualization.base.canvas.javaFx

import javafx.scene.Group
import javafx.scene.Parent
import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.AnimationProvider.AnimationEventHandler
import jetbrains.datalore.visualization.base.canvas.AnimationProvider.AnimationTimer
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.datalore.visualization.base.canvas.CanvasControl
import jetbrains.datalore.visualization.base.canvas.javaFx.JavafxCanvasUtil.imagePngBase64ToImage

class JavafxCanvasControl(override val size: Vector, private val myPixelRatio: Double) : CanvasControl {
    private val myEventPeer: JavafxEventPeer
    private val myRoot = Group()

    val javafxRoot: Parent
        get() = myRoot

    init {
        myEventPeer = JavafxEventPeer(myRoot)
    }

    override fun createAnimationTimer(eventHandler: AnimationEventHandler): AnimationTimer {
        return object : JavafxAnimationTimer() {
            override fun handle(millisTime: Long) {
                eventHandler.onEvent(millisTime)
            }
        }
    }

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return JavafxCanvasUtil.addMouseEventHandler(myEventPeer, eventSpec, eventHandler)
    }

    override fun createCanvas(size: Vector): Canvas {
        return JavafxCanvas.create(size, myPixelRatio)
    }

    override fun createSnapshot(dataUrl: String): Async<Canvas.Snapshot> {
        return Asyncs.constant(JavafxCanvas.JavafxSnapshot(imagePngBase64ToImage(dataUrl)))
    }

    override fun addChild(canvas: Canvas) {
        myRoot.children.add((canvas as JavafxCanvas).nativeCanvas)
    }

    override fun removeChild(canvas: Canvas) {
        myRoot.children.remove((canvas as JavafxCanvas).nativeCanvas)
    }
}
