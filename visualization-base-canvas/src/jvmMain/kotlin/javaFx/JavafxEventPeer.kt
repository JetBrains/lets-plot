package jetbrains.datalore.visualization.base.canvas.javaFx

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import jetbrains.datalore.visualization.base.canvas.EventPeer
import java.util.function.Consumer

internal class JavafxEventPeer(private val myNode: Node) :
        EventPeer<JavafxEventPeer.JavafxEventSpec, MouseEvent>(JavafxEventSpec::class) {

    override fun onSpecAdded(spec: JavafxEventSpec) {
        getEventHandlerConsumer(spec).accept(EventHandler { event -> this@JavafxEventPeer.dispatch(spec, event) })
    }

    override fun onSpecRemoved(spec: JavafxEventSpec) {
        // ToDo: was:  ...accept(null)
        getEventHandlerConsumer(spec).accept(EventHandler {
            // nothing
        })
    }

    private fun getEventHandlerConsumer(eventSpec: JavafxEventSpec): Consumer<EventHandler<in MouseEvent>> {
        return when (eventSpec) {
            JavafxEventSpec.MOUSE_ENTERED -> Consumer { myNode.onMouseEntered = it }
            JavafxEventSpec.MOUSE_EXITED -> Consumer { myNode.onMouseExited = it }
            JavafxEventSpec.MOUSE_MOVED -> Consumer { myNode.onMouseMoved = it }
            JavafxEventSpec.MOUSE_DRAGGED -> Consumer { myNode.onMouseDragged = it }
            JavafxEventSpec.MOUSE_CLICKED -> Consumer { myNode.onMouseClicked = it }
            JavafxEventSpec.MOUSE_PRESSED -> Consumer { myNode.onMousePressed = it }
            JavafxEventSpec.MOUSE_RELEASED -> Consumer { myNode.onMouseReleased = it }
        }
        throw IllegalStateException("Unknown JavafxEventSpec $eventSpec")
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
