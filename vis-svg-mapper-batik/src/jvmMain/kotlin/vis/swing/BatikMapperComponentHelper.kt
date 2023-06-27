/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing

import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Registration
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNodeContainer
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNodeContainerListener
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import jetbrains.datalore.vis.svgMapper.batik.SvgRootDocumentMapper
import jetbrains.datalore.vis.swing.BatikMapperComponent.Companion.DEBUG_REPAINT_MAPPER_COMPONENT
import jetbrains.datalore.vis.swing.BatikMapperComponent.Companion.USE_WEIRD_PERFORMANCE_TUNEUP
import org.apache.batik.bridge.BridgeContext
import org.apache.batik.bridge.GVTBuilder
import org.apache.batik.bridge.UserAgent
import org.apache.batik.bridge.UserAgentAdapter
import org.apache.batik.ext.awt.RenderingHintsKeyExt
import org.apache.batik.gvt.RootGraphicsNode
import org.apache.batik.gvt.event.AWTEventDispatcher
import org.apache.batik.gvt.event.EventDispatcher
import org.apache.batik.gvt.event.GraphicsNodeChangeListener
import java.awt.AlphaComposite
import java.awt.Dimension
import java.awt.Graphics2D
import kotlin.math.ceil


class BatikMapperComponentHelper private constructor(
    private val svgRoot: SvgSvgElement,
    val messageCallback: BatikMessageCallback
) {
    private val nodeContainer = SvgNodeContainer(svgRoot)
    private val registrations = CompositeRegistration()

    private val myGraphicsNode: RootGraphicsNode
    private val myMapper: SvgRootDocumentMapper
    private val myUserAgent: UserAgent
    private val myBridgeContext: BridgeContext
    private val myRenderer: BatikGraphicsNodeRenderer

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
        myGraphicsNode = builder.build(myBridgeContext, myMapper.target) as RootGraphicsNode
        GraphicsNodeInitializer.initialize(myGraphicsNode)

        myUserAgent.eventDispatcher.rootNode = myGraphicsNode

        myRenderer = BatikGraphicsNodeRenderer.getInstance()
    }

    internal fun addSvgNodeContainerListener(l: SvgNodeContainerListener) {
        registrations.add(
            nodeContainer.addListener(l)
        )
    }

    internal fun addGraphicsNodeChangeListener(l: GraphicsNodeChangeListener) {
        myGraphicsNode.addTreeGraphicsNodeChangeListener(l)
        registrations.add(
            object : Registration() {
                override fun doRemove() {
                    myGraphicsNode.removeTreeGraphicsNodeChangeListener(l)
                }
            }
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
//        println("Batik mapper paint clip: ${g.clip}")

        if (USE_WEIRD_PERFORMANCE_TUNEUP) {
            // By default, when painting graphic nodes, Batik
            // repeatedly copies massive raster data offscrin.
            // (see various "copy" methods in org.apache.batik.ext.awt.image.GraphicsUtil)
            // Disabling this behavior improve (tooltip) performance dramatically.

            // This makes function AbstractGraphicsNode.isOffscreenBufferNeeded() to return FALSE.
            g.composite = AlphaComposite.SrcOver

            // This makes function AbstractGraphicsNode.isAntialiasedClip() to return FALSE.
            g.setRenderingHint(RenderingHintsKeyExt.KEY_TRANSCODING, RenderingHintsKeyExt.VALUE_TRANSCODING_VECTOR)
        }

        myRenderer.paint(myGraphicsNode, g, preferredSize)

        if (DEBUG_REPAINT_MAPPER_COMPONENT) {
            val clip = g.clip
            if (clip != null) {
                g.color = java.awt.Color.RED
                g.draw(clip)
            }
        }
    }

    companion object {
        fun forUnattached(
            svgRoot: SvgSvgElement,
            messageCallback: BatikMessageCallback
        ): BatikMapperComponentHelper {
            require(!svgRoot.isAttached()) { "SvgSvgElement must be unattached" }
            return BatikMapperComponentHelper(svgRoot, messageCallback)
        }
    }
}
