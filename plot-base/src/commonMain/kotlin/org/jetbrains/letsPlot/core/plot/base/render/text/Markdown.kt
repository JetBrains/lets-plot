/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.markdown.Markdown
import org.jetbrains.letsPlot.commons.markdown.Xml
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Font
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

        private fun render(html: Xml.XmlNode, context: Context = Context()): List<Term> {
            return when (html) {
                is Xml.XmlNode.Text -> when {
                    context.bold && context.italic -> listOf(BoldItalicText(html.content))
                    context.bold -> listOf(BoldText(html.content))
                    context.italic -> listOf(ItalicText(html.content))
                    else -> listOf(Text(html.content))
                }

                is Xml.XmlNode.Element -> when (html.name) {
                    "strong" -> html.children.flatMap { render(it, context.copy(bold = true)) }
                    "em" -> html.children.flatMap { render(it, context.copy(italic = true)) }
                    else -> html.children.flatMap { render(it, context) }
                }
            }
        }

        fun parse(text: String): List<Term> {
            if (text.isEmpty()) {
                return listOf(Text(""))
            }

            var emphasized = false
            var bold = false

            val html = Markdown.mdToHtml(text)
            val nodes = Xml.parse("<p>$html</p>") ?: return listOf(Text(""))
            return render(nodes)
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