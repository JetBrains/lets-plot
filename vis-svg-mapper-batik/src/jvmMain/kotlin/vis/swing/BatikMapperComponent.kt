package jetbrains.datalore.vis.swing

import jetbrains.datalore.vis.svg.*
import jetbrains.datalore.vis.svg.event.SvgAttributeEvent
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import javax.swing.JComponent

class BatikMapperComponent(
    svgRoot: SvgSvgElement,
    messageCallback: BatikMessageCallback
) : JComponent() {

    private val myHelper: BatikMapperComponentHelper

    init {
        isFocusable = true

        myHelper =
            BatikMapperComponentHelper.forUnattached(svgRoot, messageCallback)

        myHelper.nodeContainer.addListener(object : SvgNodeContainerAdapter() {
            override fun onAttributeSet(element: SvgElement, event: SvgAttributeEvent<*>) {
                if (element === svgRoot && (SvgConstants.HEIGHT.equals(event.attrSpec.name, ignoreCase = true) || SvgConstants.WIDTH.equals(event.attrSpec.name, ignoreCase = true))) {
                    this@BatikMapperComponent.invalidate()
                }
                this@BatikMapperComponent.repaint()
            }

            override fun onNodeAttached(node: SvgNode) {
                this@BatikMapperComponent.repaint()
            }

            override fun onNodeDetached(node: SvgNode) {
                this@BatikMapperComponent.repaint()
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

    companion object {
        val DEF_MESSAGE_CALLBACK = object : BatikMessageCallback {
            override fun handleMessage(message: String) {
                println(message)
            }

            override fun handleException(e: Exception) {
                if (e is RuntimeException) {
                    throw e
                }
                throw RuntimeException(e)
            }
        }
    }
}
