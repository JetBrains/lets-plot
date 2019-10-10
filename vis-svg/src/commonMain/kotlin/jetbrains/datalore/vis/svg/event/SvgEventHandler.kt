package jetbrains.datalore.vis.svg.event

import jetbrains.datalore.base.event.Event
import jetbrains.datalore.vis.svg.SvgNode

interface SvgEventHandler<EventT : Event> {
    fun handle(node: SvgNode, e: EventT)
}