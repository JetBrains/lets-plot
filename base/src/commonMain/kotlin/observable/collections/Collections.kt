package observable.collections

object Collections {
    fun <T> unmodifiableCollection(collection: Collection<T>): Collection<T> {
        return ImmutableCollection(collection)
    }

    fun <T> unmodifiableList(list: List<T>): List<T> {
        return ImmutableList(list)
    }

    fun <T> unmodifiableSet(set: Set<T>): Set<T> {
        return ImmutableSet(set)
    }

    fun <K, V> unmodifiableMap(map: Map<K, V>): Map<K, V> {
        return ImmutableMap(map)
    }

    inline fun <reified T> arrayCopy(source: Array<out T>) = Array(source.size) { i -> source[i] }
}

private class ImmutableCollection<T>(private val inner: Collection<T>) : Collection<T> by inner
private class ImmutableList<T>(private val inner: List<T>) : List<T> by inner
private class ImmutableSet<T>(private val inner: Set<T>) : Set<T> by inner
private class ImmutableMap<K, V>(private val inner: Map<K, V>) : Map<K, V> by inner
