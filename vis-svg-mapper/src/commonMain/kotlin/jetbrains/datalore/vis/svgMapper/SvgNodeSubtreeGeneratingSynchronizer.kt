/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper

import jetbrains.datalore.base.registration.Registration
import org.jetbrains.letsPlot.datamodel.mapping.framework.Synchronizer
import org.jetbrains.letsPlot.datamodel.mapping.framework.SynchronizerContext
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextNode
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimElements
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimNode

class SvgNodeSubtreeGeneratingSynchronizer<T>(
    private val source: SvgNode,
    private val target: T,
    private val targetPeer: TargetPeer<T>
) : Synchronizer {

    private var myHandlersRegs: MutableList<Registration>? = null

    override fun attach(ctx: SynchronizerContext) {
        myHandlersRegs = ArrayList()
        require(source !is SvgSlimNode)
        { "Slim SVG node is not expected: ${source::class.simpleName}" }
//        for (sourceNode in source.children()) {
//            // TODO: why in `children` cycle?
//            targetPeer.appendChild(target, generateNode(source))
//        }
        targetPeer.appendChild(target, generateNode(source))
    }

    override fun detach() {
        for (handlerReg in myHandlersRegs!!) {
            handlerReg.remove()
        }
        myHandlersRegs = null

//        if (target.hasChildNodes()) {
//            var child = target.firstChild
//            while (child != null) {
//                val nextSibling = child.nextSibling
//                target.removeChild(child)
//                child = nextSibling
//            }
//        }
        targetPeer.removeAllChildren(target)
    }

    private fun generateNode(source: SvgNode): T =
        when (source) {
            is SvgSlimNode -> generateSlimNode(source)
            is SvgElement -> generateElement(source)
            is SvgTextNode -> generateTextNode(source)
            else -> throw IllegalStateException("Can't generate dom for svg node " + source::class.simpleName)
        }

    private fun generateElement(source: SvgElement): T {
//        val target = DomUtil.generateElement(source)
        val target = targetPeer.newSvgElement(source)

        for (key in source.attributeKeys) {
//            target.setAttribute(key.name, source.getAttribute(key.name).get().toString())
            targetPeer.setAttribute(target, key.name, source.getAttribute(key.name).get().toString())
        }

        val eventSpecs = source.handlersSet().get()
        if (eventSpecs.isNotEmpty()) {
//            hookEventHandlers(source, target, eventSpecs)
            targetPeer.hookEventHandlers(source, target, eventSpecs)
        }

        for (child in source.children()) {
//            target.appendChild(generateNode(source))
            targetPeer.appendChild(target, generateNode(child))
        }
        return target
    }

    private fun generateTextNode(source: SvgTextNode): T {
//        val textNode = document.createTextNode("")
//        textNode.nodeValue = source.textContent().get()
//        return textNode
        return targetPeer.newSvgTextNode(source)
    }

    private fun generateSlimNode(source: SvgSlimNode): T {
//        val target = DomUtil.generateSlimNode(source)
        val target = targetPeer.newSvgSlimNode(source)
        if (source.elementName == SvgSlimElements.GROUP) {
            for (child in source.slimChildren) {
//                target.appendChild(generateSlimNode(child))
                targetPeer.appendChild(target, generateSlimNode(child))
            }
        }

        for (attr in source.attributes) {
//            target.setAttribute(attr.key, attr.value)
            targetPeer.setAttribute(target, attr.key, attr.value)
        }

        return target
    }
}