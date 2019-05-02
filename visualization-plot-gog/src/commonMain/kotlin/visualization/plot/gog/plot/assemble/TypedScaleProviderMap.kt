package jetbrains.datalore.visualization.plot.gog.plot.assemble

import jetbrains.datalore.base.observable.collections.Collections
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.plot.scale.ScaleProvider

class TypedScaleProviderMap(map: Map<Aes<*>, ScaleProvider<*>>) {
    private var myMap: Map<Aes<*>, ScaleProvider<*>> = map

    operator fun <T> get(aes: Aes<T>): ScaleProvider<T> {
        return myMap[aes] as ScaleProvider<T>
    }

    fun containsKey(aes: Aes<*>): Boolean {
        return myMap.containsKey(aes)
    }

    fun unmodifiableCopy(): TypedScaleProviderMap {
        return TypedScaleProviderMap(Collections.unmodifiableMap(myMap))
    }

    fun keySet(): Set<Aes<*>> {
        return myMap.keys
    }
}
