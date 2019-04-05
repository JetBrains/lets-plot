package jetbrains.datalore.base.function

interface Consumer<ValueT> {
    fun accept(value: ValueT)
}