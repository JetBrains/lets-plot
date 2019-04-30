package jetbrains.datalore.visualization.plot.gog.common.text

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round

object DateTimeFormatUtil {
    internal const val YEAR = "MMM y"
    internal const val YEAR_QUARTER = "Q y"
    internal const val YEAR_MONTH = "MMMM y"
    internal const val DATE_MEDIUM = "EEE, MMM d, y"
    internal const val DATE_MEDIUM_TIME_SHORT = "EEE, MMM d, y h:mm a"

    fun formatDateUTC(instant: Number, pattern: String): String {
        val date = Date(round(instant.toDouble()).toLong())
        return formatDateUTC(date, pattern)
    }

    fun formatDateUTC(date: Date, pattern: String): String {
        val formatter = SimpleDateFormat(pattern)
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(date)
    }

    fun formatDate(date: Date, pattern: String): String {
        return SimpleDateFormat(pattern).format(date)
    }
}
