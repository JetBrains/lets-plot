package jetbrains.datalore.base.typedKey

interface TypedKeyContainer {
    operator fun <T> get(key: TypedKey<T>): T
    fun <T> put(key: TypedKey<T>, value: T): T
    operator fun contains(key: TypedKey<*>): Boolean
}
