/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.awt

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.MouseEventSpec.*
import jetbrains.datalore.base.event.awt.AwtEventUtil
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.handler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.*
import jetbrains.datalore.vis.canvas.AnimationProvider.AnimationEventHandler
import jetbrains.datalore.vis.canvas.AnimationProvider.AnimationTimer
import jetbrains.datalore.vis.canvas.Canvas.Snapshot
import jetbrains.datalore.vis.canvas.CanvasUtil.drawGraphicsCanvasControl
import java.awt.Component
import java.awt.Graphics
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.SwingUtilities
import java.awt.event.MouseEvent as AwtMouseEvent

class AwtCanvasControl(graphicsCanvasControlFactory: GraphicsCanvasControlFactory, size: Vector) :
    CanvasControl {
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

    override fun createSnapshot(bytes: ByteArray): Async<Snapshot> {
        return myGraphicsCanvasControl.createSnapshot(bytes)
    }

    override fun addChild(canvas: Canvas) {
        myGraphicsCanvasControl.addChild(canvas)
    }

    override fun addChild(index: Int, canvas: Canvas) {
        myGraphicsCanvasControl.addChild(index, canvas)
    }

    override fun removeChild(canvas: Canvas) {
        myGraphicsCanvasControl.removeChild(canvas)
    }

    override fun <T> schedule(f: () -> T) {
        if (SwingUtilities.isEventDispatchThread()) {
            f()
        } else {
            SwingUtilities.invokeLater { f() }
        }
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
