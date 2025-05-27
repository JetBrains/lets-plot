/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.datetime

import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Instant
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone

object DateTimeFormatUtil {
    // For tests only.
    internal fun format(epochMillis: Number, pattern: String, tz: TimeZone): String {
        val dateTime = Instant(epochMillis.toLong()).toDateTime(tz)
        val format = DateTimeFormat(pattern)
        return format.apply(dateTime)
    }

    // For tests only.
    fun format(dateTime: DateTime, pattern: String): String {
        val format = DateTimeFormat(pattern)
        return format.apply(dateTime)
    }

    fun createInstantFormatter(pattern: String, tz: TimeZone): (Any) -> String {
        val format = DateTimeFormat(pattern)
        return { epochMillis ->
            check(epochMillis is Number) {
                "Expected Unix timestamp in milliseconds (Number), but got '$epochMillis' (${epochMillis::class.simpleName})"
            }
            format(epochMillis, format, tz)
        }
    }

    private fun format(epochMillis: Number, format: DateTimeFormat, tz: TimeZone): String {
        val dateTime = Instant(epochMillis.toLong()).toDateTime(tz)
        return format.apply(dateTime)
    }
}