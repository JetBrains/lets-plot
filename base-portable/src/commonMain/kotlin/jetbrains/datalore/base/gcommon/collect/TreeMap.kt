package jetbrains.datalore.base.gcommon.collect

class TreeMap<K : Comparable<K>, V> {
    val values: Collection<V>
        get() = map.values
    private val sortedKeys: MutableList<K> = ArrayList()
    private val map: MutableMap<K, V> = HashMap()

    operator fun get(key: K): V? = map[key]

    fun put(key: K, value: V): V? {
        val index = sortedKeys.binarySearch(key)
        if (index < 0) {
            sortedKeys.add(index.inv(), key)
        } else {
            sortedKeys[index] = key
        }
        return map.put(key, value)
    }

    fun containsKey(key: K): Boolean = map.containsKey(key)

    fun floorKey(key: K): K? {
        var index = sortedKeys.binarySearch(key)

        if (index < 0) {
            index = index.inv() - 1

            if (index < 0)
                return null
        }
        return sortedKeys[index]
    }

    fun ceilingKey(key: K): K? {
        var index = sortedKeys.binarySearch(key)

        if (index < 0) {
            index = index.inv()

            if (index == sortedKeys.size)
                return null
        }
        return sortedKeys[index]
    }
}
