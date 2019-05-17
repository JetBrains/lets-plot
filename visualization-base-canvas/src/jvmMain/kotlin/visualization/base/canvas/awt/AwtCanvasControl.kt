package jetbrains.datalore.visualization.base.canvas.awt

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.datalore.visualization.base.canvas.CanvasControl
import jetbrains.datalore.visualization.base.canvas.CanvasUtil.drawGraphicsCanvasControl
import jetbrains.datalore.visualization.base.canvas.GraphicsCanvasControl
import jetbrains.datalore.visualization.base.canvas.GraphicsCanvasControlFactory
import java.awt.Graphics
import javax.swing.JComponent
import javax.swing.JPanel

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

    override fun createAnimationTimer(eventHandler: CanvasControl.AnimationEventHandler): CanvasControl.AnimationTimer {
        return myGraphicsCanvasControl.createAnimationTimer(eventHandler)
    }

    override fun addMouseEventHandler(eventSpec: CanvasControl.EventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return AwtCanvasUtil.addMouseEventHandler(myEventPeer, eventSpec, eventHandler)
    }

    override fun createCanvas(size: Vector): Canvas {
        return myGraphicsCanvasControl.createCanvas(size)
    }

    override fun addChild(canvas: Canvas) {
        myGraphicsCanvasControl.addChild(canvas)
    }

    override fun removeChild(canvas: Canvas) {
        myGraphicsCanvasControl.addChild(canvas)
    }
}
