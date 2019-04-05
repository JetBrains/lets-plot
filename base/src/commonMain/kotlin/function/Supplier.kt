package jetbrains.datalore.base.function

interface Supplier<ValueT> {
    fun get(): ValueT
}