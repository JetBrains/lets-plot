package jetbrains.datalore.visualization.plot.gog.core.render.point.symbol

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements

internal class CrossGlyph @JvmOverloads constructor(location: DoubleVector, size: Double, inscribedInCircle: Boolean = true) : TwoShapeGlyph() {

    init {
        val cx = location.x
        val cy = location.y
        val w = if (inscribedInCircle)
            size * CIRCLE_WIDTH_ADJUST_RATIO
        else
            size
        val half = w / 2 // half width of inner square

        val backSlashLine = SvgSlimElements.line(
                cx - half,
                cy - half,
                cx + half,
                cy + half)
        val slashLine = SvgSlimElements.line(
                cx - half,
                cy + half,
                cx + half,
                cy - half)

        setShapes(backSlashLine, slashLine)
    }

    companion object {
        val CIRCLE_WIDTH_ADJUST_RATIO = Math.cos(Math.PI / 4)   // cos(45)
    }
}
