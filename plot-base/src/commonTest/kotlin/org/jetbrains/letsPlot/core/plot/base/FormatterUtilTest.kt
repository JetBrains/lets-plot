/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.ExponentNotationType.E
import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.ExponentFormat
import org.jetbrains.letsPlot.commons.intern.datetime.*
import org.jetbrains.letsPlot.core.commons.data.DataType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FormatterUtilTest {

    @Test
    fun intDtypeHasSameDefaultFormatAsFloating() {
        assertEquals("1", formatDType(1.0, DataType.INTEGER))
        assertEquals("1.5", formatDType(1.5, DataType.INTEGER))
        assertEquals("2,025", formatDType(2025, DataType.INTEGER))
        assertEquals("1e+30", formatDType(1e30, DataType.INTEGER))
    }

    @Test
    fun floatingDtype() {
        assertEquals("1", formatDType(1, DataType.FLOATING))
        assertEquals("1", formatDType(1.0, DataType.FLOATING))
        assertEquals("1.5", formatDType(1.5, DataType.FLOATING))
        assertEquals("1e+30", formatDType(1e30, DataType.FLOATING))
    }

    @Test
    fun unknownDtype() {
        // ints with floating format
        assertEquals("1", formatDType(1, DataType.UNKNOWN))
        assertEquals("1e+9", formatDType(1_000_000_000, DataType.UNKNOWN))

        // floats
        assertEquals("1", formatDType(1.0, DataType.UNKNOWN))
        assertEquals("1.5", formatDType(1.5, DataType.UNKNOWN))
        assertEquals("1e+30", formatDType(1e30, DataType.UNKNOWN))

        // strings
        assertEquals("asd", formatDType("asd", DataType.UNKNOWN))
    }

    @Test
    fun check_expected_number_of_arguments() {
        assertEquals(0, createStringFormat("text").argsNumber)

        assertEquals(1, createStringFormat(".1f").argsNumber)
        assertEquals(1, createStringFormat("%d.%m.%y %H:%M").argsNumber)

        assertEquals(1, createStringFormat("{.1f}").argsNumber)
        assertEquals(1, createStringFormat("{.1f} test").argsNumber)
        assertEquals(2, createStringFormat("{.1f} {}").argsNumber)
        assertEquals(3, createStringFormat("{.1f} {.2f} {.3f}").argsNumber)
        assertEquals(1, createStringFormat("{%d.%m.%y %H:%M}").argsNumber)
        assertEquals(2, createStringFormat("at {%H:%M} on {%A}").argsNumber)
    }

    @Test
    fun numeric_format() {
        val formatPattern = ".2f"
        val valueToFormat = 4
        val formattedString = createStringFormat(formatPattern).format(valueToFormat)
        assertEquals("4.00", formattedString)
    }

    @Test
    fun numeric_format_in_the_string_pattern() {
        val formatPattern = "{.2f}"
        val valueToFormat = 4
        val formattedString = createStringFormat(formatPattern).format(valueToFormat)
        assertEquals("4.00", formattedString)
    }

    @Test
    fun string_pattern_with_multiple_parameters() {
        val formatPattern = "{.1f} x {.2f}"
        val valuesToFormat = listOf(1, 2)
        val formattedString = createStringFormat(formatPattern).format(valuesToFormat)
        assertEquals("1.0 x 2.00", formattedString)
    }

    @Test
    fun string_pattern_with_braces() {
        val formatPattern = "{.1f} {{text}}"
        val valueToFormat = 4
        val formattedString = createStringFormat(formatPattern).format(valueToFormat)
        assertEquals("4.0 {text}", formattedString)
    }

    @Test
    fun value_inside_braces() {
        val formatPattern = "{{{.1f}}}"
        val valueToFormat = 4
        val formattedString = createStringFormat(formatPattern).format(valueToFormat)
        assertEquals("{4.0}", formattedString)
    }

    @Test
    fun use_original_value_in_the_string_pattern() {
        val formatPattern = "original value = {}"
        val valueToFormat = 4.2
        val formattedString = createStringFormat(formatPattern).format(valueToFormat)
        assertEquals("original value = 4.2", formattedString)

        val formatter = { value: Any -> FormatterUtil.byPattern("{}", tz = null).format(value) }
        assertEquals("4", formatter(4))
        assertEquals("4.123", formatter(4.123))
        assertEquals("{.2f}", formatter("{.2f}"))
        assertEquals("value is {}", formatter("value is {}"))
    }

    @Test
    fun static_text_in_format() {
        val formatPattern = "static text"
        val formattedString = createStringFormat(formatPattern).format(emptyList())
        assertEquals("static text", formattedString)
    }

    @Test
    fun numeric_format_for_the_string_value_will_be_ignored() {
        val formatPattern = "{.1f} x {.2f}"
        val valuesToFormat = listOf("A", "B")
        val formattedString = createStringFormat(formatPattern).format(valuesToFormat)
        assertEquals("A x B", formattedString)
    }

    @Test
    fun different_number_of_parameters_in_the_pattern_and_number_of_values_to_format() {
        val formatPattern = "{.1f} x {.2f} x {.3f}"
        val fmt = createStringFormat(formatPattern)

        assertEquals("1.0 x 2.00 x {.3f}", fmt.format(listOf(1, 2)))
    }

    @Test
    fun pattern_without_placeholders() {
        val fmt = StringFormat.forPattern("It is .2f", tz = null)
        assertEquals("It is .2f", fmt.format(42))
    }

    @Test
    fun wrong_number_of_arguments_in_pattern_for_one_arg() {
        val fmt = StringFormat.forPattern("{.2f} {.2f}", tz = null)
        assertEquals("3.14 {.2f}", fmt.format(3.14159))
    }

    @Test
    fun wrong_number_of_arguments_in_pattern_for_n_args() {
        val fmt = StringFormat.forPattern("{.2f} {.2f}", tz = null)
        assertEquals("3.14 2.72", fmt.format(listOf(3.14159, 2.71828, 1.61803)))
    }

    @Test
    fun non_numeric_and_non_string_value_formatted_using_toString() {
        val fmt = StringFormat.forPattern("{.1f}", tz = null)
        assertEquals("(key, value)", fmt.format("key" to "value"))
    }

    @Test
    fun try_to_format_static_text_as_number_format() {
        val fmt = createStringFormat("pattern")
        assertEquals("pattern", fmt.format("text"))
    }

    private val dateTimeToFormat = DateTime(
        Date(6, Month.AUGUST, 2019),
        Time(4, 46, 35)
    ).toEpochMilliseconds(TZ)

    @Test
    fun dateTime_format() {
        assertEquals("August", createStringFormat("%B").format(dateTimeToFormat))
        assertEquals("Tuesday", createStringFormat("%A").format(dateTimeToFormat))
        assertEquals("2019", createStringFormat("%Y").format(dateTimeToFormat))
        assertEquals("06.08.19", createStringFormat("%d.%m.%y").format(dateTimeToFormat))
        assertEquals("06.08.19 04:46", createStringFormat("%d.%m.%y %H:%M").format(dateTimeToFormat))
    }

    @Test
    fun string_pattern_with_Number_and_DateTime() {
        val formatPattern = "{d}nd day of {%B}"
        val valuesToFormat = listOf(2, dateTimeToFormat)
        val formattedString = createStringFormat(formatPattern).format(valuesToFormat)
        assertEquals("2nd day of August", formattedString)
    }

    @Test
    fun use_DateTime_format_in_the_string_pattern() {
        assertEquals(
            "at 04:46 on Tuesday",
            createStringFormat(
                pattern = "at {%H:%M} on {%A}"
            ).format(listOf(dateTimeToFormat, dateTimeToFormat))
        )
    }

    @Test
    fun dateTime_format_can_be_used_to_form_the_string_without_braces_in_its_pattern() {
        assertEquals(
            expected = "at 04:46 on Tuesday",
            createStringFormat("at %H:%M on %A").format(dateTimeToFormat)
        )
    }

    @Test
    fun non_dateTime_value_formatted_using_toString() {
        val str = createStringFormat("{%d.%m.%y}").format("01.01.2000")
        assertEquals("01.01.2000", str)
    }

    @Test
    fun try_to_use_undefined_pattern_inside_string_pattern() {
        val formatPattern = "{.1f} x {PP}"
        val valuesToFormat = listOf(1, 2)

        val exception = assertFailsWith(IllegalArgumentException::class) {
            createStringFormat(formatPattern).format(valuesToFormat)
        }

        assertEquals("Can't detect type of pattern 'PP'", exception.message)
    }

    companion object {
        private val TZ = TimeZone.UTC

        private fun createStringFormat(pattern: String): StringFormat {
            return FormatterUtil.byPattern(pattern)
        }
    }

    private fun formatDType(value: Any, dataType: DataType): String {
        return FormatterUtil.byDataType(
            dataType,
            ExponentFormat(E),
            tz = null,
        ).invoke(value)
    }
}
