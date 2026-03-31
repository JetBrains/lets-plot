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
    fun numeric_format() {
        val fmt = FormatterUtil.byPattern(".2f")
        assertEquals("4.00", fmt.format(4))
    }

    @Test
    fun numeric_format_in_the_string_pattern() {
        val fmt = FormatterUtil.byPattern("{.2f}")
        assertEquals("4.00", fmt.format(4))
    }

    @Test
    fun string_pattern_with_multiple_parameters() {
        val fmt = FormatterUtil.byPattern("{.1f} x {.2f}")
        assertEquals("1.0 x 2.00", fmt.format(listOf(1, 2)))
    }

    @Test
    fun string_pattern_with_braces() {
        val fmt = FormatterUtil.byPattern("{.1f} {{text}}")
        assertEquals("4.0 {text}", fmt.format(4))
    }

    @Test
    fun value_inside_braces() {
        val fmt = FormatterUtil.byPattern("{{{.1f}}}")
        assertEquals("{4.0}", fmt.format(4))
    }

    @Test
    fun use_original_value_in_the_string_pattern() {
        val fmt = FormatterUtil.byPattern("original value = {}")
        assertEquals("original value = 4.2", fmt.format(4.2))

        val formatter = { value: Any -> FormatterUtil.byPattern("{}", tz = null).format(value) }
        assertEquals("4", formatter(4))
        assertEquals("4.123", formatter(4.123))
        assertEquals("{.2f}", formatter("{.2f}"))
        assertEquals("value is {}", formatter("value is {}"))
    }

    @Test
    fun static_text_in_format() {
        val fmt = FormatterUtil.byPattern("static text")
        assertEquals("static text", fmt.format(emptyList()))
    }

    @Test
    fun numeric_format_for_the_string_value_will_be_ignored() {
        val fmt = FormatterUtil.byPattern("{.1f} x {.2f}")
        assertEquals("A x B", fmt.format(listOf("A", "B")))
    }

    @Test
    fun different_number_of_parameters_in_the_pattern_and_number_of_values_to_format() {
        val fmt = FormatterUtil.byPattern("{.1f} x {.2f} x {.3f}")
        assertEquals("1.0 x 2.00 x {.3f}", fmt.format(listOf(1, 2)))
    }

    @Test
    fun pattern_without_placeholders() {
        val fmt = StringFormat.of("It is .2f", tz = null)
        assertEquals("It is .2f", fmt.format(42))
    }

    @Test
    fun wrong_number_of_arguments_in_pattern_for_one_arg() {
        val fmt = StringFormat.of("{.2f} {.2f}", tz = null)
        assertEquals("3.14 {.2f}", fmt.format(3.14159))
    }

    @Test
    fun wrong_number_of_arguments_in_pattern_for_n_args() {
        val fmt = StringFormat.of("{.2f} {.2f}", tz = null)
        assertEquals("3.14 2.72", fmt.format(listOf(3.14159, 2.71828, 1.61803)))
    }

    @Test
    fun non_numeric_and_non_string_value_formatted_using_toString() {
        val fmt = StringFormat.of("{.1f}", tz = null)
        assertEquals("(key, value)", fmt.format("key" to "value"))
    }

    @Test
    fun parameter_without_placeholder_ignored() {
        val fmt = FormatterUtil.byPattern("pattern")
        assertEquals("pattern", fmt.format("text"))
    }

    private val dateTimeToFormat = DateTime(
        Date(6, Month.AUGUST, 2019),
        Time(4, 46, 35)
    ).toEpochMilliseconds(TZ)

    @Test
    fun dateTime_format() {
        assertEquals("August", FormatterUtil.byPattern("%B").format(dateTimeToFormat))
        assertEquals("Tuesday", FormatterUtil.byPattern("%A").format(dateTimeToFormat))
        assertEquals("2019", FormatterUtil.byPattern("%Y").format(dateTimeToFormat))
        assertEquals("06.08.19", FormatterUtil.byPattern("%d.%m.%y").format(dateTimeToFormat))
        assertEquals("06.08.19 04:46", FormatterUtil.byPattern("%d.%m.%y %H:%M").format(dateTimeToFormat))
    }

    @Test
    fun string_pattern_with_Number_and_DateTime() {
        val fmt = FormatterUtil.byPattern("{d}nd day of {%B}")
        assertEquals("2nd day of August", fmt.format(listOf(2, dateTimeToFormat)))
    }

    @Test
    fun use_DateTime_format_in_the_string_pattern() {
        assertEquals(
            "at 04:46 on Tuesday",
            FormatterUtil.byPattern(
                pattern = "at {%H:%M} on {%A}"
            ).format(listOf(dateTimeToFormat, dateTimeToFormat))
        )
    }

    @Test
    fun dateTime_format_can_be_used_to_form_the_string_without_braces_in_its_pattern() {
        assertEquals(
            expected = "at 04:46 on Tuesday",
            FormatterUtil.byPattern("at %H:%M on %A").format(dateTimeToFormat)
        )
    }

    @Test
    fun non_dateTime_value_formatted_using_toString() {
        val fmt = FormatterUtil.byPattern("{%d.%m.%y}")
        assertEquals("01.01.2000", fmt.format("01.01.2000"))
    }

    @Test
    fun unknown_pattern_returned_as_is() {
        val fmt = StringFormat.of("{.1f} x {PP} x {.2f}")
        assertEquals("4.0 x {PP} x 5.00", fmt.format(listOf(4.0, "foo", 5.0)))
    }

    companion object {
        private val TZ = TimeZone.UTC

    }

    private fun formatDType(value: Any, dataType: DataType): String {
        return FormatterUtil.byDataType(
            dataType,
            ExponentFormat(E),
            tz = null,
        ).invoke(value)
    }
}
