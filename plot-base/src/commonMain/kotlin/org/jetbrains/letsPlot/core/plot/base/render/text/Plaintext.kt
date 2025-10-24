package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.xml.Xml
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.RichTextNode

internal object Plaintext {
    fun parse(text: String): List<RichTextNode> {
        val output = mutableListOf<RichTextNode>()

        var i = 0
        var plainText = ""
        while (i < text.length) {
            if (text[i] != '<') {
                plainText += text[i]
                i++
            } else {
                val tag = text.substring(i)
                val res = Xml.parse(tag)
                if (res.errorPos != null) {
                    plainText += text[i]
                    i++
                } else {
                    val node = res.root
                    if (!Hyperlink.canRender(node)) {
                        plainText += text[i]
                        i++
                    } else {
                        output += RichTextNode.Text(plainText)
                        plainText = ""
                        output += Hyperlink.render(node)
                        i += (res.nodeMap[node]?.last ?: 0) + 1
                    }
                }
            }
        }

        if (plainText.isNotEmpty()) {
            output += RichTextNode.Text(plainText)
        }

        return output
    }
}
