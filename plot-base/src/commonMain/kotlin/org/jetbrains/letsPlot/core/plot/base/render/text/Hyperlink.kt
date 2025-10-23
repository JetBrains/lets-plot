/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.intern.util.TextWidthEstimator.widthCalculator
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.xml.Xml
import org.jetbrains.letsPlot.commons.xml.Xml.XmlNode
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.RichTextNode
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.wrap
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgAElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement

internal object Hyperlink {
    fun parse(text: String): List<RichTextNode> {
        val xmlStr = "<p>$text</p>"
        val (node, nodeMap, unparsed) = Xml.parseSafe(xmlStr)

        val svg = render(node, nodeMap, xmlStr)

        return if (unparsed.isNotEmpty()) {
            svg + RichTextNode.Text(unparsed)
        } else {
            svg
        }
    }

    fun render(node: XmlNode, nodeMap: Map<XmlNode, IntRange>, input: String): List<RichTextNode> {
        val output = mutableListOf<RichTextNode>()

        when (node) {
            is XmlNode.Text -> output += RichTextNode.Text(node.content)
            is XmlNode.Element -> {
                if (node.name == "p") {
                    output += node.children.flatMap { render(it, nodeMap, input) }
                } else if (node.name == "a") {
                    val href = node.attributes["href"] ?: ""
                    val target = node.attributes["target"]
                    val text = node.children.joinToString("") { (it as? XmlNode.Text)?.content ?: "" }
                    output += HyperlinkElement(text, href, target)
                    return output
                } else {
                    val content = input.substring(nodeMap[node]!!)
                    output += RichTextNode.Text(content)
                }
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
