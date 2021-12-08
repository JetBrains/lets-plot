/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing

import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.vis.svg.*
import jetbrains.datalore.vis.svg.event.SvgAttributeEvent
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel

class BatikMapperComponent(
    svgRoot: SvgSvgElement,
    messageCallback: BatikMessageCallback
) : JPanel(), Disposable {

    private val myHelper: BatikMapperComponentHelper
    private var myIsDisposed: Boolean = false

    private val registrations = CompositeRegistration()

    init {
        isFocusable = true
//        background = Color(0, 0, 0, 0)
        isOpaque = false

        myHelper = BatikMapperComponentHelper.forUnattached(svgRoot, messageCallback)

        registrations.add(
            myHelper.nodeContainer
                .addListener(object : SvgNodeContainerAdapter() {
                    override fun onAttributeSet(element: SvgElement, event: SvgAttributeEvent<*>) {
                        if (element === svgRoot && (SvgConstants.HEIGHT.equals(
                                event.attrSpec.name,
                                ignoreCase = true
                            ) || SvgConstants.WIDTH.equals(event.attrSpec.name, ignoreCase = true))
                        ) {
                            println("onAttributeSet: ${event.attrSpec.name}")
                            this@BatikMapperComponent.invalidate()
                        }
                        this@BatikMapperComponent.parent.repaint()
                    }

                    override fun onNodeAttached(node: SvgNode) {
                        this@BatikMapperComponent.parent.repaint()
                    }

                    override fun onNodeDetached(node: SvgNode) {
                        this@BatikMapperComponent.parent.repaint()
                    }
                })
        )
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
        registrations.dispose()
        myHelper.dispose()
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
