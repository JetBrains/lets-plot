package jetbrains.datalore.visualization.plot.gog.core.scale

internal class MulFunction(private val myFactor: Double) : (Double) -> Double {
    override fun invoke(input: Double): Double {
        return input * myFactor
    }
}
