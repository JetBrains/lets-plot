package jetbrains.datalore.visualization.plot.gog.core.event3

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color

// `open` - for Mockito tests
open class TipLayoutHint internal constructor(
        open val kind: Kind,
        open val coord: DoubleVector?,
        open val objectRadius: Double,
        open val color: Color) {

    enum class Kind {
        VERTICAL_TOOLTIP,
        HORIZONTAL_TOOLTIP,
        CURSOR_TOOLTIP,
        X_AXIS_TOOLTIP,
        Y_AXIS_TOOLTIP
    }

    companion object {

        fun verticalTooltip(coord: DoubleVector?, objectRadius: Double, color: Color): TipLayoutHint {
            return TipLayoutHint(Kind.VERTICAL_TOOLTIP, coord, objectRadius, color)
        }

        fun horizontalTooltip(coord: DoubleVector?, objectRadius: Double, color: Color): TipLayoutHint {
            return TipLayoutHint(Kind.HORIZONTAL_TOOLTIP, coord, objectRadius, color)
        }

        fun cursorTooltip(coord: DoubleVector?, color: Color): TipLayoutHint {
            return TipLayoutHint(Kind.CURSOR_TOOLTIP, coord, 0.0, color)
        }

        fun xAxisTooltip(coord: DoubleVector?, color: Color): TipLayoutHint {
            return TipLayoutHint(Kind.X_AXIS_TOOLTIP, coord, 0.0, color)
        }

        fun yAxisTooltip(coord: DoubleVector?, color: Color): TipLayoutHint {
            return TipLayoutHint(Kind.Y_AXIS_TOOLTIP, coord, 0.0, color)
        }
    }
}
