package org.jetbrains.letsPlot.raster.mapping.svg.attr

import org.jetbrains.letsPlot.core.plot.base.geom.LiveMapGeom
import org.jetbrains.letsPlot.raster.scene.CanvasFigure

internal object SvgCanvasAttrMapping : SvgAttrMapping<CanvasFigure>() {
    override fun setAttribute(target: CanvasFigure, name: String, value: Any?) {
        when (name) {
            LiveMapGeom.SvgCanvasFigureElement.FIGURE.name -> target.canvasFigure = value as? org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure2
            else -> super.setAttribute(target, name, value)
        }
    }

}