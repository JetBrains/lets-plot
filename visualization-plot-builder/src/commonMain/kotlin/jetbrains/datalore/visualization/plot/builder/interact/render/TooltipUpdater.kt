package jetbrains.datalore.visualization.plot.builder.interact.render

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.plot.builder.tooltip.TooltipOrientation
import jetbrains.datalore.visualization.plot.builder.tooltip.TooltipWithStem

internal class TooltipUpdater(private val tooltipLayer: SvgGElement) {

    private val myTooltipEntries = HashSet<TooltipEntry>()
    private val myAddedTooltips = HashMap<TooltipEntry, TooltipWithStem>()

    fun updateTooltips(tooltipEntries: Collection<TooltipEntry>) {
        myTooltipEntries.clear()
        myTooltipEntries.addAll(tooltipEntries)
        doUpdateTooltips()
    }

    private fun doUpdateTooltips() {
        val freeTooltips = ArrayList(myAddedTooltips.values)
        myAddedTooltips.clear()
        balanceFreeTooltips(freeTooltips)

        if (freeTooltips.size != myTooltipEntries.size) {
            throw IllegalStateException("freeTooltips and updatingTooltips lists should be equal")
        }

        for (entry in myTooltipEntries) {
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
        var tooltipsCountDiff = freeTooltips.size - myTooltipEntries.size
        if (tooltipsCountDiff < 0) {
            while (tooltipsCountDiff++ < 0) {
                val tooltipWithStem = TooltipWithStem()
                freeTooltips.add(tooltipWithStem)
                tooltipLayer.children().add(tooltipWithStem.rootGroup)
            }
        } else if (tooltipsCountDiff > 0) {
            while (tooltipsCountDiff-- > 0) {
                tooltipLayer.children().remove(freeTooltips[0].rootGroup)
                freeTooltips.removeAt(0)
            }
        }
    }

    companion object {
        val IGNORED_COLOR = Color.BLACK
    }

}
