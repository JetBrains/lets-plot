/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.commons.formatting.datetime.Pattern.Companion.isDateTimeFormat
import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat
import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.ExponentNotationType
import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.ExponentFormat
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone
import org.jetbrains.letsPlot.core.commons.data.DataType
import org.jetbrains.letsPlot.core.commons.data.DataType.*

object FormatterUtil {

    fun byDataType(dataType: DataType, expFormat: ExponentFormat, tz: TimeZone?): (Any) -> String {
        fun stringFormatter() = StringFormat.forPattern("{}", tz = tz)
        fun numberFormatter() = StringFormat.forPattern("{,~g}", expFormat = expFormat, tz = tz)

        return when (dataType) {
            FLOATING, INTEGER -> numberFormatter()::format
            STRING, BOOLEAN -> stringFormatter()::format
            DATETIME_MILLIS -> StringFormat.forPattern("{%Y-%m-%dT%H:%M:%S}", tz = tz)::format
            DATE_MILLIS -> StringFormat.forPattern("{%Y-%m-%d}", tz = tz)::format
            TIME_MILLIS -> StringFormat.forPattern("{%H:%M:%S}", tz = tz)::format
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

    // pattern example:
    // - string placeholder: "Value: {}"
    // - number placeholder without braces: ",.2f"
    // - number placeholder with braces: "{,.2f}"
    // - datetime placeholder without braces: "%Y-%m-%d"
    // - datetime placeholder with braces: "{%Y-%m-%d}"
    // - string with two or more placeholders: "Value: {,.2f}, Date: {%Y-%m-%d}"
    fun byPattern(
        pattern: String,
        expFormat: ExponentFormat = ExponentFormat(ExponentNotationType.POW),
        tz: TimeZone? = null,
    ): StringFormat {
        // Adjust pattern: for number and datetime formats the pattern should be enclosed in braces.
        // Keep the original pattern if the desired type does not match the detected type - in this case StringFormat will print the pattern as is.
        // E.g., byPattern(",.2f", = DATETIME_FORMAT):
        // format(DateTime.now().toEpochMillis())
        // will output "{,.2f}", not "12345467234,00"
        val pattern = when {
            // contains("{") is important for multiple placeholders with datetime formats
            // isDateTimeFormat("{%Y-%m-%d}x{%H:%M}") returns true because of a loose check for delimiters
            pattern.contains("{") && pattern.contains("}") -> pattern // a string format with multiple placeholders
            NumberFormat.isValidPattern(pattern) -> "{$pattern}" // wrap to braces to make a placeholder
            isDateTimeFormat(pattern) -> "{$pattern}" // wrap to braces to make a placeholder
            else -> pattern // literal string, keep as is
        }

        return StringFormat.forPattern(pattern, expFormat = expFormat, tz = tz)
    }
}
