/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.markdown.Markdown
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.xml.Xml
import org.jetbrains.letsPlot.commons.xml.Xml.XmlNode
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextNode

internal class Markdown : Term {
    override val visualCharCount: Int
        get() = TODO("Not yet implemented")
    override val svg: List<SvgElement>
        get() = TODO("Not yet implemented")

    override fun estimateWidth(
        font: Font,
        widthCalculator: (String, Font) -> Double
    ): Double {
        TODO("Not yet implemented")
    }

    companion object {
        data class Context(
            val bold: Boolean = false,
            val italic: Boolean = false,
            val color: Color? = null
        )

        private fun renderXml(html: XmlNode, context: Context = Context()): List<Term> {
            return when (html) {
                is XmlNode.Text -> when {
                    context.bold && context.italic -> listOf(BoldItalicText(html.content))
                    context.bold -> listOf(BoldText(html.content))
                    context.italic -> listOf(ItalicText(html.content))
                    else -> listOf(Text(html.content))
                }

                is XmlNode.Element -> when (html.name) {
                    "strong" -> html.children.flatMap { renderXml(it, context.copy(bold = true)) }
                    "em" -> html.children.flatMap { renderXml(it, context.copy(italic = true)) }
                    else -> html.children.flatMap { renderXml(it, context) }
                }
            }
        }

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

            return renderXml(doc)
        }

    }

    class BoldText(private val text: String) : Term {
        override val visualCharCount: Int
            get() = text.length
        override val svg: List<SvgElement>
            get() = listOf(SvgTSpanElement().apply {
                fontWeight().set("bold")
                children().add(
                    SvgTextNode(text)
                )
            })

        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return widthCalculator(text, font)
        }
    }

    class ItalicText(private val text: String) : Term {
        override val visualCharCount: Int
            get() = text.length
        override val svg: List<SvgElement>
            get() = listOf(SvgTSpanElement().apply {
                fontStyle().set("italic")
                children().add(
                    SvgTextNode(text)
                )
            })

        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return widthCalculator(text, font)
        }
    }

    class BoldItalicText(private val text: String) : Term {
        override val visualCharCount: Int
            get() = text.length
        override val svg: List<SvgElement>
            get() = listOf(SvgTSpanElement().apply {
                fontWeight().set("bold")
                fontStyle().set("italic")
                children().add(
                    SvgTextNode(text)
                )
            })

        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return widthCalculator(text, font)
        }
    }
}