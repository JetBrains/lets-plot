package jetbrains.livemap.core

class LruCache<K, E>(private val limit: Int) {
    val values: List<E>
        get() {
            return linked.toList().map { map[it]!! }
        }

    val map = HashMap<K, E>()
    private val linked = LinkedList<K>()

    operator fun get(key: K): E? {
        val index = linked.indexOf(key)
        linked.remove(index)
        linked.prepend(key)

        return map[key]
    }

    fun put(key: K, value: E) {
        linked.prepend(key)

        if (linked.size > limit) {
            linked.subList(limit, linked.size).forEach {
                linked.removeLast()
                map.remove(it)
            }
        }

        map[key] = value
    }

    fun getOrPut(key: K, defaultValue: () -> E): E {
        val value = get(key)

        return if (value != null) {
            value
        } else {
            val answer = defaultValue()
            put(key, answer)

            answer
        }
    }

    fun containsKey(key: K): Boolean = map.containsKey(key)
}