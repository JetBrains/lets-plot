package jetbrains.datalore.visualization.plot.gog.common.text

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round

actual object DateTimeFormatUtil {

    actual fun formatDateUTC(instant: Number, pattern: String): String {
//              instant.toLong()
        val date = Date(round(instant.toDouble()).toLong())
        return formatDateUTC(date, pattern)
    }

    private fun formatDateUTC(date: Date, pattern: String): String {
        val formatter = SimpleDateFormat(pattern)
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(date)
    }

    fun formatDate(date: Date, pattern: String): String {
        return SimpleDateFormat(pattern).format(date)
    }
}
