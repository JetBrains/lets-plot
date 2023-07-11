/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.style

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.FontFace


class StyleSheet constructor(
    private val textStyles: Map<String, TextStyle>,
    private val defaultFamily: String
) {
    fun getClasses(): List<String> = textStyles.keys.toList()

    fun getTextStyle(className: String): TextStyle {
        return textStyles[className]
            ?: TextStyle(
                family = defaultFamily,
                face = UNDEFINED_FONT_FACE,
                size = DEFAULT_FONT_SIZE,
                color = UNDEFINED_FONT_COLOR
            )
    }

    fun toCSS(className: String, id: String?): String {
        val css = StringBuilder()
        css.append("""
            |${id?.let { "#$id " } ?: ""}.$className {
            |${getTextStyle(className).toCSS()}
            |}
            |""".trimMargin()
        )
        return css.toString()
    }

    fun toCSS(): String {
        val css = StringBuilder()
        getClasses().forEach { className ->
            css.append(toCSS(className, id = null))
        }
        return css.toString()
    }

    companion object {
        val UNDEFINED_FONT_FACE = FontFace.BOLD_ITALIC
        val UNDEFINED_FONT_COLOR = Color(150, 0, 255)
        private const val DEFAULT_FONT_SIZE = 15.0

        fun FontFace.toCSS(): String {
            return "font-weight: ${if (bold) "bold" else "normal"};" +
                    "\n   font-style: ${if (italic) "italic" else "normal"};"
        }

        private fun TextStyle.toCSS(): String {
            return """
                |   fill: ${color.toHexColor()};
                |   font-family: ${family};
                |   font-size: ${size}px;
                |   ${face.toCSS()}   
                """.trimMargin()
        }

        // .className text : {
        //      property: value;
        //      ....
        // }
        @Suppress("RegExpRedundantEscape") // this inspection breaks the RegEx
        private const val CSS_REGEX = """\.([\w\-]+)\s+\{([^\{\}]*)\}"""

        fun fromCSS(css: String, defaultFamily: String, defaultSize: Double): StyleSheet {
            fun parseProperty(styleProperties: String, propertyName: String): String? {
                val regex = Regex("$propertyName:(.+);")
                return regex.find(styleProperties)?.groupValues?.get(1)?.trim()
            }

            val classes = mutableMapOf<String, TextStyle>()
            Regex(CSS_REGEX)
                .findAll(css)
                .forEach { matched ->
                    val (className, styleProperties) = matched.destructured

                    val fontFamily = parseProperty(styleProperties, "font-family") ?: defaultFamily
                    val fontWeight = parseProperty(styleProperties, "font-weight")
                    val fontStyle = parseProperty(styleProperties, "font-style")
                    val fontSize = parseProperty(styleProperties, "font-size")?.removeSuffix("px")?.toDoubleOrNull()
                        ?: defaultSize

                    val color = parseProperty(styleProperties, "fill")

                    classes[className] = TextStyle(
                        family = fontFamily,
                        face = FontFace(bold = fontWeight == "bold", italic = fontStyle == "italic"),
                        size = fontSize,
                        color = color?.let(Color::parseHex) ?: Color.BLACK
                    )
                }

            return StyleSheet(classes, defaultFamily)
        }
    }
}