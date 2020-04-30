/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.vis.svg.SvgNodeContainer
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
import java.awt.event.MouseEvent


class BatikMapperComponentHelper private constructor(
    val nodeContainer: SvgNodeContainer,
    val messageCallback: BatikMessageCallback
) {

    private var myGraphicsNode: GraphicsNode? = null
    private var myMapper: SvgRootDocumentMapper? = null
    private val myUserAgent: UserAgent
    private var myBridgeContext: BridgeContext? = null
    private var mySvgRoot: SvgSvgElement? = null


    val preferredSize: Dimension
        get() {
            val w = mySvgRoot!!.width().get()?.toInt() ?: throw IllegalStateException("SVG width is not defined")
            val h = mySvgRoot!!.height().get()?.toInt() ?: throw IllegalStateException("SVG height is not defined")
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
    }

    private fun setSvg(svgRoot: SvgSvgElement) {
        clear()
        checkArgument(svgRoot.isAttached(), "SvgSvgElement must be attached to SvgNodeContainer")
        mySvgRoot = svgRoot
        createGraphicsNode()
    }

    internal fun clear() {
        if (mySvgRoot != null) {
            mySvgRoot = null
            myUserAgent.eventDispatcher.rootNode = null

            if (myMapper!!.isAttached) {
                myMapper!!.detachRoot()
            }
            myMapper = null

            myBridgeContext!!.dispose()
            myBridgeContext = null

            myGraphicsNode = null
        }
    }

    private fun createGraphicsNode() {
        if (mySvgRoot == null) return

        myBridgeContext = BridgeContext(myUserAgent)
        myBridgeContext!!.isDynamic = true

        // Build Batik SVG model.
        myMapper = SvgRootDocumentMapper(mySvgRoot!!)
        myMapper!!.attachRoot()

        // Build graphic nodes
        val builder = GVTBuilder()
        myGraphicsNode = builder.build(myBridgeContext, myMapper!!.target)

        myUserAgent.eventDispatcher.rootNode = myGraphicsNode
    }

    fun paint(g: Graphics2D) {
        if (myGraphicsNode != null) {
            myGraphicsNode!!.paint(g)
        }
    }

    fun handleMouseEvent(e: MouseEvent) {
        myUserAgent.eventDispatcher.dispatchEvent(e)
    }


    companion object {
        fun forUnattached(svgRoot: SvgSvgElement, messageCallback: BatikMessageCallback): BatikMapperComponentHelper {
            checkArgument(!svgRoot.isAttached(), "SvgSvgElement must be unattached")
            // element must be attached
            val nodeContainer = SvgNodeContainer(svgRoot)
            val helper = BatikMapperComponentHelper(nodeContainer, messageCallback)
            helper.setSvg(svgRoot)
            return helper
        }
    }
}
