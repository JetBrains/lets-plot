/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.mapping.svg.domUtil

import kotlinx.browser.document
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.dom.XmlNamespace.SVG_NAMESPACE_URI
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimElements
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimNode
import org.jetbrains.letsPlot.platf.w3c.mapping.svg.domExtensions.childCount
import org.jetbrains.letsPlot.platf.w3c.mapping.svg.domExtensions.getChild
import org.jetbrains.letsPlot.platf.w3c.mapping.svg.domExtensions.insertAfter
import org.jetbrains.letsPlot.platf.w3c.mapping.svg.domExtensions.insertFirst
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.Text
import org.w3c.dom.svg.SVGElement

object DomUtil {
    fun nodeChildren(n: Node): MutableList<Node?> {
        return object : AbstractMutableList<Node?>() {

            override val size: Int
                get() = n.childCount

            override fun get(index: Int): Node? = n.getChild(index)

            override fun set(index: Int, element: Node?): Node {
                if (element!!.parentNode != null) {
                    throw IllegalStateException()
                }

                val child = get(index)!!
                n.replaceChild(child, element)
                return child
            }

            override fun add(index: Int, element: Node?) {
                if (element!!.parentNode != null) {
                    throw IllegalStateException()
                }

                if (index == 0) {
                    n.insertFirst(element)
                } else {
                    val prev = n.getChild(index - 1)
                    n.insertAfter(element, prev)
                }
            }

            override fun removeAt(index: Int): Node {
                val child = n.getChild(index)!!
                n.removeChild(child)
                return child
            }
        }
    }

    fun generateElement(source: SvgElement): SVGElement {
        return when(source) {
            is SvgEllipseElement -> createSVGElement("ellipse")
            is SvgCircleElement -> createSVGElement("circle")
            is SvgRectElement -> createSVGElement("rect")
            is SvgTextElement -> createSVGElement("text")
            is SvgPathElement -> createSVGElement("path")
            is SvgLineElement -> createSVGElement("line")
            is SvgSvgElement -> createSVGElement("svg")
            is SvgGElement -> createSVGElement("g")
            is SvgStyleElement -> createSVGElement("style")
            is SvgTSpanElement -> createSVGElement("tspan")
            is SvgDefsElement -> createSVGElement("defs")
            is SvgClipPathElement -> createSVGElement("clipPath")
            is SvgImageElement -> createSVGElement("image")
            is SvgAElement -> createSVGElement("a")
            else -> throw IllegalStateException("Unsupported svg element ${source::class.simpleName}")
        }
    }


    fun generateSlimNode(source: SvgSlimNode): Element =
        when (source.elementName) {
            SvgSlimElements.GROUP -> createSVGElement("g")
            SvgSlimElements.LINE -> createSVGElement("line")
            SvgSlimElements.CIRCLE -> createSVGElement("circle")
            SvgSlimElements.RECT -> createSVGElement("rect")
            SvgSlimElements.PATH -> createSVGElement("path")
            else -> throw IllegalStateException("Unsupported SvgSlimNode ${source::class}")
        }

    @Suppress("UNUSED_PARAMETER")
    fun generateTextElement(source: SvgTextNode): Text = document.createTextNode("")

    private fun createSVGElement(name: String): SVGElement =
        document.createElementNS(SVG_NAMESPACE_URI, name) as SVGElement
}