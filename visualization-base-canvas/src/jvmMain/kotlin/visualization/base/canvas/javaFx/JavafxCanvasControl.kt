package jetbrains.datalore.visualization.base.canvas.javaFx

import javafx.scene.Group
import javafx.scene.Parent
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.datalore.visualization.base.canvas.CanvasControl

class JavafxCanvasControl(override val size: Vector, private val myPixelRatio: Double) : CanvasControl {
    private val myEventPeer: JavafxEventPeer
    private val myRoot = Group()

    val javafxRoot: Parent
        get() = myRoot

    init {
        myEventPeer = JavafxEventPeer(myRoot)
    }

    override fun createAnimationTimer(eventHandler: CanvasControl.AnimationEventHandler): CanvasControl.AnimationTimer {
        return object : JavafxAnimationTimer() {
            override fun handle(millisTime: Long) {
                eventHandler.onEvent(millisTime)
            }
        }
    }

    override fun addMouseEventHandler(eventSpec: CanvasControl.EventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return JavafxCanvasUtil.addMouseEventHandler(myEventPeer, eventSpec, eventHandler)
    }

    override fun createCanvas(size: Vector): Canvas {
        return JavafxCanvas.create(size, myPixelRatio)
    }

    override fun addChildren(canvas: Canvas) {
        myRoot.children.add((canvas as JavafxCanvas).nativeCanvas)
    }

    override fun removeChild(canvas: Canvas) {
        myRoot.children.remove((canvas as JavafxCanvas).nativeCanvas)
    }
}
