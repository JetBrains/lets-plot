package jetbrains.datalore.visualization.plot.gog.core.render.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.SvgRectElement
import jetbrains.datalore.visualization.plot.gog.core.render.AestheticsUtil
import jetbrains.datalore.visualization.plot.gog.core.render.DataPointAesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.LegendKeyElementFactory

class FilledSquareLegendKeyElementFactory : LegendKeyElementFactory {
    override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
        val rect = SvgRectElement(0.0, 0.0, size.x, size.y)
        AestheticsUtil.updateFill(rect, p)
        val g = SvgGElement()
        g.children().add(rect)
        return g
    }
}
