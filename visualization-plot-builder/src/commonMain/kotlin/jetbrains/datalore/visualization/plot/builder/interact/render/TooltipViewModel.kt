package jetbrains.datalore.visualization.plot.builder.interact.render

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.builder.tooltip.TooltipOrientation

internal data class TooltipViewModel(
    val text: List<String>,
    val fill: Color,
    val fontSize: Double,
    val tooltipCoord: DoubleVector,
    val stemCoord: DoubleVector,
    val orientation: TooltipOrientation
)