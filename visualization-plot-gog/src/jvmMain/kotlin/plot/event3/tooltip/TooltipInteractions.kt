package jetbrains.datalore.visualization.plot.gog.plot.event3.tooltip

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.SvgNode
import jetbrains.datalore.visualization.plot.gog.core.event3.TipLayoutHint.Kind
import jetbrains.datalore.visualization.plot.gog.plot.event3.TargetTooltipSpec
import jetbrains.datalore.visualization.plot.gog.plot.event3.TooltipManager
import jetbrains.datalore.visualization.plot.gog.plot.event3.TooltipManager.TooltipContent
import jetbrains.datalore.visualization.plot.gog.plot.event3.TooltipManager.TooltipEntry
import jetbrains.datalore.visualization.plot.gog.plot.event3.TooltipManagerImpl
import jetbrains.datalore.visualization.plot.gog.plot.event3.tooltip.layout.LayoutManager
import jetbrains.datalore.visualization.plot.gog.plot.event3.tooltip.layout.LayoutManager.MeasuredTooltip
import jetbrains.datalore.visualization.plot.gog.plot.event3.tooltip.layout.LayoutManager.PositionedTooltip
import jetbrains.datalore.visualization.plot.gog.plot.presentation.Defaults.Common.Tooltip

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

    private fun measuredTooltips(targetTooltipSpec: TargetTooltipSpec): List<MeasuredTooltip> {
        val measuredTooltips = ArrayList<MeasuredTooltip>()

        for (tooltipSpec in targetTooltipSpec.tooltipSpecs) {
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

    fun showTooltip(cursor: DoubleVector, targetTooltipSpec: TargetTooltipSpec) {
        drawTooltips(
                myLayoutManager.arrange(
                        measuredTooltips(targetTooltipSpec),
                        cursor
                )
        )
    }

    fun hideTooltip() {
        drawTooltips(emptyList<PositionedTooltip>())
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
                                positionedTooltip.stemCoord,
                                getOrientation(tooltipSpec.layoutHint.kind)
                        )
                )
            }
            return layoutDataList
        }

        private fun getOrientation(kind: Kind): TooltipOrientation {
            when (kind) {
                Kind.VERTICAL_TOOLTIP -> return TooltipOrientation.ANY

                Kind.HORIZONTAL_TOOLTIP, Kind.Y_AXIS_TOOLTIP -> return TooltipOrientation.HORIZONTAL

                Kind.CURSOR_TOOLTIP, Kind.X_AXIS_TOOLTIP -> return TooltipOrientation.VERTICAL

                else -> throw IllegalArgumentException("Unknown layout hint kind")
            }
        }

        private fun getFontSize(tooltipSpec: TooltipSpec): Double {
            when (tooltipSpec.layoutHint.kind) {

                Kind.VERTICAL_TOOLTIP, Kind.HORIZONTAL_TOOLTIP, Kind.CURSOR_TOOLTIP -> return Tooltip.FONT_SIZE.toDouble()

                Kind.X_AXIS_TOOLTIP, Kind.Y_AXIS_TOOLTIP -> return Tooltip.AXIS_FONT_SIZE.toDouble()

                else -> throw IllegalArgumentException("Unknown hint kind: " + tooltipSpec.layoutHint.kind)
            }
        }

        private fun getSortedText(spec: TooltipSpec): List<String> {
            val text = ArrayList(spec.lines)
            text.sort()
            return text
        }
    }
}
