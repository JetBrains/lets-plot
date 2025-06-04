/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.ExponentFormat
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone
import org.jetbrains.letsPlot.core.commons.data.DataType
import org.jetbrains.letsPlot.core.commons.data.DataType.*

object FormatterUtil {

    fun byDataType(dataType: DataType, expFormat: ExponentFormat, tz: TimeZone?): (Any) -> String {
        fun stringFormatter() = StringFormat.forOneArg("{}", tz = tz)
        fun numberFormatter() = StringFormat.forOneArg(",~g", expFormat = expFormat, tz = tz)

        return when (dataType) {
            FLOATING, INTEGER -> numberFormatter()::format
            STRING, BOOLEAN -> stringFormatter()::format
            DATETIME_MILLIS -> StringFormat.forOneArg("%Y-%m-%dT%H:%M:%S", tz = tz)::format
            DATE_MILLIS -> StringFormat.forOneArg("%Y-%m-%d", tz = tz)::format
            TIME_MILLIS -> StringFormat.forOneArg("%H:%M:%S", tz = TimeZone.UTC)::format
            UNKNOWN -> {
                // Outside the unknownFormatter to avoid creating of the same formatters multiple times
                val numberFormatter = numberFormatter()
                val nonNumberFormatter = stringFormatter()

                ({ value: Any ->
                    when (value) {
                        is Number -> numberFormatter.format(value)
                        else -> nonNumberFormatter.format(value)
                    }
                })
            }
        }
    }
}
