package jetbrains.datalore.visualization.base.canvas.awt

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.MouseEventSpec.*
import jetbrains.datalore.base.event.awt.AwtEventUtil
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.handler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.*
import jetbrains.datalore.visualization.base.canvas.AnimationProvider.AnimationEventHandler
import jetbrains.datalore.visualization.base.canvas.AnimationProvider.AnimationTimer
import jetbrains.datalore.visualization.base.canvas.Canvas.Snapshot
import jetbrains.datalore.visualization.base.canvas.CanvasUtil.drawGraphicsCanvasControl
import java.awt.Component
import java.awt.Graphics
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import javax.swing.JComponent
import javax.swing.JPanel
import java.awt.event.MouseEvent as AwtMouseEvent

class AwtCanvasControl(graphicsCanvasControlFactory: GraphicsCanvasControlFactory, size: Vector) : CanvasControl {
    private lateinit var myGraphicsCanvasControl: GraphicsCanvasControl
    private val myEventPeer: AwtEventPeer
    val component: JComponent

    override val size: Vector
        get() = myGraphicsCanvasControl.size

    init {
        component = object : JPanel() {
            override fun paint(g: Graphics?) {
                super.paint(g)
                drawGraphicsCanvasControl(myGraphicsCanvasControl, g!!)
            }
        }
        myGraphicsCanvasControl = graphicsCanvasControlFactory.create(size, Runnable { component.repaint() })
        myEventPeer = AwtEventPeer(component)
    }

    override fun createAnimationTimer(eventHandler: AnimationEventHandler): AnimationTimer {
        return myGraphicsCanvasControl.createAnimationTimer(eventHandler)
    }

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return myEventPeer.addEventHandler(
            eventSpec,
            handler {
                eventHandler.onEvent(AwtEventUtil.translate(it))
            }
        )
    }

    override fun createCanvas(size: Vector): Canvas {
        return myGraphicsCanvasControl.createCanvas(size)
    }

    override fun createSnapshot(dataUrl: String): Async<Snapshot> {
        return myGraphicsCanvasControl.createSnapshot(dataUrl)
    }

    override fun addChild(canvas: Canvas) {
        myGraphicsCanvasControl.addChild(canvas)
    }

    override fun removeChild(canvas: Canvas) {
        myGraphicsCanvasControl.removeChild(canvas)
    }

    private class AwtEventPeer(component: Component) :
        EventPeer<MouseEventSpec, AwtMouseEvent>(MouseEventSpec::class),
        MouseListener,
        MouseMotionListener {

        init {
            component.addMouseListener(this)
            component.addMouseMotionListener(this)
        }

        override fun onSpecAdded(spec: MouseEventSpec) {}

        override fun onSpecRemoved(spec: MouseEventSpec) {}

        override fun mouseClicked(e: AwtMouseEvent) {
            if (e.clickCount % 2 == 1) {
                dispatch(MOUSE_CLICKED, e)
            } else {
                dispatch(MOUSE_DOUBLE_CLICKED, e)
            }
        }

        override fun mousePressed(e: AwtMouseEvent) {
            dispatch(MOUSE_PRESSED, e)
        }

        override fun mouseReleased(e: AwtMouseEvent) {
            dispatch(MOUSE_RELEASED, e)
        }

        override fun mouseEntered(e: AwtMouseEvent) {
            dispatch(MOUSE_ENTERED, e)
        }

        override fun mouseExited(e: AwtMouseEvent) {
            dispatch(MOUSE_LEFT, e)
        }

        override fun mouseDragged(e: AwtMouseEvent) {
            dispatch(MOUSE_DRAGGED, e)
        }

        override fun mouseMoved(e: AwtMouseEvent) {
            dispatch(MOUSE_MOVED, e)
        }
    }
}
