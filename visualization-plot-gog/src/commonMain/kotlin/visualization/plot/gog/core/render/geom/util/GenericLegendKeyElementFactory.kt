package jetbrains.datalore.visualization.plot.gog.core.render.geom.util

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.SvgLineElement
import jetbrains.datalore.visualization.base.svg.SvgRectElement
import jetbrains.datalore.visualization.plot.gog.core.render.AestheticsUtil
import jetbrains.datalore.visualization.plot.gog.core.render.DataPointAesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.LegendKeyElementFactory

class GenericLegendKeyElementFactory : LegendKeyElementFactory {
    override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
        // background rect (to show fill)
        val rect = SvgRectElement(0.0, 0.0, size.x, size.y)
        AestheticsUtil.updateFill(rect, p)

        // slash-line (to show stroke)
        val line = SvgLineElement(0.0, size.y, size.x, 0.0)
        GeomHelper.decorate(line, p)

        val g = SvgGElement()
        g.children().add(rect)
        g.children().add(line)
        return g
    }
}
