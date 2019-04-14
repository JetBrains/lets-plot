package jetbrains.datalore.visualization.base.canvas.awt

import jetbrains.datalore.visualization.base.canvas.EventPeer
import jetbrains.datalore.visualization.base.canvas.awt.AwtEventPeer.AwtEventSpec

import java.awt.Component
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener

import jetbrains.datalore.visualization.base.canvas.awt.AwtEventPeer.AwtEventSpec.MOUSE_CLICKED
import jetbrains.datalore.visualization.base.canvas.awt.AwtEventPeer.AwtEventSpec.MOUSE_DRAGGED
import jetbrains.datalore.visualization.base.canvas.awt.AwtEventPeer.AwtEventSpec.MOUSE_ENTERED
import jetbrains.datalore.visualization.base.canvas.awt.AwtEventPeer.AwtEventSpec.MOUSE_EXITED
import jetbrains.datalore.visualization.base.canvas.awt.AwtEventPeer.AwtEventSpec.MOUSE_MOVED
import jetbrains.datalore.visualization.base.canvas.awt.AwtEventPeer.AwtEventSpec.MOUSE_PRESSED
import jetbrains.datalore.visualization.base.canvas.awt.AwtEventPeer.AwtEventSpec.MOUSE_RELEASED

internal class AwtEventPeer(component: Component) :
        EventPeer<AwtEventSpec, MouseEvent>(AwtEventSpec::class), MouseListener, MouseMotionListener {

    init {
        component.addMouseListener(this)
        component.addMouseMotionListener(this)
    }

    override fun onSpecAdded(spec: AwtEventSpec) {}

    override fun onSpecRemoved(spec: AwtEventSpec) {}

    override fun mouseClicked(e: MouseEvent) {
        dispatch(MOUSE_CLICKED, e)
    }

    override fun mousePressed(e: MouseEvent) {
        dispatch(MOUSE_PRESSED, e)
    }

    override fun mouseReleased(e: MouseEvent) {
        dispatch(MOUSE_RELEASED, e)
    }

    override fun mouseEntered(e: MouseEvent) {
        dispatch(MOUSE_ENTERED, e)
    }

    override fun mouseExited(e: MouseEvent) {
        dispatch(MOUSE_EXITED, e)
    }

    override fun mouseDragged(e: MouseEvent) {
        dispatch(MOUSE_DRAGGED, e)
    }

    override fun mouseMoved(e: MouseEvent) {
        dispatch(MOUSE_MOVED, e)
    }

    internal enum class AwtEventSpec {
        MOUSE_ENTERED,
        MOUSE_EXITED,
        MOUSE_MOVED,
        MOUSE_DRAGGED,
        MOUSE_CLICKED,
        MOUSE_PRESSED,
        MOUSE_RELEASED
    }
}
