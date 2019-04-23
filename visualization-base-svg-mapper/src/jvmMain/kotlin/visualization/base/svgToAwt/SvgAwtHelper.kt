package jetbrains.datalore.visualization.base.svgToAwt

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
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


abstract class SvgAwtHelper protected constructor() {
    private var myGraphicsNode: GraphicsNode? = null
    private var myMapper: SvgRootDocumentMapper? = null
    private val myUserAgent: UserAgent
    private var myBridgeContext: BridgeContext? = null
    private var mySvgRoot: SvgSvgElement? = null

    protected abstract val messageCallback: MessageCallback?

    val preferredSize: Dimension
        get() = Dimension(mySvgRoot!!.width().get()!!.toInt(), mySvgRoot!!.height().get()!!.toInt())

    init {
        myUserAgent = object : UserAgentAdapter() {
            private val dispatcher = AWTEventDispatcher()

            override fun getEventDispatcher(): EventDispatcher {
                return dispatcher
            }

            override fun displayMessage(message: String) {
                if (messageCallback != null) {
                    messageCallback!!.handleMessage(message)
                }
            }

            override fun displayError(e: Exception) {
                if (messageCallback != null) {
                    messageCallback!!.handleException(e)
                }
            }
        }
    }

    fun setSvg(svgRoot: SvgSvgElement) {
        clear()
        checkArgument(svgRoot.isAttached(), "SvgSvgElement must be attached to SvgNodeContainer")
        mySvgRoot = svgRoot
        createGraphicsNode()
    }

    protected fun clear() {
        if (mySvgRoot != null) {
            mySvgRoot = null
            myUserAgent.getEventDispatcher().setRootNode(null)

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
        myBridgeContext!!.setDynamic(true)

        myMapper = SvgRootDocumentMapper(mySvgRoot!!)
        myMapper!!.attachRoot()

        val builder = GVTBuilder()
        myGraphicsNode = builder.build(myBridgeContext, myMapper!!.target)

        myUserAgent.getEventDispatcher().setRootNode(myGraphicsNode)
    }

    fun paint(g: Graphics2D) {
        if (myGraphicsNode != null) {
            myGraphicsNode!!.paint(g)
        }
    }

    fun handleMouseEvent(e: MouseEvent) {
        myUserAgent.getEventDispatcher().dispatchEvent(e)
    }

    interface MessageCallback {
        fun handleMessage(message: String) {
            println(message)
        }

        fun handleException(e: Exception) {
            if (e is RuntimeException) {
                throw e
            }
            throw RuntimeException(e)
        }
    }
}
