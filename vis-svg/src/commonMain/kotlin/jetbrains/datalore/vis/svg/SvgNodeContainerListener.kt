package jetbrains.datalore.vis.svg

import jetbrains.datalore.vis.svg.event.SvgAttributeEvent

interface SvgNodeContainerListener {
    fun onAttributeSet(element: SvgElement, event: SvgAttributeEvent<*>)
    fun onNodeAttached(node: SvgNode)
    fun onNodeDetached(node: SvgNode)
}