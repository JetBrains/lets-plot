package jetbrains.datalore.visualization.base.canvas

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration

interface CanvasControl {

    val size: Vector

    fun createAnimationTimer(eventHandler: AnimationEventHandler): AnimationTimer

    fun addMouseEventHandler(eventSpec: EventSpec, eventHandler: EventHandler<MouseEvent>): Registration

    fun createCanvas(size: Vector): Canvas

    fun addChild(canvas: Canvas)

    fun removeChild(canvas: Canvas)

    enum class EventSpec {
        MOUSE_ENTERED,
        MOUSE_LEFT,
        MOUSE_MOVED,
        MOUSE_DRAGGED,
        MOUSE_CLICKED,
        MOUSE_DOUBLE_CLICKED,
        MOUSE_PRESSED,
        MOUSE_RELEASED
    }

    interface AnimationTimer {
        fun start()
        fun stop()
    }

    interface AnimationEventHandler {
        fun onEvent(millisTime: Long): Boolean
    }
}
