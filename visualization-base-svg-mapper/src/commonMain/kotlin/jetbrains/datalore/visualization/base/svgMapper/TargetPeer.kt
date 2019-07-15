package jetbrains.datalore.visualization.base.svgMapper

import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.svg.SvgElement
import jetbrains.datalore.visualization.base.svg.SvgTextNode
import jetbrains.datalore.visualization.base.svg.event.SvgEventSpec
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimNode

interface TargetPeer<T> {
    fun appendChild(target: T, child: T)
    fun removeAllChildren(target: T)
    fun newSvgElement(source: SvgElement): T
    fun newSvgTextNode(source: SvgTextNode): T
    fun newSvgSlimNode(source: SvgSlimNode): T
    fun setAttribute(target: T, name: String, value: String)
    fun hookEventHandlers(source: SvgElement, target: T, eventSpecs: Set<SvgEventSpec>): Registration
}