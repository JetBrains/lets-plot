package jetbrains.datalore.visualization.plot.gog.plot.event3

import jetbrains.datalore.visualization.plot.gog.plot.event3.tooltip.TooltipSpec
import observable.collections.Collections.unmodifiableList

class TargetTooltipSpec(tooltipSpecs: List<TooltipSpec>) {

    val tooltipSpecs: List<TooltipSpec>

    init {
        this.tooltipSpecs = unmodifiableList(ArrayList(tooltipSpecs))
    }

    companion object {
        val EMPTY = TargetTooltipSpec(emptyList())
    }
}
