package jetbrains.datalore.base.numberFormat

object NumberFormatUtil {

    fun formatNumber(num: Number, pattern: String): String {
        val format = NumberFormat(pattern)
        return format.apply(num)
    }
}
