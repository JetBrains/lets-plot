/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing

import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.vis.svg.SvgNodeContainer
import jetbrains.datalore.vis.svg.SvgNodeContainerListener
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svgMapper.batik.SvgRootDocumentMapper
import org.apache.batik.bridge.BridgeContext
import org.apache.batik.bridge.GVTBuilder
import org.apache.batik.bridge.UserAgent
import org.apache.batik.bridge.UserAgentAdapter
import org.apache.batik.gvt.GraphicsNode
import org.apache.batik.gvt.event.AWTEventDispatcher
import org.apache.batik.gvt.event.EventDispatcher
import java.awt.Dimension
import java.awt.Graphics2D
import kotlin.math.ceil


class BatikMapperComponentHelper private constructor(
    private val svgRoot: SvgSvgElement,
    val messageCallback: BatikMessageCallback
) {
    private val nodeContainer = SvgNodeContainer(svgRoot)
    private val registrations = CompositeRegistration()

    private val myGraphicsNode: GraphicsNode
    private val myMapper: SvgRootDocumentMapper
    private val myUserAgent: UserAgent
    private val myBridgeContext: BridgeContext

    val preferredSize: Dimension
        get() {
            val w = svgRoot.width().get()?.let {
                ceil(it).toInt()
            } ?: throw IllegalStateException("SVG width is not defined")
            val h = svgRoot.height().get()?.let {
                ceil(it).toInt()
            } ?: throw IllegalStateException("SVG height is not defined")
            return Dimension(w, h)
        }

    init {
        myUserAgent = object : UserAgentAdapter() {
            private val dispatcher = AWTEventDispatcher()

            override fun getEventDispatcher(): EventDispatcher {
                return dispatcher
            }

            override fun displayMessage(message: String) {
                messageCallback.handleMessage(message)
            }

            override fun displayError(e: Exception) {
                messageCallback.handleException(e)
            }
        }

        // Set-up GraphicsNode
        myBridgeContext = BridgeContext(myUserAgent)
        myBridgeContext.isDynamic = true

        // Build Batik SVG model.
        myMapper = SvgRootDocumentMapper(svgRoot)
        myMapper.attachRoot()

        // Build graphic nodes
        val builder = GVTBuilder()
        myGraphicsNode = builder.build(myBridgeContext, myMapper.target)

        myUserAgent.eventDispatcher.rootNode = myGraphicsNode
    }

    internal fun addSvgNodeContainerListener(l: SvgNodeContainerListener) {
        registrations.add(
            nodeContainer.addListener(l)
        )
    }

    internal fun dispose() {
        registrations.dispose()

        if (myMapper.isAttached) {
            myMapper.detachRoot()

            // Detach current Svg root.
            nodeContainer.root().set(SvgSvgElement())

            myUserAgent.eventDispatcher.rootNode = null

            myBridgeContext.dispose()
        }
    }

    fun paint(g: Graphics2D) {
        myGraphicsNode.paint(g)
    }

//    fun handleMouseEvent(e: MouseEvent) {
//        myUserAgent.eventDispatcher.dispatchEvent(e)
//    }

    companion object {
        fun forUnattached(svgRoot: SvgSvgElement, messageCallback: BatikMessageCallback): BatikMapperComponentHelper {
            require(!svgRoot.isAttached()) { "SvgSvgElement must be unattached" }
            return BatikMapperComponentHelper(svgRoot, messageCallback)
        }
    }
}
