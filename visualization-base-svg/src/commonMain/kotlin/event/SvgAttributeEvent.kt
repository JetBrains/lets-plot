package jetbrains.datalore.visualization.base.svg.event

import jetbrains.datalore.base.event.Event
import jetbrains.datalore.visualization.base.svg.SvgAttributeSpec

class SvgAttributeEvent<ValueT>(
        val attrSpec: SvgAttributeSpec<ValueT>,
        val oldValue: ValueT?,
        val newValue: ValueT?
) : Event()