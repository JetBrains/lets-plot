/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.intern.util.TextWidthEstimator.widthCalculator
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.xml.Xml.XmlNode
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.RichTextNode
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.parseAsXml
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.wrap
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgAElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement

internal object Hyperlink {
    fun parse(text: String): List<RichTextNode> {
        val doc = parseAsXml(text)
        return render(doc)
    }

    fun render(node: XmlNode): List<RichTextNode> {
        val output = mutableListOf<RichTextNode>()

        when (node) {
            is XmlNode.Text -> output += RichTextNode.Text(node.content)
            is XmlNode.Element -> {
                if (node.name == "a") {
                    val href = node.attributes["href"] ?: ""
                    val target = node.attributes["target"]
                    val text = node.children.joinToString("") { (it as? XmlNode.Text)?.content ?: "" }
                    output += HyperlinkElement(text, href, target)
                    return output
                }

                output += node.children.flatMap(::render)
            }
        }

        return output
    }

    class HyperlinkElement(
        private val text: String,
        private val href: String,
        target: String?
    ) : RichTextNode.RichSpan() {
        private val target = target ?: "_blank"

        override val visualCharCount: Int = text.length
        override fun estimateWidth(font: Font): Double {
            return widthCalculator(text, font)
        }

        override fun render(context: RenderState, prefixWidth: Double): List<WrappedSvgElement<SvgElement>> {
            return listOf(
                SvgAElement().apply {
                    href().set(href)
                    xlinkHref().set(href)
                    target().set(target)

                    children().add(
                        SvgTSpanElement(text).apply {
                            addClass(RichText.HYPERLINK_ELEMENT_CLASS)
                        }
                    )
                }.wrap()
            )
        }
    }
}
