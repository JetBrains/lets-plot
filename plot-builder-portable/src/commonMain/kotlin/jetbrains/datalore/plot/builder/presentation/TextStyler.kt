/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.FontFamily
import jetbrains.datalore.plot.builder.defaultTheme.values.FontFace
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.*
import jetbrains.datalore.plot.builder.presentation.Defaults.Plot
import jetbrains.datalore.plot.builder.presentation.Style.AXIS_TEXT
import jetbrains.datalore.plot.builder.presentation.Style.AXIS_TITLE
import jetbrains.datalore.plot.builder.presentation.Style.AXIS_TOOLTIP_TEXT
import jetbrains.datalore.plot.builder.presentation.Style.FACET_STRIP_TEXT
import jetbrains.datalore.plot.builder.presentation.Style.LEGEND_ITEM
import jetbrains.datalore.plot.builder.presentation.Style.LEGEND_TITLE
import jetbrains.datalore.plot.builder.presentation.Style.PLOT_CAPTION
import jetbrains.datalore.plot.builder.presentation.Style.PLOT_SUBTITLE
import jetbrains.datalore.plot.builder.presentation.Style.PLOT_TITLE
import jetbrains.datalore.plot.builder.presentation.Style.TOOLTIP_LABEL
import jetbrains.datalore.plot.builder.presentation.Style.TOOLTIP_TEXT
import jetbrains.datalore.plot.builder.presentation.Style.TOOLTIP_TITLE


open class TextStyler {

    protected val myTextStyles: MutableMap<String, FontProperties> = mutableMapOf(
        PLOT_TITLE to FontProperties.create(size = Title.FONT_SIZE.toDouble(), face = FontFace.BOLD),
        PLOT_SUBTITLE to FontProperties.create(size = Subtitle.FONT_SIZE.toDouble()),
        PLOT_CAPTION to FontProperties.create(size = Caption.FONT_SIZE.toDouble()),
        LEGEND_TITLE to FontProperties.create(size = Legend.TITLE_FONT_SIZE.toDouble()),
        LEGEND_ITEM to FontProperties.create(size = Legend.ITEM_FONT_SIZE.toDouble()),
        TOOLTIP_TEXT to FontProperties.create(size = Tooltip.DATA_TOOLTIP_FONT_SIZE.toDouble()),
        TOOLTIP_TITLE to FontProperties.create(size = Tooltip.DATA_TOOLTIP_FONT_SIZE.toDouble(), face = FontFace.BOLD),
        TOOLTIP_LABEL to FontProperties.create(size = Tooltip.DATA_TOOLTIP_FONT_SIZE.toDouble(), face = FontFace.BOLD),
        "$AXIS_TITLE-x" to FontProperties.create(size = Plot.Axis.TITLE_FONT_SIZE.toDouble()),
        "$AXIS_TITLE-y" to FontProperties.create(size = Plot.Axis.TITLE_FONT_SIZE.toDouble()),
        "$AXIS_TEXT-x" to FontProperties.create(size = Plot.Axis.TICK_FONT_SIZE.toDouble()),
        "$AXIS_TEXT-y" to FontProperties.create(size = Plot.Axis.TICK_FONT_SIZE.toDouble()),
        "$AXIS_TOOLTIP_TEXT-x" to FontProperties.create(
            size = Tooltip.AXIS_TOOLTIP_FONT_SIZE.toDouble(),
            color = Color.WHITE
        ),
        "$AXIS_TOOLTIP_TEXT-y" to FontProperties.create(
            size = Tooltip.AXIS_TOOLTIP_FONT_SIZE.toDouble(),
            color = Color.WHITE
        ),
        "$FACET_STRIP_TEXT-x" to FontProperties.create(size = Defaults.FONT_MEDIUM.toDouble()),
        "$FACET_STRIP_TEXT-y" to FontProperties.create(size = Defaults.FONT_MEDIUM.toDouble()))

    fun css(): String {
        val css = StringBuilder()
        myTextStyles.forEach { (className, props) ->
            css.append("""
                |.$className text {
                |   fill: ${props.color.toCssColor()};
                |   font-family: ${props.family};
                |   font-size: ${props.size}px;
                |   font-weight: ${if (props.face.bold) "bold" else "normal"};
                |   font-style: ${if (props.face.italic) "italic" else "normal"};
                |}
                |""".trimMargin()
            )
        }
        return css.toString()
    }

    companion object {
        val DEFAULT_FAMILY = FontFamily.forName(Defaults.FONT_FAMILY_NORMAL)
        const val DEFAULT_SIZE = Defaults.FONT_MEDIUM.toDouble()
        val DEFAULT_FACE = FontFace.NORMAL
        val DEFAULT_COLOR = Color.BLACK

        data class FontProperties(
            val family: FontFamily,
            val face: FontFace,
            val size: Double,
            val color: Color,
        ) {
            companion object {
                fun create(
                    family: FontFamily? = null,
                    face: FontFace? = null,
                    size: Double? = null,
                    color: Color? = null
                ) = FontProperties(
                    family ?: DEFAULT_FAMILY,
                    face ?: DEFAULT_FACE,
                    size ?: DEFAULT_SIZE,
                    color ?: DEFAULT_COLOR
                )
            }
        }
    }
}