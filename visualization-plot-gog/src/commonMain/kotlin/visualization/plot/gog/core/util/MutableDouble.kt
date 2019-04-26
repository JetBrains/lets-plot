package jetbrains.datalore.visualization.plot.gog.core.util

class MutableDouble(private var myValue: Double) {

    fun getAndAdd(v: Double): Double {
        val prevValue = myValue
        myValue = prevValue + v
        return prevValue
    }

    fun get(): Double {
        return myValue
    }
}
