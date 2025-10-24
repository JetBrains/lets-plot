/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.markdown.Markdown
import org.jetbrains.letsPlot.commons.values.Colors.parseColor
import org.jetbrains.letsPlot.commons.xml.Xml
import org.jetbrains.letsPlot.commons.xml.Xml.XmlNode
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.RichTextNode

internal object Markdown {
    fun parse(text: String): List<RichTextNode> {
        if (text.isEmpty()) {
            return listOf(RichTextNode.Text(""))
        }

        val res = Xml.parse("<p>${Markdown.mdToHtml(text)}</p>")

        if (res.errorPos != null) {
            // Parsing error - return plain text
            return listOf(RichTextNode.Text(text))
        }

        return renderRichText(res.root)
    }

    private fun renderRichText(node: XmlNode): List<RichTextNode> {
        val output = mutableListOf<RichTextNode>()

        when (node) {
            is XmlNode.Text -> output += RichTextNode.Text(node.content)
            is XmlNode.Element -> {
                if (Hyperlink.canRender(node)) {
                    output += Hyperlink.render(node)
                    return output
                }

                if (node.name == "br") {
                    output += RichTextNode.LineBreak
                    return output
                }

                if (node.name == "softbreak") {
                    output += RichTextNode.Text(" ")
                    return output
                }

                val (prefix, suffix) = when {
                    node.name == "em" -> RichTextNode.EmphasisStart to RichTextNode.EmphasisEnd
                    node.name == "strong" -> RichTextNode.StrongStart to RichTextNode.StrongEnd
                    node.cssStyle["color"] != null -> RichTextNode.ColorStart(parseColor(node.cssStyle["color"]!!)) to RichTextNode.ColorEnd
                    else -> null
                }?.let { (prefix, suffix) -> listOf(prefix) to listOf(suffix) }
                    ?: Pair(emptyList(), emptyList())

                output += prefix
                output += node.children.flatMap{ renderRichText(it) }
                output += suffix
            }
        }

        return output
    }

    private val XmlNode.Element.cssStyle: Map<String, String>  get() {
        val style = attributes["style"] ?: return emptyMap()
        return style
            .split(";").map(String::trim)
            .associate { it.substringBefore(":").trim() to it.substringAfter(":").trim() }
    }
}
