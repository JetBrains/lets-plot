package jetbrains.datalore.base.gcommon.collect

import jetbrains.datalore.base.function.Predicate
import java.util.stream.Stream
import java.util.stream.StreamSupport

actual object Iterables {
    private fun <T> toStream(iterable: Iterable<T>): Stream<T> {
        return StreamSupport.stream(iterable.spliterator(), false)
    }

    actual fun <T> toList(iterable: Iterable<T>): List<T> {
        val result = ArrayList<T>()
        for (e in iterable) {
            result.add(e)
        }
        return result
    }

    private fun <T> fromStream(streamFactory: () -> Stream<T>): Iterable<T> {
        return object : Iterable<T> {
            override fun iterator(): Iterator<T> {
                // streams are not re-iterable - create a new stream each time.
                return streamFactory().iterator()
            }
        }
    }

    private fun checkNonNegative(position: Int) {
        if (position < 0) {
            throw IndexOutOfBoundsException(position.toString())
        }
    }

    actual fun isEmpty(iterable: Iterable<*>): Boolean {
        return (iterable as? Collection<*>)?.isEmpty() ?: !iterable.iterator().hasNext()
    }

    // ToDo: ues Kotlin `filter`
    actual fun <T> filter(unfiltered: Iterable<T>, retainIfTrue: Predicate<in T>): Iterable<T> {
        return fromStream { toStream(unfiltered).filter(retainIfTrue) }
    }

    actual fun <T> all(iterable: Iterable<T>, predicate: Predicate<in T>): Boolean {
        return toStream(iterable).allMatch(predicate)
    }

    actual fun <T> concat(a: Iterable<T>, b: Iterable<T>): Iterable<T> {
        return fromStream { Stream.concat(toStream(a), toStream(b)) }
    }

    actual operator fun <T> get(iterable: Iterable<T>, position: Int): T {
        checkNonNegative(position)
        if (iterable is List<*>) {
            return (iterable as List<T>)[position]
        }

        val it = iterable.iterator()
        for (i in 0..position) {
            if (i == position) {
                return it.next()
            }
            it.next()
        }
        throw IndexOutOfBoundsException(position.toString())
    }

    actual operator fun <T> get(iterable: Iterable<T>, position: Int, defaultValue: T): T {
        checkNonNegative(position)
        if (iterable is List<*>) {
            val list = iterable as List<T>
            return if (position < list.size) list[position] else defaultValue
        }
        val it = iterable.iterator()
        var i = 0
        while (i <= position && it.hasNext()) {
            if (i == position) {
                return it.next()
            }
            it.next()
            i++
        }
        return defaultValue
    }

    actual fun <T> find(iterable: Iterable<T>, predicate: Predicate<in T>, defaultValue: T): T {
        val first = toStream(iterable).filter(predicate).findFirst()
        return first.orElse(defaultValue)
    }

    actual fun <T> getLast(iterable: Iterable<T>): T {
        if (iterable is List<*>) {
            val list = iterable as List<T>
            if (list.isEmpty()) {
                throw NoSuchElementException()
            }
            return list[list.size - 1]
        }

        var result: T? = null
        for (v in iterable) {
            result = v
        }

        if (result == null) {
            throw NoSuchElementException()
        }
        return result
    }

    internal actual fun toArray(iterable: Iterable<*>): Array<*> {
        if (iterable is Collection) {
            return iterable.toTypedArray()
        }

        return toStream(iterable).toArray()
    }
}