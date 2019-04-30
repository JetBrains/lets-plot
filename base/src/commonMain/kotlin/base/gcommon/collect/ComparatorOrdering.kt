package jetbrains.datalore.base.gcommon.collect

internal class ComparatorOrdering<T>(comparator: Comparator<T>) : Ordering<T>() {
    private val myComparator: Comparator<T> = comparator

    override fun compare(a: T, b: T): Int {
        return myComparator.compare(a, b)
    }
}
