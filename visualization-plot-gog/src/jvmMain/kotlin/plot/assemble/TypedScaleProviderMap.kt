package jetbrains.datalore.visualization.plot.gog.plot.assemble

import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.plot.scale.ScaleProvider
import java.util.Collections
import kotlin.collections.HashMap
import kotlin.collections.MutableMap
import kotlin.collections.Set

class TypedScaleProviderMap {
    private var myMap: MutableMap<Aes<*>, ScaleProvider<*>> = HashMap()

    operator fun <T> get(aes: Aes<T>): ScaleProvider<T> {
        return myMap[aes] as ScaleProvider<T>
    }

    fun <T> put(aes: Aes<T>, value: ScaleProvider<T>): ScaleProvider<T>? {
        return myMap.put(aes, value) as ScaleProvider<T>?
    }

    fun containsKey(aes: Aes<*>): Boolean {
        return myMap.containsKey(aes)
    }

    fun unmodifiableCopy(): TypedScaleProviderMap {
        val copy = TypedScaleProviderMap()
        copy.myMap = Collections.unmodifiableMap(HashMap(myMap))
        return copy
    }

    fun keySet(): Set<Aes<*>> {
        return myMap.keys
    }
}
