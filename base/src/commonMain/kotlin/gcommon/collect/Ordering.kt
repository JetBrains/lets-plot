package jetbrains.datalore.base.gcommon.collect

abstract class Ordering<T> : Comparator<T> {

    fun isOrdered(iterable: Iterable<T>): Boolean {
        val it = iterable.iterator()
        if (it.hasNext()) {
            var prev: T = it.next()
            while (it.hasNext()) {
                val next = it.next()
                if (compare(prev, next) > 0) {
                    return false
                }
                prev = next
            }
        }
        return true
    }

    /**
     * @return immutable sorted list
     */
    fun <E : T> sortedCopy(elements: Iterable<E>): List<E> {
        @Suppress("UNCHECKED_CAST")
        val array = Iterables.toArray(elements) as Array<E>
        array.sortWith(object : Comparator<E> {
            override fun compare(a: E, b: E): Int {
                return this@Ordering.compare(a, b)
            }
        })
        return array.toList()
    }

    fun reverse(): Ordering<T> {
        return ComparatorOrdering(reversed())
    }

    fun <E : T> min(a: E, b: E): E {
        return if (compare(a, b) <= 0) a else b
    }

    fun <E : T> min(iterable: Iterable<E>): E {
        return min(iterable.iterator())
    }

    fun <E : T> min(iterator: Iterator<E>): E {
        // input must not be empty
        var result = iterator.next()
        while (iterator.hasNext()) {
            result = min(result, iterator.next())
        }

        return result
    }


    fun <E : T> max(a: E, b: E): E {
        return if (compare(a, b) >= 0) a else b
    }

    fun <E : T> max(iterable: Iterable<E>): E {
        return max(iterable.iterator())
    }

    fun <E : T> max(iterator: Iterator<E>): E {
        // input must not be empty
        var result = iterator.next()
        while (iterator.hasNext()) {
            result = max(result, iterator.next())
        }

        return result
    }

    companion object {

        fun <T> from(comparator: Comparator<T>): Ordering<T> {
            return if (comparator is Ordering<*>)
                comparator as Ordering<T>
            else
                ComparatorOrdering(comparator)
        }

        fun <T : Comparable<T>> natural(): Ordering<T> {
            return ComparatorOrdering(naturalOrder())
        }
    }

}
