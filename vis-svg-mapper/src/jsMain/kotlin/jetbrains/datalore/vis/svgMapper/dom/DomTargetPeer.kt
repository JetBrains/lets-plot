/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.dom

import jetbrains.datalore.base.event.dom.DomEventUtil
import jetbrains.datalore.base.js.dom.DomEventType
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Registration
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextNode
import org.jetbrains.letsPlot.datamodel.svg.event.SvgEventSpec
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimNode
import jetbrains.datalore.vis.svgMapper.TargetPeer
import jetbrains.datalore.vis.svgMapper.dom.domUtil.DomUtil
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.EventTarget
import org.w3c.dom.events.MouseEvent
import kotlinx.browser.document

internal class DomTargetPeer : TargetPeer<Node> {
    override fun appendChild(target: Node, child: Node) {
        target.appendChild(child)
    }

    override fun removeAllChildren(target: Node) {
        if (target.hasChildNodes()) {
            var child = target.firstChild
            while (child != null) {
                val nextSibling = child.nextSibling
                target.removeChild(child)
                child = nextSibling
            }
        }
    }

    override fun newSvgElement(source: SvgElement): Node {
        return DomUtil.generateElement(source)
    }

    override fun newSvgTextNode(source: SvgTextNode): Node {
        val textNode = document.createTextNode("")
        textNode.nodeValue = source.textContent().get()
        return textNode
    }

    override fun newSvgSlimNode(source: SvgSlimNode): Node {
        return DomUtil.generateSlimNode(source)
    }

    override fun setAttribute(target: Node, name: String, value: String) {
        (target as Element).setAttribute(name, value)
    }

    override fun hookEventHandlers(source: SvgElement, target: Node, eventSpecs: Set<SvgEventSpec>): Registration {
        val regs = CompositeRegistration()
        for (spec in eventSpecs) {
            val eventType = when (spec) {
                SvgEventSpec.MOUSE_CLICKED -> DomEventType.CLICK
                SvgEventSpec.MOUSE_PRESSED -> DomEventType.MOUSE_DOWN
                SvgEventSpec.MOUSE_RELEASED -> DomEventType.MOUSE_UP
                SvgEventSpec.MOUSE_OVER -> DomEventType.MOUSE_OVER
                SvgEventSpec.MOUSE_MOVE -> DomEventType.MOUSE_MOVE
                SvgEventSpec.MOUSE_OUT -> DomEventType.MOUSE_OUT
                else -> throw IllegalArgumentException("unexpected event spec $spec")
            }

            regs.add(
                addMouseHandler(source, target as EventTarget, spec, eventType.name)
            )
        }
        return regs
    }

    private fun addMouseHandler(
        source: SvgElement,
        target: EventTarget,
        spec: SvgEventSpec,
        eventType: String
    ): Registration {
        val listener: EventListener = object : EventListener {
            override fun handleEvent(event: Event) {
                event.stopPropagation()
                val e = event as MouseEvent
                val targetEvent = jetbrains.datalore.base.event.MouseEvent(
                    e.clientX,
                    e.clientY,
                    DomEventUtil.getButton(e),
                    DomEventUtil.getModifiers(e)
                )
                source.dispatch(spec, targetEvent)
            }
        }
        target.addEventListener(eventType, listener, false)
        return object : Registration() {
            override fun doRemove() {
                target.removeEventListener(eventType, listener, false)
            }
        }
    }
}