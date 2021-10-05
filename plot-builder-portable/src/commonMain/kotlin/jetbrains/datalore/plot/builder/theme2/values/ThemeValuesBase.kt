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
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.ELEMENT_BLANK
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.Elem
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.FACET_STRIP
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.LEGEND_DIRECTION
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.LEGEND_JUSTIFICATION
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.LEGEND_POSITION
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.LINE
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.PANEL_GRID
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.PANEL_GRID_MAJOR
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.PANEL_GRID_MINOR
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.PANEL_RECT
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.RECT
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.TEXT
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.TITLE

object ThemeValuesBase {
    private val TEXT_COMMON = mapOf(
        Elem.SIZE to Defaults.FONT_SMALL,
        Elem.COLOR to Defaults.TEXT_COLOR,
        Elem.FONT_FACE to FontFace.NORMAL,
    )

    private val LINE_COMMON = mapOf(
        Elem.SIZE to 1.0,
        Elem.COLOR to Color.parseHex(Defaults.DARK_GRAY),
    )

    private val AXIS_TOOLTIP_COMMON = mapOf(
        Elem.COLOR to Color.WHITE,
        Elem.FILL to Color.BLACK,
        Elem.SIZE to Defaults.FONT_SMALL,
    )

    private val GRID_LINE_COMMON = mapOf(
        Elem.SIZE to 1.0,
        Elem.COLOR to Color.parseHex(Defaults.X_LIGHT_GRAY),
    )

    val values: Map<String, Any> = mapOf(
        // Basic defaults
        LINE to LINE_COMMON,
        RECT to mapOf(
            Elem.COLOR to Color.WHITE,
            Elem.FILL to Color.BLACK,
            Elem.SIZE to 1.0,
        ),
        TEXT to TEXT_COMMON,
        TITLE to TEXT_COMMON + mapOf(
            Elem.SIZE to Defaults.FONT_MEDIUM,
            Elem.FONT_FACE to FontFace.BOLD,
        ),

        // Axis
        AXIS_TOOLTIP to AXIS_TOOLTIP_COMMON,

        // Panel
//        PANEL_BACKGROUND to Color.WHITE,
//        PANEL_PANEL_BORDER to ELEMENT_BLANK,
        PANEL_RECT to ELEMENT_BLANK,

        // Panel grid
        PANEL_GRID to GRID_LINE_COMMON,
        PANEL_GRID_MAJOR to GRID_LINE_COMMON,
        PANEL_GRID_MINOR to GRID_LINE_COMMON,

        // Facet
        FACET_STRIP to RECT + mapOf(
            Elem.FILL to Color.GRAY,
            Elem.SIZE to 0.0,
        ),

        // Legend
        LEGEND_POSITION to LegendPosition.RIGHT,
        LEGEND_JUSTIFICATION to LegendJustification.CENTER,
        LEGEND_DIRECTION to LegendDirection.AUTO,
    )
}