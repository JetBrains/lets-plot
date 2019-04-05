package jetbrains.datalore.base.function

interface Predicate<ValueT> {
    fun test(value: ValueT): Boolean
}