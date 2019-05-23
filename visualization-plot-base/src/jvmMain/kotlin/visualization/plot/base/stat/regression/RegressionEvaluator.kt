package jetbrains.datalore.visualization.plot.base.stat.regression

import jetbrains.datalore.base.gcommon.base.Preconditions

abstract class RegressionEvaluator protected constructor(xs: List<Double?>, ys: List<Double?>, confidenceLevel: Double) {
    init {
        Preconditions.checkArgument(
                confidenceLevel in 0.01..0.99,
                "Confidence level is out of range [0.01-0.99]. CL:$confidenceLevel"
        )
        Preconditions.checkArgument(
                xs.size == ys.size,
                "X/Y must have same size. X:" + xs.size + " Y:" + ys.size
        )
    }

    abstract fun evalX(x: Double): EvalResult
}
