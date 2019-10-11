package jetbrains.datalore.vis.svg

import jetbrains.datalore.vis.svg.event.SvgAttributeEvent

interface SvgElementListener {
    fun onAttrSet(event: SvgAttributeEvent<*>)
}