package jetbrains.datalore.visualization.plot.base

import jetbrains.datalore.visualization.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.visualization.plot.base.render.SvgRoot

interface Geom {
    val legendKeyElementFactory: LegendKeyElementFactory
    fun build(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext)
}
