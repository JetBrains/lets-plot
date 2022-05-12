/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.jfx

import javafx.scene.Group
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.FontFace
import jetbrains.datalore.base.values.FontFamily
import jetbrains.datalore.vis.TextStyle
import jetbrains.datalore.vis.svg.SvgStyleElement

internal class SvgStyleElementMapper(
    source: SvgStyleElement,
    target: Group,
    peer: SvgJfxPeer,
) : SvgElementMapper<SvgStyleElement, Group>(source, target, peer) {

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        // Parse CSS to prepare StyleProvider
        val styleProvider = parseCSS(source.resource.css())
        peer.applyStyleProvider(styleProvider)
    }

    private fun parseCSS(css: String): (String) -> TextStyle {
        fun parseProperty(styleProperties: String, propertyName: String): String? {
            val regex = Regex("$propertyName:(.+);")
            return regex.find(styleProperties)?.groupValues?.get(1)?.trim()
        }

        val classes = mutableMapOf<String, TextStyle>()

        Regex(CSS_REGEX)
            .findAll(css)
            .forEach { matched ->
                val (className, styleProperties) = matched.destructured

                classes[className] = createTextStyle(
                    fontFamily = null, // todo extractProperty(properties, "font-family")
                    fontWeight = parseProperty(styleProperties, "font-weight"),
                    fontStyle = parseProperty(styleProperties, "font-style"),
                    fontSize = parseProperty(styleProperties, "font-size"),
                    color = parseProperty(styleProperties, "fill")
                )
            }

        return { className: String -> classes[className] ?: DEFAULT_TEXT_PROPERTIES }
    }

    companion object {
        // .className text : {
        //      property: value;
        //      ....
        // }
        private const val CSS_REGEX = """\.([\w\-]+)\s+text\s+\{([^\{\}]*)\}"""

        // defaults
        private val DEFAULT_COLOR = Color.BLACK
        private const val DEFAULT_FONT_SIZE = 15.0
        private const val DEFAULT_FONT_FAMILY = "Helvetica"
        private val DEFAULT_TEXT_PROPERTIES = createTextStyle(
            fontFamily = null,
            fontWeight = null,
            fontStyle = null,
            fontSize = null,
            color = null
        )

        private fun createTextStyle(
            fontFamily: String?,
            fontWeight: String?,
            fontStyle: String?,
            fontSize: String?,
            color: String?
        ) = TextStyle(
            family = FontFamily.forName(fontFamily ?: DEFAULT_FONT_FAMILY),
            face = FontFace(bold = fontWeight == "bold", italic = fontStyle == "italic"),
            size = fontSize?.removeSuffix("px")?.toDoubleOrNull() ?: DEFAULT_FONT_SIZE,
            color = color?.let(Color::parseHex) ?: DEFAULT_COLOR
        )
    }
}