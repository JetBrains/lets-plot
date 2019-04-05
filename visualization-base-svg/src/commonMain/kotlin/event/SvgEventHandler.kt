package jetbrains.datalore.visualization.base.svg.event

import jetbrains.datalore.base.event.Event
import jetbrains.datalore.visualization.base.svg.SvgNode

interface SvgEventHandler<EventT : Event> {
    fun handle(node: SvgNode, e: EventT)
}