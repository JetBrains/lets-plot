package jetbrains.datalore.visualization.base.canvas.dom

import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.js.dom.DomEventListener
import jetbrains.datalore.base.js.dom.DomEventType
import jetbrains.datalore.visualization.base.canvas.EventPeer
import org.w3c.dom.Node

typealias W3cMouseEvent = org.w3c.dom.events.MouseEvent

internal class DomEventPeer (rootElement: Node) :
    EventPeer<MouseEventSpec, W3cMouseEvent>(MouseEventSpec::class) {
    private var myButtonPressed = false

    init {
        rootElement.addEventListener(DomEventType.MOUSE_ENTER.name, DomEventListener<W3cMouseEvent> {
            dispatch(MouseEventSpec.MOUSE_ENTERED, it)
            false
        })

        rootElement.addEventListener(DomEventType.MOUSE_LEAVE.name, DomEventListener<W3cMouseEvent> {
            dispatch(MouseEventSpec.MOUSE_LEFT, it)
            false
        })

        rootElement.addEventListener(DomEventType.CLICK.name, DomEventListener<W3cMouseEvent> {
            dispatch(MouseEventSpec.MOUSE_CLICKED, it)
            false
        })

        rootElement.addEventListener(DomEventType.DOUBLE_CLICK.name, DomEventListener<W3cMouseEvent> {
            dispatch(MouseEventSpec.MOUSE_DOUBLE_CLICKED, it)
            false
        })

        rootElement.addEventListener(DomEventType.MOUSE_DOWN.name, DomEventListener<W3cMouseEvent> {
            myButtonPressed = true
            dispatch(MouseEventSpec.MOUSE_PRESSED, it)
            false
        })

        rootElement.addEventListener(DomEventType.MOUSE_UP.name, DomEventListener<W3cMouseEvent> {
            myButtonPressed = false
            dispatch(MouseEventSpec.MOUSE_RELEASED, it)
            false
        })

        rootElement.addEventListener(DomEventType.MOUSE_MOVE.name, DomEventListener<W3cMouseEvent> {
            if (myButtonPressed) {
                dispatch(MouseEventSpec.MOUSE_DRAGGED, it)
            } else {
                dispatch(MouseEventSpec.MOUSE_MOVED, it)
            }
            false
        })
    }

    override fun onSpecAdded(spec: MouseEventSpec) {}

    override fun onSpecRemoved(spec: MouseEventSpec) {}
}