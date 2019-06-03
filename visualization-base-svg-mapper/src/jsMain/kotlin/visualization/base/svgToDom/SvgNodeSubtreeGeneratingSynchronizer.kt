package jetbrains.datalore.visualization.base.svgToDom

import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.mapper.core.Synchronizer
import jetbrains.datalore.mapper.core.SynchronizerContext
import jetbrains.datalore.visualization.base.svg.*
import jetbrains.datalore.visualization.base.svg.event.SvgEventSpec
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimNode
import jetbrains.datalore.visualization.base.svgToDom.domUtil.DomEventType
import jetbrains.datalore.visualization.base.svgToDom.domUtil.DomEventUtil
import jetbrains.datalore.visualization.base.svgToDom.domUtil.DomUtil
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.Text
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.svg.SVGElement
import kotlin.browser.document

class SvgNodeSubtreeGeneratingSynchronizer(private val mySource: SvgNode, private val myTarget: Node): Synchronizer {

    private var myHandlersRegs: MutableList<Registration>? = null

    override fun attach(ctx: SynchronizerContext) {
        myHandlersRegs = mutableListOf()
        if (mySource is SvgSlimNode) {
            myTarget.appendChild(generateNode(mySource))
        }
        for (sourceNode in mySource.children()) {
            myTarget.appendChild(generateNode(sourceNode))
        }
    }

    override fun detach() {
        for (handlerReg in myHandlersRegs!!) {
            handlerReg.remove()
        }
        myHandlersRegs = null

        if (myTarget.hasChildNodes()) {
            var child = myTarget.firstChild
            while (child != null) {
                val nextSibling = child.nextSibling
                myTarget.removeChild(child)
                child = nextSibling
            }
        }
    }

    private fun generateNode(source: SvgNode): Node =
            when(source) {
                is SvgSlimNode -> generateSlimNode(source)
                is SvgElement -> generateElement(source)
                is SvgTextNode -> generateTextNode(source)
                else -> throw IllegalStateException("Can't generate dom for svg node " + source::class.simpleName)
            }

    private fun generateElement(source: SvgElement): Element {
        val target = DomUtil.generateElement(source)

        for (key in source.attributeKeys) {
            target.setAttribute(key.name, source.getAttribute(key.name).get().toString())
        }

        val eventSpecs = source.handlersSet().get()
        if (eventSpecs.isNotEmpty()) {
            hookEventHandlers(source, target, eventSpecs)
        }

        for (child in source.children()) {
            target.appendChild(generateNode(source))
        }
        return target
    }

    private fun generateTextNode(source: SvgTextNode): Text {
        val textNode = document.createTextNode("")
        textNode.nodeValue = source.textContent().get()
        return textNode
    }

    private fun generateSlimNode(source: SvgSlimNode): Element {
        val target = DomUtil.generateSlimNode(source)
        if (source.elementName == SvgSlimElements.GROUP) {
            for (child in source.slimChildren) {
                target.appendChild(generateSlimNode(child))
            }
        }

        for (attr in source.attributes) {
            target.setAttribute(attr.key, attr.value)
        }

        return target
    }

    private fun hookEventHandlers(source: SvgElement, target: SVGElement, eventSpecs: Set<SvgEventSpec>) {
        for (spec in eventSpecs) {
            val eventType = when(spec) {
                SvgEventSpec.MOUSE_CLICKED -> DomEventType.CLICK
                SvgEventSpec.MOUSE_PRESSED -> DomEventType.MOUSE_DOWN
                SvgEventSpec.MOUSE_RELEASED -> DomEventType.MOUSE_UP
                SvgEventSpec.MOUSE_OVER -> DomEventType.MOUSE_OVER
                SvgEventSpec.MOUSE_MOVE -> DomEventType.MOUSE_MOVE
                SvgEventSpec.MOUSE_OUT -> DomEventType.MOUSE_OUT
                else -> throw IllegalArgumentException("unexpected event spec $spec")
            }

            addMouseHandler(source, target, spec, eventType.name)
        }
    }

    private fun addMouseHandler(source: SvgElement, target: SVGElement, spec: SvgEventSpec, eventType: String) {
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
        myHandlersRegs!!.add(object : Registration() {
            override fun doRemove() {
                target.removeEventListener(eventType, listener, false)
            }
        })
    }
}