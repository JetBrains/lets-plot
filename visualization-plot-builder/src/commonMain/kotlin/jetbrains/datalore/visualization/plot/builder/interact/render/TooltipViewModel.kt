package jetbrains.datalore.visualization.plot.builder.interact.render

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.base.interact.TipLayoutHint.Kind
import jetbrains.datalore.visualization.plot.base.interact.TipLayoutHint.Kind.*
import jetbrains.datalore.visualization.plot.builder.interact.render.Orientation.HORIZONTAL
import jetbrains.datalore.visualization.plot.builder.interact.render.Orientation.VERTICAL
import jetbrains.datalore.visualization.plot.builder.presentation.Style.PLOT_AXIS_TOOLTIP
import jetbrains.datalore.visualization.plot.builder.presentation.Style.PLOT_DATA_TOOLTIP

enum class Orientation {
    VERTICAL,
    HORIZONTAL
}

internal data class TooltipViewModel(
    val text: List<String>,
    val fill: Color,
    val style: String,
    val orientation: Orientation,
    val tooltipCoord: DoubleVector,
    val stemCoord: DoubleVector
) {

    companion object {
        fun style(kind: Kind): String {
            return when (kind) {
                X_AXIS_TOOLTIP, Y_AXIS_TOOLTIP -> PLOT_AXIS_TOOLTIP
                else -> PLOT_DATA_TOOLTIP
            }
        }

        fun orientation(kind: Kind): Orientation {
            return when (kind) {
                HORIZONTAL_TOOLTIP, Y_AXIS_TOOLTIP -> HORIZONTAL
                else -> VERTICAL
            }
        }
    }
}
