/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgToString

import jetbrains.datalore.vis.svg.SvgElement
import jetbrains.datalore.vis.svg.SvgImageElementEx
import jetbrains.datalore.vis.svg.SvgImageElementEx.RGBEncoder
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svg.SvgTextNode
import jetbrains.datalore.vis.svg.XmlNamespace.SVG_NAMESPACE_URI
import jetbrains.datalore.vis.svg.XmlNamespace.XLINK_NAMESPACE_URI
import jetbrains.datalore.vis.svg.XmlNamespace.XLINK_PREFIX

class SvgToString(private val rgbEncoder: RGBEncoder?) {

    fun render(svg: SvgSvgElement): String {
        val buffer = StringBuilder()
        renderElement(svg, buffer, 0)
        return buffer.toString()
    }

    private fun renderElement(svgElement: SvgElement, buffer: StringBuilder, level: Int) {
        if (level > 0) {
            crlf(buffer, level)
        }
        buffer.append('<').append(svgElement.elementName)
        if (svgElement.elementName.equals("svg")) {
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
                if (childNode is SvgTextNode) {
                    renderTextNode(
                        childNode,
                        buffer,
                        level
                    )
                } else if (childNode is SvgElement) {
                    if (childNode is SvgImageElementEx) {
                        if (rgbEncoder != null) {
                            childNode = childNode.asImageElement(rgbEncoder)
                        } else {
                            // ToDo: error?
                            continue
                        }
                    }
                    renderElement(
                        childNode as SvgElement,
                        buffer,
                        level + 1
                    )
                } else {
                    throw IllegalStateException("Can't render unsupported svg node: $childNode")
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