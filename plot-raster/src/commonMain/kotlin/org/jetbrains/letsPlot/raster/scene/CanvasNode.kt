package org.jetbrains.letsPlot.raster.scene

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure2

internal class CanvasNode : Node() {
    var content: CanvasFigure2? by variableAttr(null)

    private var reg = Registration.EMPTY

    private val attachedContent by derivedAttr {
        val content = this@CanvasNode.content ?: return@derivedAttr null
        val peer = peer ?: return@derivedAttr null

        reg.remove()
        reg = CompositeRegistration(
            content.mapToCanvas(peer.canvasPeer),
            content.onRepaintRequested {
                markDirty()
                requestRepaint()
            }
        )

        content
    }

    override fun render(ctx: Context2d) {
        content?.paint(ctx)
    }

    override fun calculateLocalBBox(): DoubleRectangle {
        if (content == null) {
            return DoubleRectangle.ZERO
        }

        return DoubleRectangle.WH(content!!.size)
    }

    override fun onDetach() {
        reg.remove()
    }

    companion object {
        val CLASS = ATTRIBUTE_REGISTRY.addClass(CanvasNode::class)

        val CanvasNodeAttrSpec = CLASS.registerVariableAttr(CanvasNode::content, affectsBBox = true)
        val AttachedFigureAttrSpec = CLASS.registerDerivedAttr(CanvasNode::attachedContent, setOf(CanvasNodeAttrSpec, PeerAttrSpec))
    }
}