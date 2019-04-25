package jetbrains.datalore.base.gcommon.collect

import jetbrains.datalore.base.function.Predicate

expect object Iterables {
    fun <T> toList(iterable: Iterable<T>): List<T>

    fun isEmpty(iterable: Iterable<*>): Boolean

    fun <T> filter(unfiltered: Iterable<T>, retainIfTrue: Predicate<in T>): Iterable<T>

    fun <T> all(iterable: Iterable<T>, predicate: Predicate<in T>): Boolean

    fun <T> concat(a: Iterable<T>, b: Iterable<T>): Iterable<T>

    operator fun <T> get(iterable: Iterable<T>, position: Int): T

    operator fun <T> get(iterable: Iterable<T>, position: Int, defaultValue: T): T

    fun <T> find(iterable: Iterable<T>, predicate: Predicate<in T>, defaultValue: T): T

    fun <T> getLast(iterable: Iterable<T>): T

    internal fun toArray(iterable: Iterable<*>): Array<*>
}