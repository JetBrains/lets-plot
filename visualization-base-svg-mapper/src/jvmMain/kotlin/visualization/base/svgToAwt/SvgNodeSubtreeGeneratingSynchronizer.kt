package jetbrains.datalore.visualization.base.svgToAwt

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.mapper.core.Synchronizer
import jetbrains.datalore.mapper.core.SynchronizerContext
import jetbrains.datalore.visualization.base.svg.SvgElement
import jetbrains.datalore.visualization.base.svg.SvgNode
import jetbrains.datalore.visualization.base.svg.SvgTextNode
import jetbrains.datalore.visualization.base.svg.event.SvgEventSpec
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements.CIRCLE
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements.GROUP
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements.LINE
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements.PATH
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements.RECT
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimNode
import org.apache.batik.dom.AbstractDocument
import org.apache.batik.dom.events.DOMMouseEvent
import org.apache.batik.dom.svg.*
import org.apache.batik.util.SVGConstants
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.events.EventListener

internal class SvgNodeSubtreeGeneratingSynchronizer(
        private val mySource: SvgNode,
        private val myTarget: Node,
        private val myDoc: AbstractDocument) :
        Synchronizer {

    private var myHandlerRegs: MutableList<Registration>? = null

    override fun attach(ctx: SynchronizerContext) {
        myHandlerRegs = ArrayList()
        for (sourceNode in mySource.children()) {
            myTarget.appendChild(generateNode(sourceNode))
        }
    }

    override fun detach() {
        for (handlerReg in myHandlerRegs!!) {
            handlerReg.remove()
        }
        myHandlerRegs = null

        if (myTarget.hasChildNodes()) {
            var child: Node? = myTarget.firstChild
            while (child != null) {
                val nextSibling = child.nextSibling
                myTarget.removeChild(child)
                child = nextSibling
            }
        }
    }

    private fun generateNode(source: SvgNode): Node {
        if (source is SvgSlimNode) {
            return generateSlimNode(source as SvgSlimNode)
        } else if (source is SvgElement) {
            return generateElement(source)
        } else if (source is SvgTextNode) {
            return generateTextNode(source)
        }
        throw IllegalStateException("Can't generate dom for svg node " + source::class.simpleName)
    }

    private fun generateElement(source: SvgElement): Element {
        val target = Utils.newBatikElement(source, myDoc)
        for (key in source.attributeKeys) {
            target.setAttribute(key.name, source.getAttribute(key.name).get().toString())
        }

        val eventSpecs = source.handlersSet().get()
        if (!eventSpecs.isEmpty()) {
            hookEventHandlers(source, target as SVGOMElement, eventSpecs)
        }

        for (child in source.children()) {
            target.appendChild(generateNode(child))
        }
        return target
    }

    private fun generateTextNode(source: SvgTextNode): Node {
        val textNode = Utils.newBatikText(source, myDoc)
        textNode.nodeValue = source.textContent().get()
        return textNode
    }

    private fun generateSlimNode(source: SvgSlimNode): Element {
        val target: Element
        when (source.elementName) {
            GROUP -> {
                target = SVGOMGElement(null, myDoc)
                for (child in source.slimChildren) {
                    target.appendChild(generateSlimNode(child))
                }
            }
            LINE -> target = SVGOMLineElement(null, myDoc)
            CIRCLE -> target = SVGOMCircleElement(null, myDoc)
            RECT -> target = SVGOMRectElement(null, myDoc)
            PATH -> target = SVGOMPathElement(null, myDoc)
            else -> throw IllegalStateException("Unsupported slim node " + source::class.simpleName + " '" + source.elementName + "'")
        }

        for (attr in source.attributes) {
            target.setAttribute(attr.key, attr.value)
        }
        return target
    }

    private fun hookEventHandlers(source: SvgElement, target: SVGOMElement, eventSpecs: Set<SvgEventSpec>) {
        for (spec in eventSpecs) {
            when (spec) {
                SvgEventSpec.MOUSE_CLICKED -> addMouseHandler(source, target, spec, SVGConstants.SVG_CLICK_EVENT_TYPE)
                SvgEventSpec.MOUSE_PRESSED -> addMouseHandler(source, target, spec, SVGConstants.SVG_MOUSEDOWN_EVENT_TYPE)
                SvgEventSpec.MOUSE_RELEASED -> addMouseHandler(source, target, spec, SVGConstants.SVG_MOUSEUP_EVENT_TYPE)
                SvgEventSpec.MOUSE_OVER -> addMouseHandler(source, target, spec, SVGConstants.SVG_MOUSEOVER_EVENT_TYPE)
                SvgEventSpec.MOUSE_MOVE -> addMouseHandler(source, target, spec, SVGConstants.SVG_MOUSEMOVE_EVENT_TYPE)
                SvgEventSpec.MOUSE_OUT -> addMouseHandler(source, target, spec, SVGConstants.SVG_MOUSEOUT_EVENT_TYPE)
                else -> throw IllegalArgumentException("unexpected event spec $spec")
            }
        }
    }

    private fun addMouseHandler(source: SvgElement, target: SVGOMElement, spec: SvgEventSpec, eventType: String) {
        val listener = EventListener { evt ->
            evt.stopPropagation()
            val e = evt as DOMMouseEvent
            source.dispatch(spec, MouseEvent(e.clientX, e.clientY, Utils.getButton(e), Utils.getModifiers(e)))
        }
        target.addEventListener(eventType, listener, false)
        myHandlerRegs!!.add(object : Registration() {
            override fun doRemove() {
                target.removeEventListener(eventType, listener, false)
            }
        })
    }

}
