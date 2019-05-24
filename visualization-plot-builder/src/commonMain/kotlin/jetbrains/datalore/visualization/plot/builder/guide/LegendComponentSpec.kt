package jetbrains.datalore.visualization.plot.builder.guide

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.builder.theme.LegendTheme

class LegendComponentSpec(title: String,
                          val breaks: List<LegendBreak>,
                          theme: LegendTheme,
                          override val layout: LegendComponentLayout) : LegendBoxSpec(title, theme) {

    override val contentSize: DoubleVector
        get() = layout.size
}
