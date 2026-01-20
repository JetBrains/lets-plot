package org.jetbrains.letsPlot.raster.scene

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvasFigure.AsyncRenderer
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure2

internal class CanvasNode : Node(), AsyncRenderer {
    var content: CanvasFigure2? by variableAttr(null)
    private var onReadyListeners = mutableListOf<() -> Unit>()

    private var reg = Registration.EMPTY

    private val attachedContent by derivedAttr {
        val content = this@CanvasNode.content ?: return@derivedAttr null
        val peer = peer ?: return@derivedAttr null

        reg.remove()
        reg = CompositeRegistration(
            content.mapToCanvas(peer.canvasPeer),
            content.onReady { onContentReady() },
            content.onRepaintRequested {
                markDirty()
                requestRepaint()
            }
        )

        content
    }

    override fun render(ctx: Context2d) {
        attachedContent?.paint(ctx)
    }

    override fun calculateLocalBBox(): DoubleRectangle {
        if (attachedContent == null) {
            return DoubleRectangle.ZERO
        }

        return DoubleRectangle.WH(attachedContent!!.size)
    }

    override fun onDetach() {
        reg.remove()
    }

    override fun isReady(): Boolean {
        return attachedContent?.isReady() ?: true
    }

    override fun onReady(listener: () -> Unit): Registration {
        onReadyListeners.add(listener)
        return Registration.onRemove {
            onReadyListeners.remove(listener)
        }
    }

    private fun onContentReady() {
        onReadyListeners.forEach { it() }
    }

    companion object {
        val CLASS = ATTRIBUTE_REGISTRY.addClass(CanvasNode::class)

        val CanvasNodeAttrSpec = CLASS.registerVariableAttr(CanvasNode::content, affectsBBox = true)
        val AttachedFigureAttrSpec = CLASS.registerDerivedAttr(CanvasNode::attachedContent, setOf(CanvasNodeAttrSpec, PeerAttrSpec))
    }
}