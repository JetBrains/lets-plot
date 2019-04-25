package jetbrains.datalore.visualization.plot.gog.core.aes

import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import java.util.*
import java.util.function.Function

internal class TypedIndexFunctionMap {
    private var myMap: MutableMap<Aes<*>, (Int) -> Any?> = HashMap()

    operator fun <T> get(aes: Aes<T>): (Int) -> T {
        // Safe cast if 'put' is used responsibly.
        return myMap[aes] as ((Int) -> T)
    }

    fun <T> put(aes: Aes<T>, value: (Int) -> T): (Int) -> T {
        // Used responsibly, package-private
        return myMap.put(aes, value) as ((Int) -> T)
    }

    fun keySet(): Set<Aes<*>> {
        return myMap.keys
    }

    fun unmodifiableCopy(): TypedIndexFunctionMap {
        val copy = TypedIndexFunctionMap()
        copy.myMap = Collections.unmodifiableMap(HashMap(myMap))
        return copy
    }
}
