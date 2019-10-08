package jetbrains.datalore.plot.builder.theme

import jetbrains.datalore.plot.builder.presentation.Defaults.Plot

class DefaultAxisTheme : AxisTheme {
    override fun showLine(): Boolean {
        return true
    }

    override fun showTickMarks(): Boolean {
        return true
    }

    override fun showTickLabels(): Boolean {
        return true
    }

    override fun showTitle(): Boolean {
        return true
    }

    override fun lineWidth(): Double {
        return Plot.Axis.LINE_WIDTH
    }

    override fun tickMarkWidth(): Double {
        return Plot.Axis.TICK_LINE_WIDTH
    }

    override fun tickMarkLength(): Double {
        return 6.0
    }

    override fun tickMarkPadding(): Double {
        return 3.0
    }
}
