package jetbrains.datalore.base.observable.property

/**
 * An object which allows writing to a value stored somewhere
 */
interface WritableProperty<ValueT> {
    fun set(value: ValueT)
}