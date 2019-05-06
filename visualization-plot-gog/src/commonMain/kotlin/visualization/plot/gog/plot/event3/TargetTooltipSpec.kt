package jetbrains.datalore.visualization.plot.gog.plot.event3

import jetbrains.datalore.visualization.plot.gog.plot.event3.tooltip.TooltipSpec

class TargetTooltipSpec(tooltipSpecs: List<TooltipSpec>) {

    val tooltipSpecs: List<TooltipSpec> = ArrayList(tooltipSpecs)

    companion object {
        val EMPTY = TargetTooltipSpec(emptyList())
    }
}
