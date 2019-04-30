package jetbrains.datalore.visualization.plot.gog.plot.event3

import jetbrains.datalore.visualization.plot.gog.plot.event3.tooltip.TooltipSpec
import jetbrains.datalore.base.observable.collections.Collections.unmodifiableList

class TargetTooltipSpec(tooltipSpecs: List<TooltipSpec>) {

    val tooltipSpecs: List<TooltipSpec> = unmodifiableList(ArrayList(tooltipSpecs))

    companion object {
        val EMPTY = TargetTooltipSpec(emptyList())
    }
}
