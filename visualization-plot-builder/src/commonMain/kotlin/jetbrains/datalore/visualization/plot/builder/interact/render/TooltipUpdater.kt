package jetbrains.datalore.visualization.plot.builder.interact.render

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.plot.builder.tooltip.TooltipOrientation
import jetbrains.datalore.visualization.plot.builder.tooltip.TooltipWithStem

internal class TooltipUpdater(private val tooltipLayer: SvgGElement) {

    private val viewModels = HashSet<TooltipViewModel>()
    private val views = HashMap<TooltipViewModel, TooltipWithStem>()

    fun updateTooltips(tooltipEntries: Collection<TooltipViewModel>) {
        viewModels.clear()
        viewModels.addAll(tooltipEntries)

        if (true) {
            views.values
                .forEach { tooltipLayer.children().remove(it.rootGroup) }
                .also { views.clear() }

            println("tooltip update start")
            viewModels.forEach { vm ->
                views[vm] = TooltipWithStem().apply {
                    //rootGroup.visibility().set(HIDDEN) // prevent flickering while changing style properties
                    tooltipLayer.children().add(rootGroup) // have to be in DOM to calculate bbox on next line

                    with(vm) {
                        update(fill, text, fontSize)
                        moveTooltipTo(tooltipCoord, stemCoord, orientation(this))
                    }

                    //rootGroup.visibility().set(VISIBLE) // initialization done, show the tooltip
                    //with (it.tooltipContent) { update(fill, text, fontSize) }
                    //with (it) { moveTooltipTo(tooltipCoord, stemCoord, orientation(this)) }
                }
            }
            println("tooltip update end")

        } else {
            val spareTooltipView = ArrayList(views.values)
            views.clear()
            balanceFreeTooltips(spareTooltipView)

            if (spareTooltipView.size != viewModels.size) {
                throw IllegalStateException("freeTooltips and updatingTooltips lists should be equal")
            }

            println("tooltip update start")
            for (vm in viewModels) {
                views[vm] = spareTooltipView.pop().apply {
                    update(vm.fill, vm.text, vm.fontSize)
                    moveTooltipTo(vm.tooltipCoord, vm.stemCoord, orientation(vm))
                }
            }
            println("tooltip update end")

        }
    }

    private fun orientation(entry: TooltipViewModel): TooltipWithStem.Orientation =
        when {
            entry.orientation === TooltipOrientation.HORIZONTAL -> TooltipWithStem.Orientation.HORIZONTAL
            else -> TooltipWithStem.Orientation.VERTICAL
        }

    private fun <T> MutableList<T>.pop(): T {
        return this.get(0).also { removeAt(0) }
    }

    private fun balanceFreeTooltips(freeTooltips: MutableList<TooltipWithStem>) {
        var tooltipsCountDiff = freeTooltips.size - viewModels.size
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
