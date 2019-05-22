package jetbrains.datalore.visualization.plot.base.aes

import jetbrains.datalore.visualization.plot.base.Aes

internal class TypedIndexFunctionMap(indexFunctionMap: Map<Aes<*>, (Int) -> Any?>) {
    private var myMap: Map<Aes<*>, (Int) -> Any?> = indexFunctionMap

    operator fun <T> get(aes: Aes<T>): (Int) -> T {
        // Safe cast if 'put' is used responsibly.
        @Suppress("UNCHECKED_CAST")
        return myMap[aes] as ((Int) -> T)
    }
}
