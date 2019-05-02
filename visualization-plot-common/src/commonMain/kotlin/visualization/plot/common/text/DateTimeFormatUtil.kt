package jetbrains.datalore.visualization.plot.gog.common.text

expect object DateTimeFormatUtil {
    fun formatDateUTC(instant: Number, pattern: String): String
}
