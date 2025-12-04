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
import org.jetbrains.letsPlot.core.plot.base.FormatterUtil.FormatType.*

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
        type: FormatType? = null,
        expFormat: ExponentFormat = ExponentFormat(ExponentNotationType.POW),
        tz: TimeZone? = null,
    ): StringFormat {
        val formatType = type ?: detectFormatType(pattern)

        // Adjust pattern: for number and datetime formats the pattern should be enclosed in braces.
        // Keep the original pattern if the desired type does not match the detected type - in this case StringFormat will print the pattern as is.
        // E.g., byPattern(",.2f", = DATETIME_FORMAT):
        // format(DateTime.now().toEpochMillis())
        // will output "{,.2f}", not "12345467234,00"
        val pattern = when (formatType) {
            NUMBER_FORMAT -> pattern.takeUnless(NumberFormat::isValidPattern) ?: "{$pattern}"
            DATETIME_FORMAT -> pattern.takeUnless { isDateTimeFormat(it) } ?: "{$pattern}"
            STRING_FORMAT -> pattern
        }

        return StringFormat.forPattern(pattern, expFormat = expFormat, tz = tz)
    }

    private fun detectFormatType(pattern: String): FormatType {
        return when {
            NumberFormat.isValidPattern(pattern) -> NUMBER_FORMAT
            isDateTimeFormat(pattern) -> DATETIME_FORMAT
            else -> STRING_FORMAT
        }
    }

    enum class FormatType {
        NUMBER_FORMAT,
        DATETIME_FORMAT,
        STRING_FORMAT
    }

}
