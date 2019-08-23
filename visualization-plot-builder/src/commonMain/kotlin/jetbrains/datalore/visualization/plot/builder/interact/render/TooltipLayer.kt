package jetbrains.datalore.visualization.plot.builder.interact.render

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.SvgNode
import jetbrains.datalore.visualization.plot.base.interact.TipLayoutHint.Kind.*
import jetbrains.datalore.visualization.plot.builder.interact.TooltipSpec
import jetbrains.datalore.visualization.plot.builder.presentation.Style
import jetbrains.datalore.visualization.plot.builder.tooltip.TooltipBox
import jetbrains.datalore.visualization.plot.builder.tooltip.layout.LayoutManager
import jetbrains.datalore.visualization.plot.builder.tooltip.layout.LayoutManager.HorizontalAlignment
import jetbrains.datalore.visualization.plot.builder.tooltip.layout.LayoutManager.MeasuredTooltip

internal class TooltipLayer(decorationLayer: SvgNode, viewport: DoubleRectangle) {
    private val myLayoutManager = LayoutManager(viewport, HorizontalAlignment.LEFT)
    private val myTooltipLayer = SvgGElement().also { decorationLayer.children().add(it) }
    private val myVisibleTooltips = HashSet<TooltipBox>()

    fun showTooltips(cursor: DoubleVector, tooltipSpecs: List<TooltipSpec>) {
        removeVisibleTooltips()
        tooltipSpecs
            .filter { spec -> spec.lines.isNotEmpty() }
            .map { spec -> spec
                .run { newTooltipBox() }
                .apply { setContent(spec.fill, spec.lines, spec.style) }
                .run { MeasuredTooltip(tooltipSpec = spec, tooltipBox = this) }
            }
            .run { myLayoutManager.arrange(tooltips = this, cursorCoord = cursor) }
            .map { arranged ->
                arranged.tooltipBox.apply {
                    setPosition(
                        arranged.tooltipCoord,
                        arranged.stemCoord,
                        arranged.orientation
                    )
                    visible = true
                }
            }.forEach { myVisibleTooltips.add(it) }
    }

    fun hideTooltip() {
        removeVisibleTooltips()
    }

    private fun removeVisibleTooltips() {
        myVisibleTooltips.forEach { myTooltipLayer.children().remove(it.rootGroup) }
        myVisibleTooltips.clear()
    }

    private fun newTooltipBox(): TooltipBox {
        return TooltipBox().apply {
            visible = false

            // Add to the layer to be able to calcualte a bbox
            myTooltipLayer.children().add(rootGroup)
        }
    }

    private val TooltipSpec.style: String
        get() = when (this.layoutHint.kind) {
            X_AXIS_TOOLTIP, Y_AXIS_TOOLTIP -> Style.PLOT_AXIS_TOOLTIP
            else -> Style.PLOT_DATA_TOOLTIP
        }

    private val LayoutManager.PositionedTooltip.orientation: TooltipBox.Orientation
        get() = when (this.hintKind) {
            HORIZONTAL_TOOLTIP, Y_AXIS_TOOLTIP -> TooltipBox.Orientation.HORIZONTAL
            else -> TooltipBox.Orientation.VERTICAL
        }
}
