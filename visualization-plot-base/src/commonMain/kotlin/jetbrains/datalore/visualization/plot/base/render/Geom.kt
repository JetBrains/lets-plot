package jetbrains.datalore.visualization.plot.base.render

import jetbrains.datalore.visualization.plot.base.Aesthetics

interface Geom {
    val legendKeyElementFactory: LegendKeyElementFactory
    fun build(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext)
}
