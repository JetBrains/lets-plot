package jetbrains.datalore.vis.svg

import jetbrains.datalore.vis.svg.event.SvgAttributeEvent

open class SvgNodeContainerAdapter : SvgNodeContainerListener {
    override fun onAttributeSet(element: SvgElement, event: SvgAttributeEvent<*>) {}

    override fun onNodeAttached(node: SvgNode) {}

    override fun onNodeDetached(node: SvgNode) {}
}