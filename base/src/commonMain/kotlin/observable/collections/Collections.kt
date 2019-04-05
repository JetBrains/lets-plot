package observable.collections

object Collections {
    fun <T> unmodifiableList(list: List<T>): List<T> {
        return ImmutableList(list)
    }

    fun <K, V> unmodifiableMap(map: Map<K, V>): Map<K, V> {
        return ImmutableMap(map)
    }

    inline fun <reified T> arrayCopy(source: Array<out T>) = Array(source.size) { i -> source[i] }
}

private class ImmutableList<T>(private val inner: List<T>) : List<T> by inner
private class ImmutableMap<K, V>(private val inner: Map<K, V>) : Map<K, V> by inner
