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
        println("TooltipLayer $viewport")
        val tooltipLayer = SvgGElement()
        decorationLayer.children().add(tooltipLayer)

        myTooltipMeter = TooltipMeter(tooltipLayer)
        myTooltipUpdater = TooltipUpdater(tooltipLayer)
        myLayoutManager = LayoutManager(viewport, LayoutManager.HorizontalAlignment.LEFT)
    }


    fun showTooltips(cursor: DoubleVector, tooltipSpecs: List<TooltipSpec>) {
        updateTooltips(
                myLayoutManager.arrange(
                        toMeasured(tooltipSpecs),
                        cursor)
        )
    }

    fun hideTooltip() {
        updateTooltips(emptyList())
    }

    private fun updateTooltips(tooltips: List<PositionedTooltip>) {
        myTooltipUpdater.updateTooltips(toTooltipEntries(tooltips))
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
                                    getSortedText(tooltipSpec),
                                    getFontSize(tooltipSpec)
                            )
                    )
            )
        }

        return measuredTooltips
    }

    companion object {

        fun toTooltipEntries(positionedTooltips: List<PositionedTooltip>): List<TooltipEntry> {
            val tooltipEntries = ArrayList<TooltipEntry>()
            for (positionedTooltip in positionedTooltips) {

                val tooltipSpec = positionedTooltip.tooltipSpec
                val content = TooltipContent(
                        getSortedText(tooltipSpec),
                        tooltipSpec.fill,
                        getFontSize(tooltipSpec)
                )

                tooltipEntries.add(
                        TooltipEntry(
                                content,
                                positionedTooltip.tooltipCoord,
                                positionedTooltip.stemCoord,
                                getOrientation(tooltipSpec.layoutHint.kind)
                        )
                )
            }
            return tooltipEntries
        }

        private fun getOrientation(kind: Kind): TooltipOrientation {
            return when (kind) {
                Kind.VERTICAL_TOOLTIP -> TooltipOrientation.ANY

                Kind.HORIZONTAL_TOOLTIP, Kind.Y_AXIS_TOOLTIP -> TooltipOrientation.HORIZONTAL

                Kind.CURSOR_TOOLTIP, Kind.X_AXIS_TOOLTIP -> TooltipOrientation.VERTICAL
            }
        }

        private fun getFontSize(tooltipSpec: TooltipSpec): Double {
            return when (tooltipSpec.layoutHint.kind) {
                Kind.VERTICAL_TOOLTIP, Kind.HORIZONTAL_TOOLTIP, Kind.CURSOR_TOOLTIP -> Tooltip.FONT_SIZE.toDouble()

                Kind.X_AXIS_TOOLTIP, Kind.Y_AXIS_TOOLTIP -> Tooltip.AXIS_FONT_SIZE.toDouble()
            }
        }

        private fun getSortedText(spec: TooltipSpec): List<String> {
            val text = ArrayList(spec.lines)
            text.sort()
            return text
        }
    }
}
