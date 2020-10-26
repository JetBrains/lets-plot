/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgToString

import jetbrains.datalore.vis.svg.SvgElement
import jetbrains.datalore.vis.svg.SvgNode
import jetbrains.datalore.vis.svg.SvgTextNode
import jetbrains.datalore.vis.svg.slim.SvgSlimElements.CIRCLE
import jetbrains.datalore.vis.svg.slim.SvgSlimElements.GROUP
import jetbrains.datalore.vis.svg.slim.SvgSlimElements.LINE
import jetbrains.datalore.vis.svg.slim.SvgSlimElements.PATH
import jetbrains.datalore.vis.svg.slim.SvgSlimElements.RECT
import jetbrains.datalore.vis.svg.slim.SvgSlimNode
import jetbrains.datalore.vis.svgToString.SvgToString.Companion.crlf

internal class PrebuiltSvgSubtree(source: SvgNode, level: Int) {
    val asString: String

    private fun generateSvgNode(source: SvgNode, level: Int): StringBuilder {
        return when (source) {
            is SvgSlimNode -> generateSlimNode(source as SvgSlimNode, level)
            is SvgElement -> generateElement(source, level)
            is SvgTextNode -> generateTextNode(source)
            else -> throw IllegalStateException("Can't generate dom for svg node " + source::class.simpleName)
        }
    }

    private fun generateSlimNode(source: SvgSlimNode, level: Int): StringBuilder {
        val buffer = StringBuilder()
        crlf(buffer, level)
        buffer.append("<" + source.elementName)
        for (attr in source.attributes) {
            buffer.append(' ')
                .append(attr.key).append('=')
                .append('"')
                .append(attr.value)
                .append('"')
        }
        when (source.elementName) {
            GROUP -> buffer.append(" >")
            LINE, CIRCLE, RECT, PATH -> {
                buffer.append(" />")
                return buffer
            }
            else -> throw IllegalStateException("Unsupported slim node " + source::class.simpleName + " '" + source.elementName + "'")
        }
        // group content
        for (child in source.slimChildren) {
            buffer.append(generateSlimNode(child, level + 1))
        }
        crlf(buffer, level)
        buffer.append("</g>")
        return buffer
    }

    private fun generateElement(source: SvgElement, level: Int): StringBuilder {
        val buffer = StringBuilder()
        crlf(buffer, level)
        buffer.append("<" + source.elementName)
        for (key in source.attributeKeys) {
            buffer.append(' ')
                .append(key.name).append('=')
                .append('"')
                .append(source.getAttribute(key.name).get())
                .append('"')
        }
        for (child in source.children()) {
            buffer.append(generateSvgNode(child, level + 1))
        }
        crlf(buffer, level)
        buffer.append("</" + source.elementName + ">")
        return buffer
    }

    private fun generateTextNode(source: SvgTextNode): StringBuilder {
        return StringBuilder(source.textContent().get())
    }

    init {
        val buffer = generateSvgNode(source, level)
        asString = buffer.toString()
    }
}