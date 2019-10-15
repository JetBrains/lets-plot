package jetbrains.livemap.containers

fun <T> singletonCollection(value: T): Collection<T> = SingletonCollection(value)

class SingletonCollection<T>(private val item: T) : AbstractCollection<T>() {
    override val size get() = 1

    override fun iterator(): Iterator<T> = SingleItemIterator(item)

    private class SingleItemIterator<T>(private val value: T) : AbstractIterator<T>() {
        private var requested = false
        override fun computeNext() {
            when (requested) {
                false -> setNext(value)
                true -> done()
            }

            requested = true
        }
    }
}
