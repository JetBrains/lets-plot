package jetbrains.datalore.visualization.plot.builder.interact.render

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.plot.builder.presentation.Style
import jetbrains.datalore.visualization.plot.builder.tooltip.TooltipWithStem

internal class TooltipUpdater(private val tooltipLayer: SvgGElement) {

    private val viewModels = HashSet<TooltipViewModel>()
    private val views = HashMap<TooltipViewModel, TooltipWithStem>()

    fun updateTooltips(tooltipEntries: Collection<TooltipViewModel>) {
        viewModels.clear()
        viewModels.addAll(tooltipEntries)

        views.values
            .forEach { view -> tooltipLayer.children().remove(view.rootGroup) }
            .also { views.clear() }

        viewModels.forEach { vm ->
            views[vm] = TooltipWithStem().apply {
                tooltipLayer.children().add(rootGroup) // have to be in DOM to calculate bbox on next line
                with(vm) {
                    update(fill, text, fontSize)
                    moveTooltipTo(tooltipCoord, stemCoord, orientation(this))
                    addClassName(Style.PLOT_TOOLTIP)
                    addClassName("shown")
                }
            }
        }
    }

    private fun orientation(entry: TooltipViewModel): TooltipWithStem.Orientation =
        when {
            entry.orientation === TooltipOrientation.HORIZONTAL -> TooltipWithStem.Orientation.HORIZONTAL
            else -> TooltipWithStem.Orientation.VERTICAL
        }

    companion object {
        val IGNORED_COLOR = Color.BLACK
    }
}
