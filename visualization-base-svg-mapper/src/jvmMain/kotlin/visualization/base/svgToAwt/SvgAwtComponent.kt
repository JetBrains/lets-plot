package jetbrains.datalore.visualization.base.svgToAwt

import jetbrains.datalore.visualization.base.svg.*
import jetbrains.datalore.visualization.base.svg.event.SvgAttributeEvent
import jetbrains.datalore.visualization.base.svgToAwt.SvgAwtHelper.MessageCallback
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import javax.swing.JComponent

abstract class SvgAwtComponent protected constructor(svgRoot: SvgSvgElement) : JComponent() {
    private val myHelper: SvgAwtComponentHelper

    init {
        isFocusable = true

        myHelper = SvgAwtComponentHelper.forUnattached(svgRoot, createMessageCallback())

        myHelper.nodeContainer.addListener(object : SvgNodeContainerAdapter() {
            override fun onAttributeSet(element: SvgElement, event: SvgAttributeEvent<*>) {
                if (element === svgRoot && (SvgConstants.HEIGHT.equals(event.attrSpec.name, ignoreCase = true) || SvgConstants.WIDTH.equals(event.attrSpec.name, ignoreCase = true))) {
                    this@SvgAwtComponent.invalidate()
                }
                this@SvgAwtComponent.repaint()
            }

            override fun onNodeAttached(node: SvgNode) {
                this@SvgAwtComponent.repaint()
            }

            override fun onNodeDetached(node: SvgNode) {
                this@SvgAwtComponent.repaint()
            }
        })

        this.addMouseMotionListener(object : MouseMotionListener {
            override fun mouseDragged(e: MouseEvent) {
                myHelper.handleMouseEvent(e)
            }

            override fun mouseMoved(e: MouseEvent) {
                myHelper.handleMouseEvent(e)
            }
        })

        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                myHelper.handleMouseEvent(e)
            }

            override fun mousePressed(e: MouseEvent) {
                myHelper.handleMouseEvent(e)
            }

            override fun mouseReleased(e: MouseEvent) {
                myHelper.handleMouseEvent(e)
            }

            override fun mouseEntered(e: MouseEvent) {
                myHelper.handleMouseEvent(e)
            }

            override fun mouseExited(e: MouseEvent) {
                myHelper.handleMouseEvent(e)
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        myHelper.paint(g as Graphics2D)
    }

    override fun getPreferredSize(): Dimension {
        return myHelper.preferredSize
    }

    protected abstract fun createMessageCallback(): MessageCallback

    protected fun createDefaultMessageCallback(): MessageCallback {
        return object : MessageCallback {

        }
    }
}
