package jetbrains.datalore.visualization.plot.builder.interact.render

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgGraphicsElement
import jetbrains.datalore.visualization.base.svg.SvgNode
import jetbrains.datalore.visualization.plot.builder.tooltip.TooltipBox

internal class TooltipMeter(
    private val tooltipLayer: SvgNode
) {
    fun measure(text: List<String>, style: String): DoubleVector {
        val tt = TooltipBox()
        tt.rootGroup.visibility().set(SvgGraphicsElement.Visibility.HIDDEN)
        tooltipLayer.children().add(tt.rootGroup)
        tt.setContent(TooltipUpdater.IGNORED_COLOR, text, style)

        return tt.contentRect.dimension.also { tooltipLayer.children().remove(tt.rootGroup) }
    }
}