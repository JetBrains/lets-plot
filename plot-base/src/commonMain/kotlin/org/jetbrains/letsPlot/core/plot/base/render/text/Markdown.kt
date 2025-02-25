/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.markdown.Markdown
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.xml.Xml
import org.jetbrains.letsPlot.commons.xml.Xml.XmlNode
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextNode

internal object Markdown {
    fun render(text: String): List<Term> {
        if (text.isEmpty()) {
            return listOf(Text(""))
        }

        val html = Markdown.mdToHtml(text)
        val doc = Xml.parseSafe("<p>$html</p>") // wrap in <p> to make it a valid XML with a single root element
            .let { (root, unparsed) ->
                if (unparsed.isEmpty()) return@let root

                when (root) {
                    is XmlNode.Element -> root.copy(children = root.children + XmlNode.Text(unparsed))
                    is XmlNode.Text -> root.copy(content = root.content + unparsed)
                }
            }

        return renderHtml(doc)
    }

    private fun renderHtml(node: XmlNode, context: Context = Context()): List<Term> {
        return when (node) {
            is XmlNode.Text -> listOf(Text(node.content, context.color, context.bold, context.italic))
            is XmlNode.Element -> {
                var ctx = when (node.name) {
                    "strong" -> context.copy(bold = true)
                    "em" -> context.copy(italic = true)
                    else -> context
                }

                val style = node.attributes["style"]
                    ?.let {
                        it
                            .split(";").map(String::trim)
                            .associate { it.substringBefore(":").trim() to it.substringAfter(":").trim() }
                    } ?: emptyMap()

                style["color"]?.let {
                    ctx = ctx.copy(color = Colors.parseColor(it))
                }
                node.children.flatMap { renderHtml(it, ctx) }
            }
        }
    }

    private data class Context(
        val bold: Boolean = false,
        val italic: Boolean = false,
        val color: Color? = null
    )

    private class Text(
        private val text: String,
        private val color: Color? = null,
        private val bold: Boolean = false,
        private val italic: Boolean = false
    ) : Term {
        override val visualCharCount: Int
            get() = text.length
        override val svg: List<SvgElement>
            get() = listOf(SvgTSpanElement().apply {
                if (color != null) {
                    fillColor().set(color)
                }

                if (bold) {
                    fontWeight().set("bold")
                }

                if (italic) {
                    fontStyle().set("italic")
                }

                children().add(
                    SvgTextNode(text)
                )
            })

        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return widthCalculator(text, font)
        }
    }
}
