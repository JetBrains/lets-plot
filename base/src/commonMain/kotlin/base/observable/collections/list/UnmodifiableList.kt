package jetbrains.datalore.base.observable.collections.list


open class UnmodifiableList<ElementT>(protected open val wrappedList: List<ElementT>) : AbstractMutableList<ElementT>() {

    override val size: Int
        get() = wrappedList.size

    override fun get(index: Int): ElementT {
        return wrappedList[index]
    }


    override fun add(element: ElementT): Boolean {
        throw UnsupportedOperationException()
    }

    override fun add(index: Int, element: ElementT) {
        throw UnsupportedOperationException()
    }

    override fun removeAt(index: Int): ElementT {
        throw UnsupportedOperationException()
    }

    override fun clear() {
        throw UnsupportedOperationException()
    }

    override fun addAll(index: Int, elements: Collection<ElementT>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun addAll(elements: Collection<ElementT>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun remove(element: ElementT): Boolean {
        throw UnsupportedOperationException()
    }

    override fun removeAll(elements: Collection<ElementT>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun retainAll(elements: Collection<ElementT>): Boolean {
        throw UnsupportedOperationException()
    }

    override operator fun set(index: Int, element: ElementT): ElementT {
        throw UnsupportedOperationException()
    }
}