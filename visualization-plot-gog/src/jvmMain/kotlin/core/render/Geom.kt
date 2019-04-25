package jetbrains.datalore.visualization.plot.gog.core.render

interface Geom {
    val legendKeyElementFactory: LegendKeyElementFactory
    fun build(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext)
}
