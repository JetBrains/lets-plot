package jetbrains.datalore.visualization.base.canvasDom

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.js.css.enumerables.CssPosition
import jetbrains.datalore.base.js.css.setPosition
import jetbrains.datalore.base.js.dom.DomApi
import jetbrains.datalore.base.js.dom.DomHTMLElement
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.datalore.visualization.base.canvas.CanvasControl

class DomCanvasControl(override val size: Vector) : CanvasControl {
    val rootElement: DomHTMLElement = DomApi.createDiv() as DomHTMLElement

    init {
        rootElement.style.setPosition(CssPosition.RELATIVE)
    }

    override fun createAnimationTimer(eventHandler: CanvasControl.AnimationEventHandler): CanvasControl.AnimationTimer {
        return object : DomAnimationTimer(rootElement) {
            override fun handle(millisTime: Long) {
                eventHandler.onEvent(millisTime)
            }
        }
    }

    override fun addMouseEventHandler(eventSpec: CanvasControl.EventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return DomCanvasUtil.addMouseEventHandler(rootElement, eventSpec, eventHandler)
    }

    override fun createCanvas(size: Vector): Canvas {
        val domCanvas = DomCanvas.create(size)
        domCanvas.domHTMLCanvasElement.style.setPosition(CssPosition.ABSOLUTE)
        return domCanvas
    }

    override fun addChildren(canvas: Canvas) {
        rootElement.appendChild((canvas as DomCanvas).domHTMLCanvasElement)
    }

    override fun removeChild(canvas: Canvas) {
        rootElement.removeChild((canvas as DomCanvas).domHTMLCanvasElement)

    }
}
