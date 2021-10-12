/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme2.values

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.guide.LegendDirection
import jetbrains.datalore.plot.builder.guide.LegendJustification
import jetbrains.datalore.plot.builder.guide.LegendPosition
import jetbrains.datalore.plot.builder.presentation.Defaults
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS_TOOLTIP
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.Elem
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.FACET_STRIP
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.LEGEND_DIRECTION
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.LEGEND_JUSTIFICATION
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.LEGEND_POSITION
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.LINE
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.PANEL_GRID_MINOR
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.RECT
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.TEXT
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.TITLE

object ThemeValuesBase {
    internal val PLOT_BACKGROUND = Color.WHITE

    val values: Map<String, Any> = mapOf(
        LINE to mapOf(
            Elem.COLOR to Color.BLUE,
            Elem.SIZE to 1.0,
        ),
        RECT to mapOf(
            Elem.COLOR to Color.BLUE,
            Elem.FILL to Color.LIGHT_BLUE,
            Elem.SIZE to 1.0,
        ),
        TEXT to mapOf(
            Elem.COLOR to Color.BLUE,
            Elem.SIZE to Defaults.FONT_SMALL,
            Elem.FONT_FACE to FontFace.NORMAL,
        ),
        TITLE to mapOf(
            Elem.SIZE to Defaults.FONT_MEDIUM,
            Elem.FONT_FACE to FontFace.BOLD,
        ),

        AXIS_TOOLTIP to mapOf(
            Elem.COLOR to Color.WHITE,
            Elem.FILL to Color.BLACK,
            Elem.SIZE to 1.0,
        ),

        PANEL_GRID_MINOR to mapOf(
            Elem.SIZE to 0.7,
        ),

        FACET_STRIP to RECT + mapOf(
            Elem.COLOR to Color.BLUE,
            Elem.FILL to PLOT_BACKGROUND,
            Elem.SIZE to 1.0,
        ),

        // Legend
        LEGEND_POSITION to LegendPosition.RIGHT,
        LEGEND_JUSTIFICATION to LegendJustification.CENTER,
        LEGEND_DIRECTION to LegendDirection.AUTO,
    )
}