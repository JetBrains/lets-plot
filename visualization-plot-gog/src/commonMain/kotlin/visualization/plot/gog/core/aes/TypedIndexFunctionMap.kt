package jetbrains.datalore.visualization.plot.gog.core.aes

import jetbrains.datalore.visualization.plot.gog.core.render.Aes

internal class TypedIndexFunctionMap(indexFunctionMap: MutableMap<Aes<*>, (Int) -> Any?>) {
    private var myMap: Map<Aes<*>, (Int) -> Any?> = indexFunctionMap

    operator fun <T> get(aes: Aes<T>): (Int) -> T {
        // Safe cast if 'put' is used responsibly.
        return myMap[aes] as ((Int) -> T)
    }
}
