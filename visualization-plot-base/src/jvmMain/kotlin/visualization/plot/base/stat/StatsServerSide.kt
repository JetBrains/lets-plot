package jetbrains.datalore.visualization.plot.base.stat

import jetbrains.datalore.visualization.plot.base.stat.regression.LoessRegression
import jetbrains.datalore.visualization.plot.base.stat.regression.RegressionEvaluator

actual object StatsServerSide {
    actual fun smooth() = SmoothStat()

    actual fun density2d(): Density2dStatShell {
        return Density2dStat()
    }

    actual fun density2df(): Density2dStatShell {
        return Density2dfStat()
    }
}

actual fun loess(valuesX: List<Double?>, valuesY: List<Double?>, confidenceLevel: Double): RegressionEvaluator {
    return LoessRegression(valuesX, valuesY, confidenceLevel)
}
