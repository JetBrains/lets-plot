package jetbrains.datalore.visualization.plot.base.stat

import jetbrains.datalore.visualization.plot.base.stat.regression.RegressionEvaluator

actual object StatsServerSide {
    actual fun smooth() = SmoothStat()

    actual fun density2d(): Density2dStatShell {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun density2df(): Density2dStatShell {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

actual fun loess(valuesX: List<Double?>, valuesY: List<Double?>, confidenceLevel: Double): RegressionEvaluator {
    TODO("not implemented")
}