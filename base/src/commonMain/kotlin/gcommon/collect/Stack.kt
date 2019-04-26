package jetbrains.datalore.base.gcommon.collect

class Stack<T> {
    private val elements: MutableList<T> = mutableListOf()

    fun empty() = elements.isEmpty()

    fun push(item: T) = elements.add(item)

    fun pop(): T? = if (elements.isEmpty()) null else elements.removeAt(elements.size - 1)

    fun peek(): T? = elements.lastOrNull()
}
