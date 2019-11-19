/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.text

import jetbrains.datalore.base.dateFormat.Format
import jetbrains.datalore.base.datetime.Instant
import jetbrains.datalore.base.datetime.tz.TimeZone

object DateTimeFormatUtil {
    fun formatDateUTC(instant: Number, pattern: String): String {
        val format = Format(pattern)
        return instant.toLong()
            .let(::Instant)
            .let(TimeZone.UTC::toDateTime)
            .let(format::apply)
    }
}
