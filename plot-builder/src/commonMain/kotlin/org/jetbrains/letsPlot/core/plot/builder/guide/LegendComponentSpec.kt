/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.guide

import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme

class LegendComponentSpec(
    title: String,
    internal val breaks: List<LegendBreak>,
    theme: LegendTheme,
    override val layout: LegendComponentLayout,
    reverse: Boolean
) : LegendBoxSpec(title, theme, reverse) {

    override fun hasSameContent(other: LegendBoxSpec): Boolean {
        if (other !is LegendComponentSpec) return false

        if (title != other.title) return false
        if (reverse != other.reverse) return false
        if (breaks.size != other.breaks.size) return false

        for (i in breaks.indices) {
            val thisBreak = breaks[i]
            val otherBreak = other.breaks[i]
            if (!thisBreak.hasSameVisualProperties(otherBreak)) return false
        }

        return true
    }
}
