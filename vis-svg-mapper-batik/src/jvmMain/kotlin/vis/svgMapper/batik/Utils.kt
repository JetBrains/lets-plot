/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.batik

import jetbrains.datalore.base.event.Button
import jetbrains.datalore.base.event.KeyModifiers
import org.apache.batik.anim.dom.*
import org.apache.batik.dom.AbstractDocument
import org.apache.batik.dom.events.DOMMouseEvent
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.Text

internal object Utils {
    private const val W3C_BUTTON_LEFT = 0
    private const val W3C_BUTTON_MIDDLE = 1
    private const val W3C_BUTTON_RIGHT = 2

    fun elementChildren(e: Node): MutableList<Node> {
        return object : AbstractMutableList<Node>() {
            override val size: Int
                get() = e.childNodes.length

            override fun get(index: Int): Node {
                return e.childNodes.item(index)
            }

            override fun set(index: Int, element: Node): Node {
                if (element.parentNode != null) {
                    throw IllegalStateException()
                }

                val child = get(index)
                e.replaceChild(element, child)
                return child
            }

            override fun add(index: Int, element: Node) {
                if (element.parentNode != null) {
                    throw IllegalStateException()
                }

                if (index == size) {
                    e.insertBefore(element, null)
                } else {
                    e.insertBefore(element, get(index))
                }
            }

            override fun removeAt(index: Int): Node {
                val child = get(index)
                e.removeChild(child)
                return child
            }
        }
    }

    fun newBatikElement(source: SvgElement, myDoc: AbstractDocument): Element {
        return newBatikNode(source, myDoc) as Element
    }

    fun newBatikText(source: SvgTextNode, myDoc: AbstractDocument): Text {
        return newBatikNode(source, myDoc) as Text
    }

    fun newBatikNode(source: SvgNode, myDoc: AbstractDocument): Node {
        return when (source) {
            is SvgEllipseElement -> SVGOMEllipseElement(null, myDoc)
            is SvgCircleElement -> SVGOMCircleElement(null, myDoc)
            is SvgRectElement -> SVGOMRectElement(null, myDoc)
            is SvgTextElement -> SVGOMTextElement(null, myDoc)
            is SvgPathElement -> SVGOMPathElement(null, myDoc)
            is SvgLineElement -> SVGOMLineElement(null, myDoc)
            is SvgSvgElement -> SVGOMSVGElement(null, myDoc)
            is SvgGElement -> SVGOMGElement(null, myDoc)
            is SvgStyleElement -> SVGOMStyleElement(null, myDoc)
            is SvgTextNode -> myDoc.createTextNode(null)
            is SvgTSpanElement -> SVGOMTSpanElement(null, myDoc)
            is SvgDefsElement -> SVGOMDefsElement(null, myDoc)
            is SvgClipPathElement -> SVGOMClipPathElement(null, myDoc)
            is SvgImageElement -> SVGOMImageElement(null, myDoc)
            else -> throw IllegalStateException("Unsupported SvgElement $source")
        }
    }

    fun getButton(evt: DOMMouseEvent): Button {
        return when (evt.button.toInt()) {
            W3C_BUTTON_LEFT -> Button.LEFT
            W3C_BUTTON_MIDDLE -> Button.MIDDLE
            W3C_BUTTON_RIGHT -> Button.RIGHT
            else -> Button.NONE
        }
    }

    fun getModifiers(evt: DOMMouseEvent): KeyModifiers {
        val ctrlKey = evt.ctrlKey
        val altKey = evt.altKey
        val shiftKey = evt.shiftKey
        val metaKey = evt.metaKey
        return KeyModifiers(ctrlKey, altKey, shiftKey, metaKey)
    }
}
