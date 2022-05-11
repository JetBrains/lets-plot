/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.jfx

import javafx.scene.Group
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.svg.SvgStyleElement

internal class SvgStyleElementMapper(
    source: SvgStyleElement,
    target: Group,
    peer: SvgJfxPeer,
) : SvgElementMapper<SvgStyleElement, Group>(source, target, peer) {

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        // Parse CSS to prepare StyleProperties
        val styles = parseCSS(source.resource.css())
        peer.applyStyleProperties(styles)
    }

    private fun parseCSS(css: String): (String) -> StyleProperty {
        val styles = mutableMapOf<String, StyleProperty>()

        fun extractProperty(block: String, property: String): String? {
            val regex = Regex("$property:(.+);")
            return regex.find(block)?.groupValues?.get(1)?.trim()
        }

        Regex(CSS_REGEX)
            .findAll(css)
            .forEach { matched ->
                val (className, properties) = matched.destructured

                val font = getFont(
                    fontFamily = null,// todo extractProperty(properties, FONT_FAMILY)
                    fontWeight = extractProperty(properties, FONT_WEIGHT),
                    fontStyle = extractProperty(properties, FONT_STYLE),
                    fontSize = extractProperty(properties, FONT_SIZE)
                )
                val color = extractProperty(properties, FONT_COLOR) ?: DEFAULT_COLOR

                styles[className] = StyleProperty(font, color)
            }

        return { className: String -> styles[className] ?: StyleProperty(DEFAULT_FONT, DEFAULT_COLOR) }
    }

    companion object {
        // .className text : {
        //      property: value;
        //      ....
        // }
        const val CSS_REGEX = """\.([\w\-]+)\s+text\s+\{([^\{\}]*)\}"""

        // properties
        const val FONT_FAMILY = "font-family"
        const val FONT_SIZE = "font-size"
        const val FONT_COLOR = "fill"
        const val FONT_WEIGHT = "font-weight"
        const val FONT_STYLE = "font-style"

        // defaults
        val DEFAULT_FONT = getFont(fontFamily = null, fontWeight = null, fontStyle = null, fontSize = null)
        val DEFAULT_COLOR = Color.BLACK.toCssColor()
        private const val DEFAULT_FONT_SIZE = 15.0
        private const val DEFAULT_FONT_FAMILY = "Helvetica"

        private fun getFont(
            fontFamily: String?,
            fontWeight: String?,
            fontStyle: String?,
            fontSize: String?
        ): Font {
            return Font.font(
                fontFamily ?: DEFAULT_FONT_FAMILY,
                if (fontWeight == "bold") FontWeight.BOLD else null,
                if (fontStyle == "italic") FontPosture.ITALIC else null,
                fontSize?.removeSuffix("px")?.toDoubleOrNull() ?: DEFAULT_FONT_SIZE
            )
        }
    }
}