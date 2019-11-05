/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import jetbrains.datalore.plot.builder.theme.LegendTheme

class LegendComponentSpec(title: String,
                          internal val breaks: List<jetbrains.datalore.plot.builder.guide.LegendBreak>,
                          theme: LegendTheme,
                          override val layout: jetbrains.datalore.plot.builder.guide.LegendComponentLayout
) : jetbrains.datalore.plot.builder.guide.LegendBoxSpec(title, theme)
