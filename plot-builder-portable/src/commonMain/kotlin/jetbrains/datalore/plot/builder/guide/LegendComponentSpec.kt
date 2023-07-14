/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme

class LegendComponentSpec(
    title: String,
    internal val breaks: List<LegendBreak>,
    theme: LegendTheme,
    override val layout: LegendComponentLayout,
    reverse: Boolean
) : LegendBoxSpec(title, theme, reverse)
