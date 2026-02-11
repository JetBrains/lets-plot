package org.jetbrains.letsPlot.raster.mapping.svg.attr

import org.jetbrains.letsPlot.core.canvas.CanvasDrawable
import org.jetbrains.letsPlot.core.plot.base.geom.LiveMapGeom
import org.jetbrains.letsPlot.raster.scene.CanvasNode

internal object SvgCanvasAttrMapping : SvgAttrMapping<CanvasNode>() {
    override fun setAttribute(target: CanvasNode, name: String, value: Any?) {
        when (name) {
            LiveMapGeom.SvgCanvasDrawableElement.CONTENT.name -> target.content = value as? CanvasDrawable
            else -> super.setAttribute(target, name, value)
        }
    }

}