package jetbrains.livemap.core.input

import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.input.MouseEventType.*

class EventListenerComponent : EcsComponent {
    private val pressListeners = ArrayList<(InputMouseEvent) -> Unit>()
    private val clickListeners = ArrayList<(InputMouseEvent) -> Unit>()
    private val doubleClickListeners = ArrayList<(InputMouseEvent) -> Unit>()

    internal fun getListeners(type: MouseEventType): List<(InputMouseEvent) -> Unit> {
        return when (type) {
            PRESS -> pressListeners
            CLICK -> clickListeners
            DOUBLE_CLICK -> doubleClickListeners
        }
    }

    fun addPressListener(onPress: (InputMouseEvent) -> Unit) {
        pressListeners.add(onPress)
    }

    fun removePressListener() {
        pressListeners.clear()
    }

    fun removePressListener(onPress: (InputMouseEvent) -> Unit) {
        pressListeners.remove(onPress)
    }

    fun addClickListener(onClick: (InputMouseEvent) -> Unit) {
        clickListeners.add(onClick)
    }

    fun removeClickListener() {
        clickListeners.clear()
    }

    fun removeClickListener(onClick: (InputMouseEvent) -> Unit) {
        clickListeners.remove(onClick)
    }

    fun addDoubleClickListener(onDoubleClick: (InputMouseEvent) -> Unit) {
        doubleClickListeners.add(onDoubleClick)
    }

    fun removeDoubleClickListener() {
        doubleClickListeners.clear()
    }

    fun removeDoubleClickListener(onDoubleClick: (InputMouseEvent) -> Unit) {
        doubleClickListeners.remove(onDoubleClick)
    }
}