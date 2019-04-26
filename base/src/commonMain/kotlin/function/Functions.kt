package jetbrains.datalore.base.function

object Functions {

    private val TRUE_PREDICATE: Predicate<Any?> = {
        true
    }

    private val FALSE_PREDICATE: Predicate<Any?> = {
        false
    }

    private val NULL_PREDICATE: Predicate<Any?> = {
        it == null
    }

    private val NOT_NULL_PREDICATE: Predicate<Any?> = {
        it != null
    }

    private val IDENTITY_FUNCTION = fun(value: Any?): Any? {
        return value
    }

    fun <ItemT> constantSupplier(value: ItemT): Supplier<ItemT> {
        return object : Supplier<ItemT> {
            override fun get(): ItemT {
                return value
            }
        }
    }

    fun <V, R> function(func: (V) -> R): Function<V, R> {
        return object : Function<V, R> {
            override fun apply(value: V): R {
                return func.invoke(value)
            }
        }
    }

    fun <ItemT> memorize(supplier: Supplier<ItemT>): Supplier<ItemT> {
        return Memo(supplier)
    }

    fun <ArgT> alwaysTrue(): Predicate<ArgT> {
        return TRUE_PREDICATE as Predicate<ArgT>
    }

    fun <ArgT> alwaysFalse(): Predicate<ArgT> {
        return FALSE_PREDICATE as Predicate<ArgT>
    }

    fun <ArgT, ResultT> constant(result: ResultT): (ArgT) -> ResultT {
        return {
            result
        }
    }

    fun <ArgT> isNull(): Predicate<ArgT> {
        return NULL_PREDICATE
    }

    fun <ArgT> isNotNull(): Predicate<ArgT> {
        return NOT_NULL_PREDICATE
    }

    fun <ValueT> identity(): (Any?) -> Any? {
        return IDENTITY_FUNCTION
    }

    fun <ValueT> same(value: Any?): Predicate<ValueT> {
        return {
            it === value
        }
    }

    private class Memo<ItemT> internal constructor(private val mySupplier: Supplier<ItemT>) : Supplier<ItemT> {
        private var myCachedValue: ItemT? = null
        private var myCached = false

        override fun get(): ItemT {
            if (!myCached) {
                myCachedValue = mySupplier.get()
                myCached = true
            }
            return myCachedValue!!
        }
    }
}