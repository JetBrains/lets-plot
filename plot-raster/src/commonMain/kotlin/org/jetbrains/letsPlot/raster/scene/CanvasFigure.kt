package org.jetbrains.letsPlot.raster.scene

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure2

internal class CanvasFigure : Node() {
    var canvasFigure: CanvasFigure2? by variableAttr(null)

    private var reg = Registration.EMPTY

    private val attachedFigure by derivedAttr {
        val canvasFigure = canvasFigure ?: return@derivedAttr null
        val peer = peer ?: return@derivedAttr null

        reg.remove()
        reg = canvasFigure.mapToCanvas(peer.canvasPeer)

        canvasFigure
    }

    override fun render(ctx: Context2d) {
        attachedFigure?.paint(ctx)
    }

    override fun calculateLocalBBox(): DoubleRectangle {
        if (canvasFigure == null) {
            return DoubleRectangle.ZERO
        }

        return DoubleRectangle.WH(canvasFigure!!.size)
    }

    override fun onDetach() {
        reg.remove()
    }

    companion object {
        val CLASS = ATTRIBUTE_REGISTRY.addClass(CanvasFigure::class)

        val CanvasFigureAttrSpec = CLASS.registerVariableAttr(CanvasFigure::canvasFigure, affectsBBox = true)
        val AttachedFigureAttrSpec = CLASS.registerDerivedAttr(CanvasFigure::attachedFigure, setOf(CanvasFigureAttrSpec, PeerAttrSpec))
    }
}