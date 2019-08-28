package jetbrains.datalore.base.numberFormat


object NumberFormat {
    fun formatNumber(num: Number, pattern: String): String {
        val format = Format(pattern)
        return format.apply(num)
    }
}