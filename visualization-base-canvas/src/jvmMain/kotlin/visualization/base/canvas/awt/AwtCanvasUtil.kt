package jetbrains.datalore.visualization.base.canvas.awt

import jetbrains.datalore.base.event.Button
import jetbrains.datalore.base.event.KeyModifiers
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.function.Predicate
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.awt.AwtEventPeer.AwtEventSpec
import java.awt.event.MouseEvent

internal object AwtCanvasUtil {
    private val EVENT_SPEC_MAP = mapOf(
            MouseEventSpec.MOUSE_ENTERED to eventOptions(AwtEventSpec.MOUSE_ENTERED),
            MouseEventSpec.MOUSE_LEFT to eventOptions(AwtEventSpec.MOUSE_EXITED),
            MouseEventSpec.MOUSE_MOVED to eventOptions(AwtEventSpec.MOUSE_MOVED),
            MouseEventSpec.MOUSE_DRAGGED to eventOptions(AwtEventSpec.MOUSE_DRAGGED),
            MouseEventSpec.MOUSE_CLICKED to eventOptions(AwtEventSpec.MOUSE_CLICKED) { e -> e.clickCount % 2 == 1 },
            MouseEventSpec.MOUSE_DOUBLE_CLICKED to eventOptions(AwtEventSpec.MOUSE_CLICKED) { e -> e.clickCount % 2 == 0 },
            MouseEventSpec.MOUSE_PRESSED to eventOptions(AwtEventSpec.MOUSE_PRESSED),
            MouseEventSpec.MOUSE_RELEASED to eventOptions(AwtEventSpec.MOUSE_RELEASED)
    )

    private val MOUSE_BUTTON_MAP = mapOf(
            MouseEvent.NOBUTTON to Button.NONE,
            MouseEvent.BUTTON1 to Button.LEFT,
            MouseEvent.BUTTON2 to Button.MIDDLE,
            MouseEvent.BUTTON3 to Button.RIGHT
    )

    fun addMouseEventHandler(
            eventPeer: AwtEventPeer,
            eventSpec: MouseEventSpec,
            eventHandler: EventHandler<jetbrains.datalore.base.event.MouseEvent>
    ): Registration {
        val eventOptions = EVENT_SPEC_MAP[eventSpec]
        return eventPeer.addEventHandler(eventOptions!!.spec, object : EventHandler<MouseEvent> {
            override fun onEvent(event: MouseEvent) {
                if (eventOptions.predicate(event)) {
                    eventHandler.onEvent(createMouseEvent(event))
                }
            }
        })
    }

    private fun eventOptions(eventSpec: AwtEventSpec, eventPredicate: Predicate<MouseEvent>): AwtEventOptions {
        return AwtEventOptions(eventSpec, eventPredicate)
    }

    private fun eventOptions(eventSpec: AwtEventSpec): AwtEventOptions {
        return AwtEventOptions(eventSpec) { true }
    }

    private fun createMouseEvent(e: MouseEvent): jetbrains.datalore.base.event.MouseEvent {
        return jetbrains.datalore.base.event.MouseEvent(
                round(e.x.toDouble()),
                round(e.y.toDouble()),
                MOUSE_BUTTON_MAP[e.button],
                createKeyModifiers(e)
        )
    }

    private fun round(v: Double): Int {
        return kotlin.math.round(v).toInt()
    }

    private fun createKeyModifiers(e: MouseEvent): KeyModifiers {
        return KeyModifiers(e.isControlDown, e.isAltDown, e.isShiftDown, e.isMetaDown)
    }

    private class AwtEventOptions internal constructor(internal val spec: AwtEventSpec, internal val predicate: Predicate<MouseEvent>)
}
