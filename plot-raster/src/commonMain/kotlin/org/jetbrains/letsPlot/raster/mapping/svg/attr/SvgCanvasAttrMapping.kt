package org.jetbrains.letsPlot.raster.mapping.svg.attr

import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure2
import org.jetbrains.letsPlot.core.plot.base.geom.LiveMapGeom
import org.jetbrains.letsPlot.raster.scene.CanvasNode

internal object SvgCanvasAttrMapping : SvgAttrMapping<CanvasNode>() {
    override fun setAttribute(target: CanvasNode, name: String, value: Any?) {
        when (name) {
            LiveMapGeom.SvgCanvasFigureElement.FIGURE.name -> target.content = value as? CanvasFigure2
            else -> super.setAttribute(target, name, value)
        }
    }

}