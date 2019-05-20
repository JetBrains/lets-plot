package jetbrains.datalore.visualization.plot.common.text

expect object DateTimeFormatUtil {
    fun formatDateUTC(instant: Number, pattern: String): String
}
