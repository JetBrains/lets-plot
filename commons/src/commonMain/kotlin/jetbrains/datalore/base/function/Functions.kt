/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

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

    fun <ItemT> constantSupplier(value: ItemT): Supplier<ItemT> {
        return object : Supplier<ItemT> {
            override fun get(): ItemT {
                return value
            }
        }
    }

    fun <ItemT> memorize(supplier: Supplier<ItemT>): Supplier<ItemT> {
        return Memo(supplier)
    }

    fun <ArgT> alwaysTrue(): Predicate<ArgT> {
        return TRUE_PREDICATE
    }

    fun <ArgT> alwaysFalse(): Predicate<ArgT> {
        return FALSE_PREDICATE
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

    fun <ValueT> identity(): (ValueT) -> ValueT {
        return { it }
    }

    fun <ValueT> same(value: Any?): Predicate<ValueT> {
        return {
            it === value
        }
    }

    fun <ArgT, ResultT> funcOf(lambda: (ArgT) -> ResultT): Function<ArgT, ResultT> {
        return object : Function<ArgT, ResultT> {
            override fun apply(value: ArgT): ResultT {
                return lambda(value)
            }
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