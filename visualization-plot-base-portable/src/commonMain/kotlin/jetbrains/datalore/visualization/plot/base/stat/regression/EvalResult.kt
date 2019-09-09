package jetbrains.datalore.visualization.plot.base.stat.regression

data class EvalResult(
    val y: Double,    // predicted value
    val ymin: Double, // lower pointwise confidence interval around the mean
    val ymax: Double, // upper pointwise confidence interval around the mean
    val se: Double    // standard error
)
