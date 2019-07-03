package jetbrains.datalore.base.numberFormat

actual object NumberFormatUtil {

    actual fun formatNumber(num: Number, pattern: String): String {
        // ToDo: pattern format
        // ToDo: This code will not pass NumberFormat tests
        return num.toString()
    }
}
