package jetbrains.datalore.visualization.base.canvasGwt

import jetbrains.datalore.base.domCore.css.enumerables.CssPosition
import jetbrains.datalore.base.domCore.dom.DomApi
import jetbrains.datalore.base.domCore.dom.DomHTMLElement
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.datalore.visualization.base.canvas.CanvasControl
import org.w3c.dom.HTMLElement

class GwtCanvasControl(override val size: Vector) : CanvasControl {
    val rootElement: DomHTMLElement = DomHTMLElement(DomApi.createDiv().element as HTMLElement)

    init {
        rootElement.style.setPosition(CssPosition.RELATIVE)
    }

    override fun createAnimationTimer(eventHandler: CanvasControl.AnimationEventHandler): CanvasControl.AnimationTimer {
        return object : GwtAnimationTimer(rootElement) {
            override fun handle(millisTime: Long) {
                eventHandler.onEvent(millisTime)
            }
        }
    }

    override fun addMouseEventHandler(eventSpec: CanvasControl.EventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return GwtCanvasUtil.addMouseEventHandler(rootElement, eventSpec, eventHandler)
    }

    override fun createCanvas(size: Vector): Canvas {
        val gwtCanvas = GwtCanvas.create(size)
        gwtCanvas.domHTMLCanvasElement.style.setPosition(CssPosition.ABSOLUTE)
        return gwtCanvas
    }

    override fun addChildren(canvas: Canvas) {
        rootElement.appendChild((canvas as GwtCanvas).domHTMLCanvasElement)
    }

    override fun removeChild(canvas: Canvas) {
        rootElement.removeChild((canvas as GwtCanvas).domHTMLCanvasElement)

    }
}
