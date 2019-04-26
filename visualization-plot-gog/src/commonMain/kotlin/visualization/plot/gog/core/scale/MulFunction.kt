package jetbrains.datalore.visualization.plot.gog.core.scale

import jetbrains.datalore.base.function.Function

internal class MulFunction(private val myFactor: Double) : Function<Double, Double> {
    override fun apply(value: Double): Double {
        return value * myFactor
    }
}
