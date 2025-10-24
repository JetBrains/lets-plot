package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.xml.Xml
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.RichTextNode

internal object Plaintext {
    fun parse(text: String): List<RichTextNode> {
        val output = mutableListOf<RichTextNode>()

        var i = 0
        var plainText = ""

        while (i < text.length) {
            if (text[i] == '<') {
                val (node, nodeLocations, err) = Xml.parse(text.substring(i))
                if (err == null) {
                    if (Hyperlink.canRender(node)) {
                        output += RichTextNode.Text(plainText)
                        output += Hyperlink.render(node)

                        plainText = ""
                        i += nodeLocations.getValue(node).last + 1
                    } else {
                        // Not a hyperlink tag - treat as plain text
                        plainText += text[i]
                        i++
                    }
                } else {
                    // Malformed tag - treat as plain text
                    plainText += text[i]
                    i++
                }
            } else {
                // Plain text character
                plainText += text[i]
                i++
            }
        }

        if (plainText.isNotEmpty()) {
            output += RichTextNode.Text(plainText)
        }

        return output
    }
}
