package jetbrains.datalore.visualization.plot.gog.core.util

class MutableInteger(private var myValue: Int) {

    val andIncrement: Int
        get() = getAndAdd(1)

    fun get(): Int {
        return myValue
    }

    fun getAndAdd(v: Int): Int {
        val prevValue = myValue
        myValue = prevValue + v
        return prevValue
    }

    fun increment() {
        getAndAdd(1)
    }
}
