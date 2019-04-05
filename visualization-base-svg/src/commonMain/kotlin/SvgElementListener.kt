package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.visualization.base.svg.event.SvgAttributeEvent

interface SvgElementListener {
    fun onAttrSet(event: SvgAttributeEvent<*>)
}