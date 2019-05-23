package jetbrains.datalore.visualization.plot.builder.interact.render

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgGraphicsElement
import jetbrains.datalore.visualization.base.svg.SvgNode
import jetbrains.datalore.visualization.plot.builder.tooltip.TooltipWithStem

internal class TooltipMeter(tooltipLayer: SvgNode) {
    private val myHiddenTooltip = TooltipWithStem()

    init {
        myHiddenTooltip.rootGroup.visibility().set(SvgGraphicsElement.Visibility.HIDDEN)
        tooltipLayer.children().add(myHiddenTooltip.rootGroup)
    }

    fun measure(text: List<String>, fontSize: Double): DoubleVector {
        myHiddenTooltip.update(TooltipManager.IGNORED_COLOR, text, fontSize)
        return myHiddenTooltip.contentRect.dimension
    }
}