package jetbrains.datalore.base.function

/**
 * Mutable container for ValueT. Used mainly to change values from inside of anonymous class/function
 */
class Value<ValueT>(private var myValue: ValueT) : Supplier<ValueT> {

    override fun get(): ValueT {
        return myValue
    }

    fun set(value: ValueT) {
        myValue = value
    }

    override fun toString(): String {
        return "" + myValue
    }
}