/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.RichTextNode
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.RichTextNode.RichSvgElement
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.enrich
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgAElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent

internal object Hyperlink {
    fun parse(text: String): List<RichTextNode> {
        val links = anchorTagRegex.findAll(text)
            .map { match ->
                val (href, label) = match.destructured
                HyperlinkElement(label, href) to match.range
            }.toList()

        return RichText.fillTextTermGaps(text, links)
    }

    private val anchorTagRegex = "<a\\s+[^>]*href=\"(?<href>[^\"]*)\"[^>]*>(?<text>[^<]*)</a>".toRegex()

    class HyperlinkElement(
        private val text: String,
        private val href: String,
    ) : RichTextNode.Span() {
        override val visualCharCount: Int = text.length
        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return widthCalculator(text, font)
        }

        override fun toRichSvgElements(context: RenderState, previousNodes: List<RichTextNode.Span>): List<RichSvgElement> {
            return listOf(
                SvgAElement().apply {
                    href().set(href)
                    xlinkHref().set(href)
                    children().add(
                        SvgTSpanElement(text).apply {
                            addClass(RichText.HYPERLINK_ELEMENT_CLASS)
                        }
                    )
                }.enrich()
            )
        }

        override fun setXAttributeToSvgElement(aElement: SvgElement, x: Double?): SvgElement {
            val tSpan = aElement.children().first() as SvgTSpanElement
            x?.let { tSpan.setAttribute(SvgTextContent.X, x.toString()) }
            return aElement
        }
    }
}
