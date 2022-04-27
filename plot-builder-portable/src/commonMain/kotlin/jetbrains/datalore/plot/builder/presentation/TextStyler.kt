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
import jetbrains.datalore.vis.StyleRenderer


class TextStyler : StyleRenderer {
    private val myTextStyles: MutableMap<String, FontProperties> = DEFAULT_STYLES.toMutableMap()

    override fun getColor(className: String): Color {
        return myTextStyles[className]?.color ?: DEFAULT_COLOR
    }

    override fun getFontSize(className: String): Double {
        return myTextStyles[className]?.size ?: DEFAULT_SIZE
    }

    override fun getFontFamily(className: String): String {
        return myTextStyles[className]?.family?.toString() ?: DEFAULT_FAMILY.toString()
    }

    override fun getIsItalic(className: String): Boolean {
        val face = myTextStyles[className]?.face ?: DEFAULT_FACE
        return face.italic
    }

    override fun getIsBold(className: String): Boolean {
        val face = myTextStyles[className]?.face ?: DEFAULT_FACE
        return face.bold
    }

    fun css(): String {
        val css = StringBuilder()
        myTextStyles.forEach { (className, props) ->
            css
                .append(".$className text").append(" {")
                .append("\n  fill: ").append(props.color.toHexColor() + ";")
                .append("\n  font-family: ").append(props.family.toString() + ";")
                .append("\n  font-size: ").append(props.size).append("px;")
                .append("\n  font-weight: ").append(if (props.face.bold) "bold;" else "normal;")
                .append("\n  font-style: ").append(if (props.face.italic) "italic;" else "normal;")
                .append("\n}\n")
        }
        return css.toString()
    }

    companion object {
        val DEFAULT_FAMILY = FontFamily.forName(Defaults.FONT_FAMILY_NORMAL)
        const val DEFAULT_SIZE = Defaults.FONT_MEDIUM.toDouble()
        val DEFAULT_FACE = FontFace.NORMAL
        val DEFAULT_COLOR = Color.BLACK

        private val DEFAULT_STYLES = mapOf(
            PLOT_TITLE to fontProperties(size = 16.0, face = FontFace.BOLD),
            PLOT_SUBTITLE to fontProperties(size = 15.0),
            PLOT_CAPTION to fontProperties(size = 13.0),

            LEGEND_TITLE to fontProperties(size = 15.0),
            LEGEND_ITEM to fontProperties(size = 13.0),

            TOOLTIP_TEXT to fontProperties(size = 13.0),
            TOOLTIP_TITLE to fontProperties(size = 13.0),
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
            family: FontFamily = DEFAULT_FAMILY,
            face: FontFace = DEFAULT_FACE,
            size: Double = DEFAULT_SIZE,
            color: Color = DEFAULT_COLOR
        ) = FontProperties(family, face, size, color)
    }
}