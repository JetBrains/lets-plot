package jetbrains.datalore.base.observable.collections.set


open class UnmodifiableSet<ElementT>(protected open val wrappedSet: Set<ElementT>) : AbstractMutableSet<ElementT>() {
    override val size: Int
        get() = wrappedSet.size

    override fun iterator(): MutableIterator<ElementT> {
        val it = wrappedSet.iterator()
        return object : MutableIterator<ElementT> {
            override fun hasNext(): Boolean {
                return it.hasNext()
            }

            override fun next(): ElementT {
                return it.next()
            }

            override fun remove() {
                throw UnsupportedOperationException()
            }
        }
    }

    override operator fun contains(element: ElementT): Boolean {
        return wrappedSet.contains(element)
    }

//    public override fun toArray(): Array<Any> {
//        return wrappedSet.toTypedArray()
//    }

//    public override fun <T> toArray(a: Array<T>): Array<T> {
//        return wrappedSet.toTypedArray<T>()
//    }

    override fun add(element: ElementT): Boolean {
        throw UnsupportedOperationException()
    }

    override fun remove(element: ElementT): Boolean {
        throw UnsupportedOperationException()
    }

    override fun addAll(elements: Collection<ElementT>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun retainAll(elements: Collection<ElementT>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun removeAll(elements: Collection<ElementT>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun clear() {
        throw UnsupportedOperationException()
    }

    override fun equals(other: Any?): Boolean {
        return wrappedSet == other
    }

    override fun hashCode(): Int {
        return wrappedSet.hashCode()
    }
}