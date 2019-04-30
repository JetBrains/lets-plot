package jetbrains.datalore.base.typedKey

class TypedKeyHashMap : TypedKeyContainer {

    val map = hashMapOf<TypedKey<*>, Any>()

    override operator fun <T> get(key: TypedKey<T>): T {
        @Suppress("UNCHECKED_CAST")
        return map[key] as T
    }

    operator fun <T> set(key: TypedKey<T>, value: T): T {
        return put(key, value)
    }

    override fun <T> put(key: TypedKey<T>, value: T): T {
        @Suppress("UNCHECKED_CAST")
        return map.put(key, value!!) as T
    }

    override fun contains(key: TypedKey<*>): Boolean {
        return containsKey(key)
    }

    fun <T> containsKey(key: TypedKey<T>): Boolean {
        return map.containsKey(key)
    }

    fun <T> keys(): Set<TypedKey<T>> {
        @Suppress("UNCHECKED_CAST")
        return map.keys as Set<TypedKey<T>>
    }
}
