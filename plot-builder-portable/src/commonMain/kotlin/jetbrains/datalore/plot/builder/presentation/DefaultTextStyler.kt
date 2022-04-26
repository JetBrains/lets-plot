/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.FontFamily
import jetbrains.datalore.plot.builder.defaultTheme.values.FontFace
import jetbrains.datalore.plot.builder.defaultTheme.values.FontProperties
import jetbrains.datalore.plot.builder.presentation.Style.AXIS_TEXT
import jetbrains.datalore.plot.builder.presentation.Style.AXIS_TITLE
import jetbrains.datalore.plot.builder.presentation.Style.AXIS_TOOLTIP
import jetbrains.datalore.plot.builder.presentation.Style.FACET_STRIP_TEXT
import jetbrains.datalore.plot.builder.presentation.Style.LEGEND_ITEM
import jetbrains.datalore.plot.builder.presentation.Style.LEGEND_TITLE
import jetbrains.datalore.plot.builder.presentation.Style.PLOT_CAPTION
import jetbrains.datalore.plot.builder.presentation.Style.PLOT_SUBTITLE
import jetbrains.datalore.plot.builder.presentation.Style.PLOT_TITLE
import jetbrains.datalore.plot.builder.presentation.Style.TOOLTIP_LABEL
import jetbrains.datalore.plot.builder.presentation.Style.TOOLTIP_TEXT
import jetbrains.datalore.plot.builder.presentation.Style.TOOLTIP_TITLE

class DefaultTextStyler : TextStyler(STYLES) {

    companion object {
        private val STYLES = mapOf(
            PLOT_TITLE to fontProperties(size = 16.0, face = FontFace.BOLD),
            PLOT_SUBTITLE to fontProperties(size = 15.0),
            PLOT_CAPTION to fontProperties(size = 13.0),

            LEGEND_TITLE to fontProperties(size = 15.0),
            LEGEND_ITEM to fontProperties(size = 13.0),

            TOOLTIP_TEXT to fontProperties(13.0),
            TOOLTIP_TITLE to fontProperties(13.0),
            TOOLTIP_LABEL to fontProperties(size = 13.0, face = FontFace.BOLD)

        ) + listOf("-x", "-y")
            .flatMap { suffix ->
                listOf(
                    AXIS_TITLE + suffix to fontProperties(size = 15.0),
                    AXIS_TEXT + suffix to fontProperties(size = 13.0),
                    AXIS_TOOLTIP + suffix to fontProperties(size = 13.0, color = Color.WHITE),

                    FACET_STRIP_TEXT + suffix to fontProperties(size = 15.0)
                )
            }

        private fun fontProperties(
            size: Double,
            face: FontFace = FontFace.NORMAL,
            color: Color = Color.BLACK
        ) = FontProperties(
            family = FontFamily.forName(Defaults.FONT_FAMILY_NORMAL),
            face,
            size,
            color
        )
    }
}