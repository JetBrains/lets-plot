/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.text

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong

actual object DateTimeFormatUtil {

    actual fun formatDateUTC(instant: Number, pattern: String): String {
        val date = Date(instant.toDouble().roundToLong())
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
