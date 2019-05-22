package jetbrains.datalore.visualization.plot.builder.event3

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.SvgGraphicsElement
import jetbrains.datalore.visualization.plot.builder.event3.tooltip.TooltipOrientation
import jetbrains.datalore.visualization.plot.builder.event3.tooltip.TooltipWithStem

class TooltipManagerImpl(private val myTooltipLayer: SvgGElement) : TooltipManager {
    private val myUpdatingTooltips = HashSet<TooltipManager.TooltipEntry>()
    private val myAddedTooltips = HashMap<TooltipManager.TooltipEntry, TooltipWithStem>()
    private val myMeasuringTooltip = TooltipWithStem()

    init {

        myMeasuringTooltip.rootGroup.visibility().set(SvgGraphicsElement.Visibility.HIDDEN)
        myTooltipLayer.children().add(myMeasuringTooltip.rootGroup)
    }

    override fun measure(text: List<String>, fontSize: Double): DoubleVector {
        myMeasuringTooltip.update(IGNORED_COLOR, text, fontSize)
        return myMeasuringTooltip.contentRect.dimension
    }

    override fun add(tooltipEntry: TooltipManager.TooltipEntry) {
        myUpdatingTooltips.add(tooltipEntry)
    }

    override fun beginUpdate() {
        myUpdatingTooltips.clear()
    }

    override fun endUpdate() {
        val freeTooltips = ArrayList(myAddedTooltips.values)
        myAddedTooltips.clear()
        balanceFreeTooltips(freeTooltips)

        if (freeTooltips.size != myUpdatingTooltips.size) {
            throw IllegalStateException("freeTooltips and updatingTooltips lists should be equal")
        }

        for (entry in myUpdatingTooltips) {
            val orientation = if (entry.orientation === TooltipOrientation.HORIZONTAL)
                TooltipWithStem.Orientation.HORIZONTAL
            else
                TooltipWithStem.Orientation.VERTICAL

            val tooltip = pop(freeTooltips)
            val content = entry.tooltipContent
            tooltip.update(content.fill, content.text, content.fontSize)
            tooltip.moveTooltipTo(entry.tooltipCoord, entry.stemCoord, orientation)

            myAddedTooltips[entry] = tooltip
        }
    }

    private fun <T> pop(list: MutableList<T>): T {
        val top = list[0]
        list.removeAt(0)
        return top
    }

    private fun balanceFreeTooltips(freeTooltips: MutableList<TooltipWithStem>) {
        var tooltipsCountDiff = freeTooltips.size - myUpdatingTooltips.size
        if (tooltipsCountDiff < 0) {
            while (tooltipsCountDiff++ < 0) {
                val tooltipWithStem = TooltipWithStem()
                freeTooltips.add(tooltipWithStem)
                myTooltipLayer.children().add(tooltipWithStem.rootGroup)
            }
        } else if (tooltipsCountDiff > 0) {
            while (tooltipsCountDiff-- > 0) {
                myTooltipLayer.children().remove(freeTooltips[0].rootGroup)
                freeTooltips.removeAt(0)
            }
        }
    }

    companion object {
        val IGNORED_COLOR = Color.BLACK
    }

}
