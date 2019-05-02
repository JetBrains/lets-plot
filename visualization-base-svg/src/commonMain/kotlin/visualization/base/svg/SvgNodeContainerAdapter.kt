package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.visualization.base.svg.event.SvgAttributeEvent

open class SvgNodeContainerAdapter : SvgNodeContainerListener {
    override fun onAttributeSet(element: SvgElement, event: SvgAttributeEvent<*>) {}

    override fun onNodeAttached(node: SvgNode) {}

    override fun onNodeDetached(node: SvgNode) {}
}