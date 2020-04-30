/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing

import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.vis.svg.*
import jetbrains.datalore.vis.svg.event.SvgAttributeEvent
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import javax.swing.JPanel

class BatikMapperComponent(
    svgRoot: SvgSvgElement,
    messageCallback: BatikMessageCallback
) : JPanel(), Disposable {

    private val myHelper: BatikMapperComponentHelper
    private var myIsDisposed: Boolean = false

    init {
        isFocusable = true

        myHelper =
            BatikMapperComponentHelper.forUnattached(svgRoot, messageCallback)

        myHelper.nodeContainer.addListener(object : SvgNodeContainerAdapter() {
            override fun onAttributeSet(element: SvgElement, event: SvgAttributeEvent<*>) {
                if (element === svgRoot && (SvgConstants.HEIGHT.equals(
                        event.attrSpec.name,
                        ignoreCase = true
                    ) || SvgConstants.WIDTH.equals(event.attrSpec.name, ignoreCase = true))
                ) {
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

    override fun dispose() {
        require(!myIsDisposed) { "Alreadey disposed." }
        myHelper.clear()
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
