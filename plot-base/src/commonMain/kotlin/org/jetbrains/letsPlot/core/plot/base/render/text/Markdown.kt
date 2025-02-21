/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.markdown.Markdown
import org.jetbrains.letsPlot.commons.markdown.Xml
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
        fun parse(text: String): List<Term> {
            if (text.isEmpty()) {
                return listOf(Text(""))
            }

            var emphasized = false
            var bold = false

            val html = Markdown.mdToHtml(text)
            val nodes = Xml.parse(html)

            //val terms = nodes?.mapNotNull { node ->
            //    when (node) {
            //        is Node.Strong -> null.also { bold = true }
            //        is Node.Em -> null.also { emphasized = true }
            //        is Node.CloseStrong -> null.also { bold = false }
            //        is Node.CloseEm -> null.also { emphasized = false }
            //        is Node.Text -> {
            //            when {
            //                bold && emphasized -> BoldItalicText(node.text)
            //                bold -> BoldText(node.text)
            //                emphasized -> ItalicText(node.text)
            //                else -> Text(node.text)
            //            }
            //        }
            //        else -> throw IllegalArgumentException("Unsupported node type: $node")
            //    }
            //}
            //return terms

            return listOf(Text(text))
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