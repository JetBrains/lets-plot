package jetbrains.datalore.vis.svg.event

import jetbrains.datalore.base.event.Event
import jetbrains.datalore.vis.svg.SvgAttributeSpec

class SvgAttributeEvent<ValueT>(
    val attrSpec: SvgAttributeSpec<ValueT>,
    val oldValue: ValueT?,
    val newValue: ValueT?
) : Event()