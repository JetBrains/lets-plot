package jetbrains.datalore.visualization.plot.gog.core.render.point.symbol

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimShape

class DiamondGlyph(location: DoubleVector, width: Double) : SingletonGlyph(location, width) {

    override fun createShape(location: DoubleVector, size: Double): SvgSlimShape {
        val half = size / 2
        val x = doubleArrayOf(half, size, half, 0.0)
        val y = doubleArrayOf(0.0, half, size, half)
        val ox = location.x - half
        val oy = location.y - half
        for (i in 0..3) {
            x[i] = ox + x[i]
            y[i] = oy + y[i]
        }

        val pathData = GlyphUtil.buildPathData(x.asList(), y.asList())
        return SvgSlimElements.path(pathData)
    }
}
