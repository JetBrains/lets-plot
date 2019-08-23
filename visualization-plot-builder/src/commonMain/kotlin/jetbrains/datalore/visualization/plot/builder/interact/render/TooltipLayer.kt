package jetbrains.datalore.visualization.plot.builder.interact.render

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.SvgNode
import jetbrains.datalore.visualization.plot.builder.interact.TooltipSpec
import jetbrains.datalore.visualization.plot.builder.tooltip.layout.LayoutManager
import jetbrains.datalore.visualization.plot.builder.tooltip.layout.LayoutManager.MeasuredTooltip
import jetbrains.datalore.visualization.plot.builder.tooltip.layout.LayoutManager.PositionedTooltip

internal class TooltipLayer(decorationLayer: SvgNode, viewport: DoubleRectangle) {

    private val myTooltipMeter: TooltipMeter
    private val myTooltipUpdater: TooltipUpdater
    private val myLayoutManager: LayoutManager

    init {
        val tooltipLayer = SvgGElement()
        decorationLayer.children().add(tooltipLayer)

        myTooltipMeter = TooltipMeter(tooltipLayer)
        myTooltipUpdater = TooltipUpdater(tooltipLayer)
        myLayoutManager = LayoutManager(viewport, LayoutManager.HorizontalAlignment.LEFT)
    }


    fun showTooltips(cursor: DoubleVector, tooltipSpecs: List<TooltipSpec>) {
        tooltipSpecs
            .run(::toMeasured)
            .run { myLayoutManager.arrange(this, cursor) }
            .run(::toViewModels)
            .run(myTooltipUpdater::drawTooltips)
    }

    fun hideTooltip() {
        myTooltipUpdater.drawTooltips(emptyList())
    }

    private fun toMeasured(tooltipSpecs: List<TooltipSpec>): List<MeasuredTooltip> {
        val measuredTooltips = ArrayList<MeasuredTooltip>()

        for (tooltipSpec in tooltipSpecs) {
            if (tooltipSpec.lines.isEmpty()) {
                continue
            }

            tooltipSpec
                .run { myTooltipMeter.measure(lines, TooltipViewModel.style(layoutHint.kind)) }
                .run { MeasuredTooltip(tooltipSpec, this)}
                .run (measuredTooltips::add)
        }

        return measuredTooltips
    }

    companion object {

        fun toViewModels(positionedTooltips: List<PositionedTooltip>): List<TooltipViewModel> {
            return positionedTooltips.map {
                it.run {
                    TooltipViewModel(
                        tooltipCoord = tooltipCoord,
                        stemCoord = stemCoord,
                        fill = tooltipSpec.fill,
                        text = tooltipSpec.lines,
                        orientation = TooltipViewModel.orientation(hintKind),
                        style = TooltipViewModel.style(hintKind)
                    )
                }
            }
        }
    }
}
