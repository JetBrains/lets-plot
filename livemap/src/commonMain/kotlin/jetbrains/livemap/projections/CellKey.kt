package jetbrains.livemap.projections

data class CellKey(val key: String) {
    val length: Int
        get() { return key.length }

    override fun toString(): String = key
}