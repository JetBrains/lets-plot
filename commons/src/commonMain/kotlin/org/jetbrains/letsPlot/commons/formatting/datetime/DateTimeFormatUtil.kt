/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.datetime

import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Instant
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone

object DateTimeFormatUtil {
    fun format(epochMillis: Number, pattern: String, tz: TimeZone): String {
        val dateTime = Instant(epochMillis.toLong()).toDateTime(tz)
        return format(dateTime, pattern)
    }

    fun format(dateTime: DateTime, pattern: String): String {
        val format = DateTimeFormat(pattern)
        return format.apply(dateTime)
    }

    fun createInstantFormatter(pattern: String, tz: TimeZone): (Number) -> String = { epochMillis ->
        format(epochMillis, pattern, tz)
    }
}