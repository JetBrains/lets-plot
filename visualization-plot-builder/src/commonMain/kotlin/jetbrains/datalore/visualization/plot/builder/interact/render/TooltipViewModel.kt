package jetbrains.datalore.visualization.plot.builder.interact.render

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color

enum class TooltipOrientation {
    VERTICAL,
    HORIZONTAL,
    ANY
}

internal data class TooltipViewModel(
    val text: List<String>,
    val fill: Color,
    val fontSize: Double,
    val tooltipCoord: DoubleVector,
    val stemCoord: DoubleVector,
    val orientation: TooltipOrientation
)