/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.render

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind.*
import jetbrains.datalore.plot.builder.interact.TooltipSpec
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plot.builder.tooltip.CrosshairComponent
import jetbrains.datalore.plot.builder.tooltip.TooltipBox
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.HorizontalAlignment
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.MeasuredTooltip
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgNode

internal class TooltipLayer(
    decorationLayer: SvgNode,
    viewport: DoubleRectangle
) {
    private val myLayoutManager = LayoutManager(viewport, HorizontalAlignment.LEFT)
    private val myTooltipLayer = SvgGElement().also { decorationLayer.children().add(it) }

    fun showTooltips(
        cursor: DoubleVector,
        tooltipSpecs: List<TooltipSpec>,
        geomBounds: DoubleRectangle?,
        geomClipBounds: DoubleRectangle?
    ) {
        clearTooltips()

        tooltipSpecs
            .filter { spec -> spec.lines.isNotEmpty() }
            .also { specs -> geomBounds?.let { showCrosshair(specs, it) } }
            .map { spec -> spec
                .run { newTooltipBox(spec.minWidth).apply { visible = false } } // to not flicker on arrange
                .apply { setContent(spec.fill, spec.lines, spec.style, spec.isOutlier) }
                .run { MeasuredTooltip(tooltipSpec = spec, tooltipBox = this) }
            }
            .run { myLayoutManager.arrange(tooltips = this, cursorCoord = cursor, geomBounds, geomClipBounds) }
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

    private fun newTooltipBox(tooltipMinWidth: Double?): TooltipBox {
        // Add to the layer to be able to calculate a bbox
        return TooltipBox(tooltipMinWidth).apply { myTooltipLayer.children().add(rootGroup) }
    }

    private fun newCrosshairComponent(): CrosshairComponent {
        return CrosshairComponent().apply { myTooltipLayer.children().add(rootGroup) }
    }

    private fun showCrosshair(tooltipSpecs: List<TooltipSpec>, geomBounds: DoubleRectangle) {
        val showVertical = tooltipSpecs.any { it.layoutHint.kind == X_AXIS_TOOLTIP }
        val showHorizontal = tooltipSpecs.any { it.layoutHint.kind == Y_AXIS_TOOLTIP }
        if (!showVertical && !showHorizontal) {
            return
        }
        tooltipSpecs
            .filter(TooltipSpec::isCrosshairEnabled)
            .forEach { tooltipSpec ->
                tooltipSpec.layoutHint.coord?.let { coord ->
                    newCrosshairComponent().also { crosshair ->
                        if (showHorizontal) crosshair.addHorizontal(coord, geomBounds)
                        if (showVertical) crosshair.addVertical(coord, geomBounds)
                    }
                }
            }
    }

    private val TooltipSpec.style get() = when (this.layoutHint.kind) {
        X_AXIS_TOOLTIP, Y_AXIS_TOOLTIP -> Style.PLOT_AXIS_TOOLTIP
        else -> Style.PLOT_DATA_TOOLTIP
    }

    private val LayoutManager.PositionedTooltip.orientation get() = when (this.hintKind) {
        HORIZONTAL_TOOLTIP, Y_AXIS_TOOLTIP -> TooltipBox.Orientation.HORIZONTAL
        else -> TooltipBox.Orientation.VERTICAL
    }
}
