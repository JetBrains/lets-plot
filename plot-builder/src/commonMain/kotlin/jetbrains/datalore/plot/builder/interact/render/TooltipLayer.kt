package jetbrains.datalore.plot.builder.interact.render

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plot.builder.tooltip.TooltipBox
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.HorizontalAlignment
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.MeasuredTooltip
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.SvgNode
import jetbrains.datalore.visualization.plot.base.interact.TipLayoutHint.Kind.*

internal class TooltipLayer(decorationLayer: SvgNode, viewport: DoubleRectangle) {
    private val myLayoutManager = LayoutManager(viewport, HorizontalAlignment.LEFT)
    private val myTooltipLayer = SvgGElement().also { decorationLayer.children().add(it) }

    fun showTooltips(cursor: DoubleVector, tooltipSpecs: List<jetbrains.datalore.plot.builder.interact.TooltipSpec>) {
        clearTooltips()
        tooltipSpecs
            .filter { spec -> spec.lines.isNotEmpty() }
            .map { spec -> spec
                .run { newTooltipBox().apply { visible = false } } // to not flicker on arrange
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
                }
            }.forEach { it.visible = true } // arranged, show tooltips
    }

    fun hideTooltip() = clearTooltips()

    private fun clearTooltips() = myTooltipLayer.children().clear()

    private fun newTooltipBox(): TooltipBox {
        // Add to the layer to be able to calcualte a bbox
        return TooltipBox().apply { myTooltipLayer.children().add(rootGroup) }
    }

    private val jetbrains.datalore.plot.builder.interact.TooltipSpec.style get() = when (this.layoutHint.kind) {
        X_AXIS_TOOLTIP, Y_AXIS_TOOLTIP -> Style.PLOT_AXIS_TOOLTIP
        else -> Style.PLOT_DATA_TOOLTIP
    }

    private val LayoutManager.PositionedTooltip.orientation get() = when (this.hintKind) {
        HORIZONTAL_TOOLTIP, Y_AXIS_TOOLTIP -> TooltipBox.Orientation.HORIZONTAL
        else -> TooltipBox.Orientation.VERTICAL
    }
}
