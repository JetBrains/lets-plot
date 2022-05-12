/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.FontFace
import jetbrains.datalore.base.values.FontFamily


open class StyleProperties(
    protected val textStyles: MutableMap<String, TextStyle>,
    private val defaultFamily: String,
    private val defaultSize: Double
) {
    fun getClasses(): List<String> = textStyles.keys.toList()

    fun getProperties(className: String): TextStyle {
        return textStyles[className]
            ?: TextStyle(
                family = FontFamily.forName(defaultFamily),
                face = FontFace.NORMAL,
                size = defaultSize,
                color = Color.BLACK
            )
    }

    companion object {

        private const val CSS_REGEX = """\.([\w\-]+)\s+text\s+\{([^\{\}]*)\}"""

        fun parseFromCSS(css: String, defaultFamily: String, defaultSize: Double): StyleProperties {
            fun parseProperty(styleProperties: String, propertyName: String): String? {
                val regex = Regex("$propertyName:(.+);")
                return regex.find(styleProperties)?.groupValues?.get(1)?.trim()
            }

            val classes = mutableMapOf<String, TextStyle>()
            Regex(CSS_REGEX)
                .findAll(css)
                .forEach { matched ->
                    val (className, styleProperties) = matched.destructured

                    val fontFamily = defaultFamily // todo parseProperty(styleProperties, "font-family") ?: defaultFamily
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

            return StyleProperties(classes, defaultFamily, defaultSize)
        }
    }
}