/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.commons.values.Color

data class TooltipMarker(val colors: List<Color>) {
    val fillColor: Color?
        get() = colors.firstOrNull()

    val strokeColor: Color?
        get() = colors.getOrNull(1)

    val colorCount: Int
        get() = colors.size

    fun allTransparent(): Boolean = colors.all { it.alpha == 0 }

    fun distinct(): TooltipMarker = of(colors.distinct())

    companion object {
        val NONE = TooltipMarker(emptyList())

        fun of(colors: List<Color>): TooltipMarker {
            return if (colors.isEmpty()) NONE else TooltipMarker(colors)
        }

        fun of(vararg colors: Color): TooltipMarker = of(colors.toList())
    }
}
