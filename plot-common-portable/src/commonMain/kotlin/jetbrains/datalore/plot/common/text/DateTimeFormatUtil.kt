package jetbrains.datalore.plot.common.text

expect object DateTimeFormatUtil {
    fun formatDateUTC(instant: Number, pattern: String): String
}
