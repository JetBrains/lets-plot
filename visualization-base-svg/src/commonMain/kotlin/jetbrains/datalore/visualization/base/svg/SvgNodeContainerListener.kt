package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.visualization.base.svg.event.SvgAttributeEvent

interface SvgNodeContainerListener {
    fun onAttributeSet(element: SvgElement, event: SvgAttributeEvent<*>)
    fun onNodeAttached(node: SvgNode)
    fun onNodeDetached(node: SvgNode)
}