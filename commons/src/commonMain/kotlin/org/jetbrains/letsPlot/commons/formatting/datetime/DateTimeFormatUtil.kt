/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.datetime

import org.jetbrains.letsPlot.commons.intern.datetime.Instant
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone

object DateTimeFormatUtil {
    private fun formatDate(instant: Number, pattern: String, timeZone: TimeZone = TimeZone.UTC): String {
        val format = DateTimeFormat(pattern)
        return instant.toLong()
            .let(::Instant).toDateTime(timeZone)
            .let(format::apply)
    }

    fun formatterDate(pattern: String, timeZone: TimeZone = TimeZone.UTC): (Number) -> String = { input ->
        formatDate(input, pattern, timeZone)
    }

    fun formatDateUTC(instant: Number, pattern: String): String =
        formatDate(instant, pattern, TimeZone.UTC)

    fun formatterDateUTC(pattern: String): (Number) -> String =
        formatterDate(pattern, TimeZone.UTC)
}