/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.datetime

import org.jetbrains.letsPlot.commons.intern.datetime.Instant
import org.jetbrains.letsPlot.commons.intern.datetime.tz.TimeZone

object DateTimeFormatUtil {
    fun formatDateUTC(instant: Number, pattern: String): String {
        val format = DateTimeFormat(pattern)
        return instant.toLong()
            .let(::Instant)
            .let(TimeZone.UTC::toDateTime)
            .let(format::apply)
    }

    fun formatterDateUTC(pattern: String): (Number) -> String = { input ->
        formatDateUTC(input, pattern)
    }
}
