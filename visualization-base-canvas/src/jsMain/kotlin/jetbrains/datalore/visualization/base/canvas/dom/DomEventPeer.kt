package jetbrains.datalore.visualization.base.canvas.dom

import jetbrains.datalore.base.js.dom.DomEventListener
import jetbrains.datalore.base.js.dom.DomEventType
import jetbrains.datalore.visualization.base.canvas.EventPeer
import jetbrains.datalore.visualization.base.canvas.dom.DomEventPeer.DomEventSpec
import org.w3c.dom.Node

typealias W3cMouseEvent = org.w3c.dom.events.MouseEvent


class DomEventPeer (rootElement: Node) :
    EventPeer<DomEventSpec, W3cMouseEvent>(DomEventSpec::class) {
    private var myButtonPressed = false

    init {
        rootElement.addEventListener(DomEventType.MOUSE_ENTER.name, DomEventListener<W3cMouseEvent> {
            dispatch(DomEventSpec.MOUSE_ENTERED, it)
            false
        })

        rootElement.addEventListener(DomEventType.MOUSE_LEAVE.name, DomEventListener<W3cMouseEvent> {
            dispatch(DomEventSpec.MOUSE_EXITED, it)
            false
        })

        rootElement.addEventListener(DomEventType.CLICK.name, DomEventListener<W3cMouseEvent> {
            dispatch(DomEventSpec.MOUSE_CLICKED, it)
            false
        })

        rootElement.addEventListener(DomEventType.DOUBLE_CLICK.name, DomEventListener<W3cMouseEvent> {
            dispatch(DomEventSpec.MOUSE_DOUBLE_CLICKED, it)
            false
        })

        rootElement.addEventListener(DomEventType.MOUSE_DOWN.name, DomEventListener<W3cMouseEvent> {
            myButtonPressed = true
            dispatch(DomEventSpec.MOUSE_PRESSED, it)
            false
        })

        rootElement.addEventListener(DomEventType.MOUSE_UP.name, DomEventListener<W3cMouseEvent> {
            myButtonPressed = false
            dispatch(DomEventSpec.MOUSE_RELEASED, it)
            false
        })

        rootElement.addEventListener(DomEventType.MOUSE_MOVE.name, DomEventListener<W3cMouseEvent> {
            if (myButtonPressed) {
                dispatch(DomEventSpec.MOUSE_DRAGGED, it)
            }
            false
        })
    }

    override fun onSpecAdded(spec: DomEventSpec) {}

    override fun onSpecRemoved(spec: DomEventSpec) {}

    enum class DomEventSpec {
        MOUSE_ENTERED,
        MOUSE_EXITED,
        MOUSE_MOVED,
        MOUSE_DRAGGED,
        MOUSE_CLICKED,
        MOUSE_DOUBLE_CLICKED,
        MOUSE_PRESSED,
        MOUSE_RELEASED
    }
}