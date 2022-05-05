/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.jfx

import javafx.scene.Group
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
        peer.applyStyleProperties(
            FontStyleProperties(styles)
        )
    }

    private fun parseCSS(css: String): Map<String, Any> {
        val styles = mutableMapOf<String, Any>()

        fun extractProperty(block: String, property: String): String? {
            val regex = Regex("$property:(.+);")
            return regex.find(block)?.groupValues?.get(1)?.trim()
        }

        Regex(CSS_REGEX)
            .findAll(css)
            .map { it.groupValues[SELECTOR_NAME] to it.groupValues[DECLARATION] }
            .forEach { (selector, declaration) ->
                styles[selector] = mapOf(
                    FONT_FAMILY to extractProperty(declaration, FONT_FAMILY),
                    FONT_SIZE to extractProperty(declaration, FONT_SIZE),
                    FONT_COLOR to extractProperty(declaration, FONT_COLOR),
                    FONT_WEIGHT to extractProperty(declaration, FONT_WEIGHT),
                    FONT_STYLE to extractProperty(declaration, FONT_STYLE)
                )
            }
        return styles
    }

    companion object {
        // .selector text : {
        //      property: value;
        //      ....
        // }
        const val CSS_REGEX = """\.([\w\-]+)\s+text\s+\{([^\{\}]*)\}"""
        const val SELECTOR_NAME = 1
        const val DECLARATION = 2

        //properties
        const val FONT_FAMILY = "font-family"
        const val FONT_SIZE = "font-size"
        const val FONT_COLOR = "fill"
        const val FONT_WEIGHT = "font-weight"
        const val FONT_STYLE = "font-style"
    }

    private class FontStyleProperties(private val myTextStyles: Map<String, Any>) : StyleProperties {

        private fun getMap(key: String): Map<String, String> {
            val map = myTextStyles[key]
            @Suppress("UNCHECKED_CAST")
            return map as? Map<String, String> ?: emptyMap()
        }

        override fun getColor(className: String): String {
            return getMap(className)[FONT_COLOR] ?: Color.BLACK.toCssColor()
        }

        override fun getFontSize(className: String): Double {
            return getMap(className)[FONT_SIZE]?.removeSuffix("px")?.toDouble() ?: 15.0
        }

        override fun getFontFamily(className: String): String {
            return getMap(className)[FONT_FAMILY] ?: "Helvetica"
        }

        override fun getIsItalic(className: String): Boolean {
            return getMap(className)[FONT_STYLE] == "italic"
        }

        override fun getIsBold(className: String): Boolean {
            return getMap(className)[FONT_WEIGHT] == "bold"
        }
    }
}