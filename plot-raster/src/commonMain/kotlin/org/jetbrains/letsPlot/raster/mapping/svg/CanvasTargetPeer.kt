/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg

import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.datamodel.mapping.svg.shared.TargetPeer
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextNode
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimElements
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimNode
import org.jetbrains.letsPlot.datamodel.svg.event.SvgEventSpec
import org.jetbrains.letsPlot.raster.mapping.svg.SvgUtils.getChildren
import org.jetbrains.letsPlot.raster.mapping.svg.SvgUtils.newElement
import org.jetbrains.letsPlot.raster.scene.*

internal class CanvasTargetPeer(
    private val peer: SvgCanvasPeer
) : TargetPeer<Node> {
    override fun appendChild(target: Node, child: Node) {
        getChildren(target as Group).add(child)
    }

    override fun removeAllChildren(target: Node) {
        if (target is Group) {
            getChildren(target).clear()
        }
    }

    override fun newSvgElement(source: SvgElement): Node {
        return newElement(source, peer)
    }

    override fun newSvgTextNode(source: SvgTextNode): Node {
        TODO() // return Text(source.textContent().get())
    }

    override fun newSvgSlimNode(source: SvgSlimNode): Node {
        return when (source.elementName) {
            SvgSlimElements.GROUP -> Group()
            SvgSlimElements.LINE -> Line()
            SvgSlimElements.CIRCLE -> Circle()
            SvgSlimElements.RECT -> Rectangle()
            SvgSlimElements.PATH -> Path()
            else -> throw IllegalStateException("Unsupported slim node " + source::class.simpleName + " '" + source.elementName + "'")
        }
    }

    override fun setAttribute(target: Node, name: String, value: String) {
        SvgUtils.setAttribute(target, name, value)
    }

    override fun hookEventHandlers(source: SvgElement, target: Node, eventSpecs: Set<SvgEventSpec>): Registration {
        error("UNSUPPORTED: hookEventHandlers")
    }
}
