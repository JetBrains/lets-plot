package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.xml.Xml
import org.jetbrains.letsPlot.commons.xml.Xml.XmlNode
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.RichTextNode

internal object Plaintext {
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
                } else if (Hyperlink.canRender(node)) {
                    output += Hyperlink.render(node)
                } else {
                    val nodeRange = nodeMap[node] ?: error("Node $node not found")

                    if (node.children.isEmpty()) {
                        val nodeText = input.substring(nodeRange)
                        output += RichTextNode.Text(nodeText)
                        return output
                    } else {
                        val fistChild = node.children.first()
                        val lastChild = node.children.last()

                        val firstChildRange = nodeMap[fistChild] ?: error("Node $fistChild not found")
                        val lastChildRange = nodeMap[lastChild] ?: error("Node $lastChild not found")

                        val head = input.substring(nodeRange.first, firstChildRange.first)
                        output += RichTextNode.Text(head)

                        for (child in node.children) {
                            output += render(child, nodeMap, input)
                        }

                        val tail = input.substring(lastChildRange.last + 1, nodeRange.last + 1)
                        output += RichTextNode.Text(tail)
                    }
                }
            }
        }

        return output
    }
}
