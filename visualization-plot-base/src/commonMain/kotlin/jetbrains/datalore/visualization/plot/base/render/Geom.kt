package jetbrains.datalore.visualization.plot.base.render

interface Geom {
    val legendKeyElementFactory: LegendKeyElementFactory
    fun build(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext)
}
