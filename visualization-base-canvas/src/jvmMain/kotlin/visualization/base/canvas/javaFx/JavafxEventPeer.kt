package jetbrains.datalore.visualization.base.canvas.javaFx

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.visualization.base.canvas.EventPeer

internal class JavafxEventPeer(private val myNode: Node) :
        EventPeer<JavafxEventPeer.JavafxEventSpec, MouseEvent>(JavafxEventSpec::class) {

    override fun onSpecAdded(spec: JavafxEventSpec) {
        getEventHandlerConsumer(spec)(EventHandler { event -> this@JavafxEventPeer.dispatch(spec, event) })
    }

    override fun onSpecRemoved(spec: JavafxEventSpec) {
        // ToDo: was:  ...accept(null)
        getEventHandlerConsumer(spec)(EventHandler {
            // nothing
        })
    }

    private fun getEventHandlerConsumer(eventSpec: JavafxEventSpec): Consumer<EventHandler<in MouseEvent>> {
        return when (eventSpec) {
            JavafxEventSpec.MOUSE_ENTERED -> { it -> myNode.onMouseEntered = it }
            JavafxEventSpec.MOUSE_EXITED -> { it -> myNode.onMouseExited = it }
            JavafxEventSpec.MOUSE_MOVED -> { it -> myNode.onMouseMoved = it }
            JavafxEventSpec.MOUSE_DRAGGED -> { it -> myNode.onMouseDragged = it }
            JavafxEventSpec.MOUSE_CLICKED -> { it -> myNode.onMouseClicked = it }
            JavafxEventSpec.MOUSE_PRESSED -> { it -> myNode.onMousePressed = it }
            JavafxEventSpec.MOUSE_RELEASED -> { it -> myNode.onMouseReleased = it }
        }
    }

    internal enum class JavafxEventSpec {
        MOUSE_ENTERED,
        MOUSE_EXITED,
        MOUSE_MOVED,
        MOUSE_DRAGGED,
        MOUSE_CLICKED,
        MOUSE_PRESSED,
        MOUSE_RELEASED
    }
}
