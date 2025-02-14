/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.markdown.Markdown
import org.jetbrains.letsPlot.commons.markdown.Node
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextNode

internal class MarkdownEx : Term {
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
        fun parse(text: String): List<Term> {
            if (text.isEmpty()) {
                return listOf(Text(""))
            }

            val rootNode = Markdown.parse(text)

            if (rootNode is Node.Group) {
                return rootNode.children.map { node ->
                    when (node) {
                        is Node.Text -> Text(node.text)
                        is Node.Strong -> BoldText(node.text)
                        is Node.Emph -> ItalicText(node.text)
                        is Node.BoldItalic -> BoldItalicText(node.text)
                        else -> throw IllegalArgumentException("Unsupported node type: $node")
                    }
                }
            }

            return listOf(Text(text))
        }

    }

    class BoldText(text: String) : Term {
        private val text = text.trim()
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

    class ItalicText(text: String) : Term {
        private val text = text.trim()
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

    class BoldItalicText(text: String) : Term {
        private val text = text.trim()
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