/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.util

import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.commons.encoding.RGBEncoder
import org.jetbrains.letsPlot.datamodel.svg.dom.XmlNamespace.SVG_NAMESPACE_URI
import org.jetbrains.letsPlot.datamodel.svg.dom.XmlNamespace.XLINK_NAMESPACE_URI
import org.jetbrains.letsPlot.datamodel.svg.dom.XmlNamespace.XLINK_PREFIX

class SvgToString(
    private val rgbEncoder: RGBEncoder,
    private val useCssPixelatedImageRendering: Boolean = true // true for browser, false for Batik.Transcoder or Cairo
) {
    fun render(svg: SvgElement): String {
        val buffer = StringBuilder()
        renderElement(svg, buffer, 0)
        return buffer.toString()
    }

    private fun renderElement(svgElement: SvgElement, buffer: StringBuilder, level: Int) {
        if (level > 0) {
            crlf(buffer, level)
        }
        buffer.append('<').append(svgElement.elementName)
        if (svgElement.elementName == "svg") {
            buffer.append(" xmlns").append("=\"").append(SVG_NAMESPACE_URI).append('"')
            buffer.append(" xmlns:").append(XLINK_PREFIX).append("=\"").append(XLINK_NAMESPACE_URI).append('"')
        }

        for (key in svgElement.attributeKeys) {
            buffer.append(' ')
            val name: String = key.name
            val value: String = svgElement.getAttribute(name).get().toString()
            //if (key.hasNamespace()) {
//  buffer.append(key.getNamespaceUri()).append(':');
//}
            buffer.append(name).append("=\"").append(value).append('"')
        }
        buffer.append('>')
        // children
        if (svgElement.isPrebuiltSubtree) {
            for (childNode in svgElement.children()) {
                crlf(buffer, level + 1)
                val subtree =
                    PrebuiltSvgSubtree(childNode, level + 1)
                buffer.append(subtree.asString)
            }
        } else {
            for (childNode in svgElement.children()) {
                @Suppress("NAME_SHADOWING")
                var childNode = childNode
                when (childNode) {
                    is SvgTextNode -> {
                        renderTextNode(
                            childNode,
                            buffer,
                            level
                        )
                    }

                    is SvgElement -> {
                        if (childNode is SvgImageElementEx) {
                            childNode = childNode.asImageElement(rgbEncoder)
                        }

                        if (childNode is SvgImageElement) {
                            val style = when (useCssPixelatedImageRendering) {
                                true -> "image-rendering: optimizeSpeed; image-rendering: pixelated"
                                false -> "image-rendering: optimizeSpeed"
                            }
                            childNode.setAttribute(SvgConstants.SVG_STYLE_ATTRIBUTE, style)
                        }

                        renderElement(
                            childNode as SvgElement,
                            buffer,
                            level + 1
                        )
                    }

                    else -> {
                        throw IllegalStateException("Can't render unsupported svg node: $childNode")
                    }
                }
            }
        }
        crlf(buffer, level)
        buffer.append("</").append(svgElement.elementName).append('>')
    }

    private fun renderTextNode(node: SvgTextNode, buffer: StringBuilder, level: Int) {
        crlf(buffer, level)
        buffer.append(htmlEscape(node.textContent().get()))
    }

    companion object {
        private const val TAB = 2

        fun htmlEscape(str: String): String {
            val escaped = StringBuilder()
            str.forEach { ch ->
                escaped.append(
                    when (ch) {
                        '&' -> "&amp;"
                        '<' -> "&lt;"
                        '>' -> "&gt;"
                        '"' -> "&quot;"
                        '\'' -> "&#39;"
                        else -> ch
                    }
                )
            }
            return escaped.toString()
        }

        fun crlf(buffer: StringBuilder, level: Int) {
            buffer.append('\n')
            for (i in 0 until level * TAB) {
                buffer.append(' ')
            }
        }
    }
}