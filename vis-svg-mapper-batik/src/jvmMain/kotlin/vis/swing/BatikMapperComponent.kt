/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing

import jetbrains.datalore.base.awt.AwtContainerDisposer
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.DisposableRegistration
import jetbrains.datalore.base.registration.DisposingHub
import jetbrains.datalore.vis.svg.*
import jetbrains.datalore.vis.svg.event.SvgAttributeEvent
import java.awt.Cursor
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel

class BatikMapperComponent(
    svgRoot: SvgSvgElement,
    messageCallback: BatikMessageCallback
) : JPanel(), Disposable, DisposingHub {

    private val myHelper: BatikMapperComponentHelper
    private var isDisposed: Boolean = false

    private val registrations = CompositeRegistration()

    init {
        isFocusable = true
//        background = Color(0, 0, 0, 0)
        isOpaque = false
        cursor = Cursor(Cursor.CROSSHAIR_CURSOR)
        layout = null  // Composite figure contains sub-panels with provided bounds.

        myHelper = BatikMapperComponentHelper.forUnattached(svgRoot, messageCallback)

//        registrations.add(
//            myHelper.nodeContainer
            myHelper.addSvgNodeContainerListener(object : SvgNodeContainerAdapter() {
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
//        )
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        myHelper.paint(g as Graphics2D)
    }

    override fun getPreferredSize(): Dimension {
        return myHelper.preferredSize
    }

    override fun registerDisposable(disposable: Disposable) {
        registrations.add(DisposableRegistration(disposable))
    }

    override fun dispose() {
        require(!isDisposed) { "Alreadey disposed." }
        myHelper.dispose()
        registrations.dispose()

        AwtContainerDisposer(this).dispose()
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
