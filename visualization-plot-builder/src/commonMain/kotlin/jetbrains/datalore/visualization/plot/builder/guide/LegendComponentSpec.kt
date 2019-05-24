package jetbrains.datalore.visualization.plot.builder.guide

import jetbrains.datalore.visualization.plot.builder.theme.LegendTheme

class LegendComponentSpec(title: String,
                          internal val breaks: List<LegendBreak>,
                          theme: LegendTheme,
                          override val layout: LegendComponentLayout) : LegendBoxSpec(title, theme)
