package jetbrains.datalore.base.values

// ToDo: use Kotlin Pair
class Pair<FirstT, SecondT>(val first: FirstT?, val second: SecondT?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        val pair = other as Pair<*, *>?

        if (if (first != null) first != pair!!.first else pair!!.first != null) return false
        return !if (second != null) second != pair.second else pair.second != null

    }

    override fun hashCode(): Int {
        var result = first?.hashCode() ?: 0
        result = 31 * result + (second?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "[$first, $second]"
    }
}
