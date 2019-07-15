package jetbrains.datalore.base.function

interface Function<ValueT, ResultT> {
    fun apply(value: ValueT): ResultT
}