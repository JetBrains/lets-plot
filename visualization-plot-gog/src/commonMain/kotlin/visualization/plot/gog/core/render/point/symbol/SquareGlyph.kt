package jetbrains.datalore.visualization.plot.gog.core.render.point.symbol

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimShape

internal class SquareGlyph(location: DoubleVector, size: Double) : SingletonGlyph(location, size) {

    override fun createShape(location: DoubleVector, width: Double): SvgSlimShape {
        return SvgSlimElements.rect(
                location.x - width / 2,
                location.y - width / 2,
                width,
                width)
    }
}
