package jetbrains.datalore.visualization.plot.builder.event3.tooltip

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.SvgNode
import jetbrains.datalore.visualization.plot.base.event3.TipLayoutHint.Kind
import jetbrains.datalore.visualization.plot.builder.event3.TooltipManager
import jetbrains.datalore.visualization.plot.builder.event3.TooltipManager.TooltipContent
import jetbrains.datalore.visualization.plot.builder.event3.TooltipManager.TooltipEntry
import jetbrains.datalore.visualization.plot.builder.event3.TooltipManagerImpl
import jetbrains.datalore.visualization.plot.builder.event3.tooltip.layout.LayoutManager
import jetbrains.datalore.visualization.plot.builder.event3.tooltip.layout.LayoutManager.MeasuredTooltip
import jetbrains.datalore.visualization.plot.builder.event3.tooltip.layout.LayoutManager.PositionedTooltip
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip

class TooltipInteractions(decorationsRoot: SvgNode, viewport: DoubleRectangle) {

    private val myTooltipManager: TooltipManager
    private val myLayoutManager: LayoutManager

    init {
        val interactionsRoot = SvgGElement()
        decorationsRoot.children().add(interactionsRoot)

        myTooltipManager = TooltipManagerImpl(interactionsRoot)
        myLayoutManager = LayoutManager(viewport, LayoutManager.HorizontalAlignment.LEFT)
    }

    private fun drawTooltips(tooltips: List<PositionedTooltip>) {
        myTooltipManager.beginUpdate()

        for (tooltipEntry in convertToTooltipEntry(tooltips)) {
            myTooltipManager.add(tooltipEntry)
        }

        myTooltipManager.endUpdate()
    }

    private fun measuredTooltips(tooltipSpecs: List<TooltipSpec>): List<MeasuredTooltip> {
        val measuredTooltips = ArrayList<MeasuredTooltip>()

        for (tooltipSpec in tooltipSpecs) {
            if (tooltipSpec.lines.isEmpty()) {
                continue
            }

            measuredTooltips.add(
                    MeasuredTooltip(
                            tooltipSpec,
                            myTooltipManager.measure(
                                    getSortedText(tooltipSpec),
                                    getFontSize(tooltipSpec)
                            )
                    )
            )
        }

        return measuredTooltips
    }

    fun showTooltip(cursor: DoubleVector, tooltipSpecs: List<TooltipSpec>) {
        drawTooltips(
                myLayoutManager.arrange(
                        measuredTooltips(tooltipSpecs),
                        cursor
                )
        )
    }

    fun hideTooltip() {
        drawTooltips(emptyList())
    }

    companion object {

        fun convertToTooltipEntry(arrangeDataList: List<PositionedTooltip>): List<TooltipEntry> {
            val layoutDataList = ArrayList<TooltipEntry>()
            for (positionedTooltip in arrangeDataList) {

                val tooltipSpec = positionedTooltip.tooltipSpec
                val content = TooltipContent(
                        getSortedText(tooltipSpec),
                        tooltipSpec.fill,
                        getFontSize(tooltipSpec)
                )

                layoutDataList.add(
                        TooltipEntry(
                                content,
                                positionedTooltip.tooltipCoord,
                                positionedTooltip.stemCoord!!,
                                getOrientation(tooltipSpec.layoutHint.kind)
                        )
                )
            }
            return layoutDataList
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
