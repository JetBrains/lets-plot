package jetbrains.livemap.containers

class PriorityQueue<T>(private val comparator: Comparator<T>) {
    private val queue: ArrayList<T> = ArrayList()

    fun add(value: T) {
        var index = queue.binarySearch(value, comparator)

        if (index < 0) {
            index = 0
        }

        queue.add(index, value)
    }

    fun peek(): T? = if (queue.isEmpty()) null else queue[0]

    fun clear() = queue.clear()
}