/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.batik

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Registration
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextNode
import org.jetbrains.letsPlot.datamodel.svg.event.SvgEventSpec
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimElements
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimNode
import org.jetbrains.letsPlot.datamodel.mapping.svg.shared.TargetPeer
import org.apache.batik.anim.dom.*
import org.apache.batik.dom.AbstractDocument
import org.apache.batik.dom.events.DOMMouseEvent
import org.apache.batik.util.SVGConstants
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.EventTarget


internal class BatikTargetPeer(private val doc: AbstractDocument) : TargetPeer<Node> {
    override fun appendChild(target: Node, child: Node) {
        target.appendChild(child)
    }

    override fun removeAllChildren(target: Node) {
        if (target.hasChildNodes()) {
            var child: Node? = target.firstChild
            while (child != null) {
                val nextSibling = child.nextSibling
                target.removeChild(child)
                child = nextSibling
            }
        }
    }

    override fun newSvgElement(source: SvgElement): Node {
        return Utils.newBatikElement(source, doc)
    }

    override fun newSvgTextNode(source: SvgTextNode): Node {
        val textNode = Utils.newBatikText(source, doc)
        textNode.nodeValue = source.textContent().get()
        return textNode
    }

    override fun newSvgSlimNode(source: SvgSlimNode): Node {
        return when (source.elementName) {
            SvgSlimElements.GROUP -> SVGOMGElement(null, doc)
            SvgSlimElements.LINE -> SVGOMLineElement(null, doc)
            SvgSlimElements.CIRCLE -> SVGOMCircleElement(null, doc)
            SvgSlimElements.RECT -> SVGOMRectElement(null, doc)
            SvgSlimElements.PATH -> SVGOMPathElement(null, doc)
            else -> throw IllegalStateException("Unsupported slim node " + source::class.simpleName + " '" + source.elementName + "'")
        }
    }

    override fun setAttribute(target: Node, name: String, value: String) {
        (target as Element).setAttribute(name, value)
    }

    override fun hookEventHandlers(source: SvgElement, target: Node, eventSpecs: Set<SvgEventSpec>): Registration {
        val regs = CompositeRegistration()

        target as EventTarget
        for (spec in eventSpecs) {
            val handlerReg = when (spec) {
                SvgEventSpec.MOUSE_CLICKED -> addMouseHandler(source, target, spec, SVGConstants.SVG_CLICK_EVENT_TYPE)
                SvgEventSpec.MOUSE_PRESSED -> addMouseHandler(
                    source,
                    target,
                    spec,
                    SVGConstants.SVG_MOUSEDOWN_EVENT_TYPE
                )
                SvgEventSpec.MOUSE_RELEASED -> addMouseHandler(
                    source,
                    target,
                    spec,
                    SVGConstants.SVG_MOUSEUP_EVENT_TYPE
                )
                SvgEventSpec.MOUSE_OVER -> addMouseHandler(source, target, spec, SVGConstants.SVG_MOUSEOVER_EVENT_TYPE)
                SvgEventSpec.MOUSE_MOVE -> addMouseHandler(source, target, spec, SVGConstants.SVG_MOUSEMOVE_EVENT_TYPE)
                SvgEventSpec.MOUSE_OUT -> addMouseHandler(source, target, spec, SVGConstants.SVG_MOUSEOUT_EVENT_TYPE)
                else -> throw IllegalArgumentException("unexpected event spec $spec")
            }
            regs.add(handlerReg)
        }

        return regs
    }

    private fun addMouseHandler(
        source: SvgElement,
        target: EventTarget,
        spec: SvgEventSpec,
        eventType: String
    ): Registration {

        val listener = EventListener { evt ->
            evt.stopPropagation()
            val e = evt as DOMMouseEvent
            source.dispatch(
                spec, MouseEvent(
                    e.clientX, e.clientY,
                    Utils.getButton(e),
                    Utils.getModifiers(e)
                )
            )
        }
        target.addEventListener(eventType, listener, false)
        return object : Registration() {
            override fun doRemove() {
                target.removeEventListener(eventType, listener, false)
            }
        }
    }
}