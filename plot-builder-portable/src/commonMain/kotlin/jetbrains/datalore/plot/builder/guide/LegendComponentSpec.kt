/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import jetbrains.datalore.plot.builder.theme.LegendTheme

class LegendComponentSpec(title: String,
                          internal val breaks: List<LegendBreak>,
                          theme: LegendTheme,
                          override val layout: LegendComponentLayout
) : LegendBoxSpec(title, theme)
