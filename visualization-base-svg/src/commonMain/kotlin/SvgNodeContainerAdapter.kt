package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.visualization.base.svg.event.SvgAttributeEvent

class SvgNodeContainerAdapter : SvgNodeContainerListener {
    fun onAttributeSet(element: SvgElement, event: SvgAttributeEvent<*>) {}

    fun onNodeAttached(node: SvgNode) {}

    fun onNodeDetached(element: SvgNode) {}
}