package jetbrains.datalore.visualization.plot.gog.plot.assemble

import jetbrains.datalore.visualization.plot.base.render.Aes
import jetbrains.datalore.visualization.plot.gog.plot.scale.ScaleProvider

class TypedScaleProviderMap constructor(map: Map<Aes<*>, ScaleProvider<*>>) {
    private var myMap: Map<Aes<*>, ScaleProvider<*>> = map.toMap()

    operator fun <T> get(aes: Aes<T>): ScaleProvider<T> {
        @Suppress("UNCHECKED_CAST")
        return myMap[aes] as ScaleProvider<T>
    }

    fun containsKey(aes: Aes<*>): Boolean {
        return myMap.containsKey(aes)
    }

    fun keySet(): Set<Aes<*>> {
        return myMap.keys
    }
}
