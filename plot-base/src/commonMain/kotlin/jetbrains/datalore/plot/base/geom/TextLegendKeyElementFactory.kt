package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.visualization.base.svg.SvgGElement

internal class TextLegendKeyElementFactory : LegendKeyElementFactory {

    override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
        val label = TextLabel("a")
        GeomHelper.decorate(label, p)
        label.setHorizontalAnchor(TextLabel.HorizontalAnchor.MIDDLE)
        label.setVerticalAnchor(TextLabel.VerticalAnchor.CENTER)
        label.moveTo(size.x / 2, size.y / 2)
        val g = SvgGElement()
        g.children().add(label.rootGroup)
        return g
    }

    override fun minimumKeySize(p: DataPointAesthetics): DoubleVector {
        val strokeWidth = AesScaling.strokeWidth(p)
        return DoubleVector(4.0, strokeWidth + 4)
    }
}
