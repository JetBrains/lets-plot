package jetbrains.datalore.visualization.plot.builder.interact.render

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.SvgNode
import jetbrains.datalore.visualization.plot.base.interact.TipLayoutHint.Kind
import jetbrains.datalore.visualization.plot.builder.interact.TooltipSpec
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip
import jetbrains.datalore.visualization.plot.builder.tooltip.TooltipOrientation
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
            .run { toMeasured(this) }
            .run { myLayoutManager.arrange(this, cursor) }
            .run { toTooltipEntries(this) }
            .run { myTooltipUpdater.updateTooltips(this) }
    }

    fun hideTooltip() {
        myTooltipUpdater.updateTooltips(emptyList())
    }

    private fun toMeasured(tooltipSpecs: List<TooltipSpec>): List<MeasuredTooltip> {
        val measuredTooltips = ArrayList<MeasuredTooltip>()

        for (tooltipSpec in tooltipSpecs) {
            if (tooltipSpec.lines.isEmpty()) {
                continue
            }

            measuredTooltips.add(
                    MeasuredTooltip(
                            tooltipSpec,
                            myTooltipMeter.measure(
                                    tooltipSpec.lines,
                                    getFontSize(tooltipSpec.layoutHint.kind)
                            )
                    )
            )
        }

        return measuredTooltips
    }

    companion object {

        fun toTooltipEntries(positionedTooltips: List<PositionedTooltip>): List<TooltipViewModel> {
            return positionedTooltips.map {
                it.run {
                    TooltipViewModel(
                        tooltipCoord = tooltipCoord,
                        stemCoord = stemCoord,
                        orientation = getOrientation(),
                        fill = tooltipSpec.fill,
                        fontSize = tooltipSpec.getFontSize(),
                        text = tooltipSpec.lines
                    )
                }
            }
        }

        private fun PositionedTooltip.getOrientation() = when (tooltipSpec.layoutHint.kind) {
            Kind.VERTICAL_TOOLTIP -> TooltipOrientation.ANY
            Kind.HORIZONTAL_TOOLTIP, Kind.Y_AXIS_TOOLTIP -> TooltipOrientation.HORIZONTAL
            Kind.CURSOR_TOOLTIP, Kind.X_AXIS_TOOLTIP -> TooltipOrientation.VERTICAL
        }

        private fun TooltipSpec.getFontSize() = getFontSize (layoutHint.kind)
        private fun getFontSize(kind: Kind) = when (kind) {
            Kind.VERTICAL_TOOLTIP, Kind.HORIZONTAL_TOOLTIP, Kind.CURSOR_TOOLTIP -> Tooltip.FONT_SIZE.toDouble()
            Kind.X_AXIS_TOOLTIP, Kind.Y_AXIS_TOOLTIP -> Tooltip.AXIS_FONT_SIZE.toDouble()
        }
    }
}
