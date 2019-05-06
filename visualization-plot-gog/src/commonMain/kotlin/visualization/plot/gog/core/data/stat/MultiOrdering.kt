package jetbrains.datalore.visualization.plot.gog.core.data.stat

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument

internal class MultiOrdering<K : Comparable<K>>(private val myKeys: List<K?>) {
    private val myIndices: MutableList<Int>

    init {
        myIndices = ArrayList(myKeys.size)
        for (i in myKeys.indices) {
            myIndices.add(i)
        }

        myIndices.sortWith(Comparator { i: Int?, j: Int? ->
            val keyI = myKeys[i!!]
            val keyJ = myKeys[j!!]
            when {
                keyI === keyJ -> 0
                keyI == null -> -1
                keyJ == null -> 1
                else -> keyI.compareTo(keyJ)
            }
        })
    }

    fun <T> sortedCopy(l: List<T?>): List<T?> {
        checkArgument(l.size == myIndices.size,
                "Expected size " + myIndices.size + " but was size " + l.size)
        val copy = ArrayList<T?>(myIndices.size)
        for (oldIndex in myIndices) {
            val v = l[oldIndex]
            copy.add(v)
        }
        return copy
    }

    fun sortedCopyOfKeys(): List<K?> {
        return sortedCopy(myKeys)
    }
}
