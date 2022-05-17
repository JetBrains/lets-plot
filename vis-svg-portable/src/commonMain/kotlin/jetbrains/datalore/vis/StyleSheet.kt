/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.FontFace
import jetbrains.datalore.base.values.FontFamily


class StyleSheet(
    private val textStyles: Map<String, TextStyle>,
    private val defaultFamily: String,
    private val defaultSize: Double
) {
    fun getClasses(): List<String> = textStyles.keys.toList()

    fun getTextStyle(className: String): TextStyle {
        return textStyles[className]
            ?: TextStyle(
                family = FontFamily.forName(defaultFamily),
                face = FontFace.NORMAL,
                size = defaultSize,
                color = Color.BLACK
            )
    }

    fun toCSS(className: String, id: String?): String {
        val css = StringBuilder()
        css.append("""
            |${id?.let { "#$id " } ?: ""}.$className text {
            |${getTextStyle(className).toCSS()}
            |}
            |""".trimMargin()
        )
        return css.toString()
    }

    companion object {

        private fun TextStyle.toCSS(): String {
            return """
                |   fill: ${color.toHexColor()};
                |   font-family: ${family};
                |   font-size: ${size}px;
                |   font-weight: ${if (face.bold) "bold" else "normal"};
                |   font-style: ${if (face.italic) "italic" else "normal"};
                """.trimMargin()
        }

        // .className text : {
        //      property: value;
        //      ....
        // }
        private const val CSS_REGEX = """\.([\w\-]+)\s+text\s+\{([^\{\}]*)\}"""

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

                    val fontFamily = defaultFamily // todo: parseProperty(styleProperties, "font-family")
                    val fontWeight = parseProperty(styleProperties, "font-weight")
                    val fontStyle = parseProperty(styleProperties, "font-style")
                    val fontSize = parseProperty(styleProperties, "font-size")?.removeSuffix("px")?.toDoubleOrNull()
                        ?: defaultSize
                    val color = parseProperty(styleProperties, "fill")

                    classes[className] = TextStyle(
                        family = FontFamily.forName(fontFamily),
                        face = FontFace(bold = fontWeight == "bold", italic = fontStyle == "italic"),
                        size = fontSize,
                        color = color?.let(Color::parseHex) ?: Color.BLACK
                    )
                }

            return StyleSheet(classes, defaultFamily, defaultSize)
        }
    }
}