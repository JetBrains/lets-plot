package jetbrains.datalore.base.gcommon.collect

object Sets {
    /**
     * Mutable set
     */
    fun <E> newHashSet(elements: Iterable<E>): MutableSet<E> {
        if (elements is Collection<*>) {
            val collection = elements as Collection<E>
            return HashSet(collection)
        }
        return newHashSet(elements.iterator())
    }

    /**
     * Mutable set
     */
    private fun <E> newHashSet(elements: Iterator<E>): MutableSet<E> {
        val set = HashSet<E>()
        while (elements.hasNext()) {
            set.add(elements.next())
        }
        return set
    }

//    /**
//     * Unmodifiable copy
//     */
//    fun <E> difference(set1: Set<E>, set2: Set<E>): Set<E> {
//        val copy = HashSet(set1)
//        copy.removeAll(set2)
//        return copy
//    }
}
