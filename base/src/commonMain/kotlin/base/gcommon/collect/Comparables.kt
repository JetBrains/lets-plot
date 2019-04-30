package jetbrains.datalore.base.gcommon.collect


object Comparables {
    fun <T : Comparable<T>> min(a: T, b: T): T {
        return if (a < b) a else b
    }

    fun <T : Comparable<T>> max(a: T, b: T): T {
        return if (a >= b) a else b
    }

    fun <T : Comparable<T>> lse(a: T, b: T): Boolean {
        return a <= b
    }

    fun <T : Comparable<T>> gte(a: T, b: T): Boolean {
        return a >= b
    }

    fun <T : Comparable<T>> ls(a: T, b: T): Boolean {
        return a < b
    }

    fun <T : Comparable<T>> gt(a: T, b: T): Boolean {
        return a > b
    }
}
