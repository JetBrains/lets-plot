/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.datamodel.svg.dom.slim.WithTextGen

object SvgNodeBufferUtil {

    fun generateSvgNodeBuffer(source: SvgNode): StringBuilder {
        if (source is WithTextGen) {
            val sb = StringBuilder()
            (source as WithTextGen).appendTo(sb)
            return sb
        } else if (source is SvgElement) {
            return generateSvgElementBuffer(source)
        } else if (source is SvgTextNode) {
            return StringBuilder(source.textContent().get())
        }

        throw IllegalStateException("Can't generate dom for svg node " + source::class.simpleName)
    }

    private fun generateSvgElementBuffer(source: SvgElement): StringBuilder {
        // head
        val elementName = source.elementName
        val sb = StringBuilder()
        sb.append('<').append(elementName)
        for (key in source.attributeKeys) {
            sb.append(' ').append(key).append("=\"").append(source.getAttribute(key.name).get()).append('\"')
        }
        sb.append('>')

        // content
        for (child in source.children()) {
            sb.append(generateSvgNodeBuffer(child))
        }

        // foot
        sb.append("</").append(elementName).append('>')
        return sb
    }
}
