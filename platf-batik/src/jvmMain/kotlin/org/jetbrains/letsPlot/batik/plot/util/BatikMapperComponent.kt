/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.batik.plot.util

import org.apache.batik.gvt.event.GraphicsNodeChangeEvent
import org.apache.batik.gvt.event.GraphicsNodeChangeListener
import org.jetbrains.letsPlot.awt.util.AwtContainerDisposer
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.DisposableRegistration
import org.jetbrains.letsPlot.commons.registration.DisposingHub
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.event.SvgAttributeEvent
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
    private val repaintManager = BatikMapperComponentRepaintManager(this, 10)

    init {
        isFocusable = true
//        background = Color(0, 0, 0, 0)
        isOpaque = false
        cursor = Cursor(Cursor.CROSSHAIR_CURSOR)
        layout = null  // Composite figure contains sub-panels with provided bounds.

        myHelper = BatikMapperComponentHelper.forUnattached(svgRoot, messageCallback)

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

                if (!USE_NEW_REPAINT_MANAGER) {
                    this@BatikMapperComponent.parent.repaint()
                }
            }

            override fun onNodeAttached(node: SvgNode) {
                if (!USE_NEW_REPAINT_MANAGER) {
                    this@BatikMapperComponent.parent.repaint()
                }
            }

            override fun onNodeDetached(node: SvgNode) {
                if (!USE_NEW_REPAINT_MANAGER) {
                    this@BatikMapperComponent.parent.repaint()
                }
            }
        })

        if (USE_NEW_REPAINT_MANAGER) {
            myHelper.addGraphicsNodeChangeListener(
                object : GraphicsNodeChangeListener {
                    override fun changeStarted(gnce: GraphicsNodeChangeEvent?) {
                        repaintManager.repaintNode(gnce!!.graphicsNode)
                    }

                    override fun changeCompleted(gnce: GraphicsNodeChangeEvent?) {
                        repaintManager.repaintNode(gnce!!.graphicsNode)
                    }
                }
            )
        }
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
        repaintManager.stop()
        myHelper.dispose()
        registrations.dispose()

        AwtContainerDisposer(this).dispose()
    }

    companion object {

        internal const val USE_NEW_REPAINT_MANAGER = true
        internal const val USE_WEIRD_PERFORMANCE_TUNEUP = true
        internal const val DEBUG_REPAINT_MAPPER_COMPONENT = false

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
