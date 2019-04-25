package jetbrains.datalore.base.gcommon.collect


object Lists {
    fun <F, T> transform(fromList: List<F>, function: (F) -> T): List<T> {
        return fromList.map { t -> function.invoke(t) }
    }

    /**
     * Doesn't return reversed `view` of the list (guava)
     * Instead creates reversed copy.
     */
    fun <T> reverse(list: List<T>): List<T> {
        val copy = ArrayList(list)
        copy.reverse()
        return copy
    }
}
