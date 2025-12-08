/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.string

import org.jetbrains.letsPlot.commons.intern.datetime.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StringFormatTest {

    @Test
    fun check_expected_number_of_arguments() {
        assertEquals(0, StringFormat.of("text").argsNumber)
        assertEquals(0, StringFormat.of("%d.%m.%y %H:%M").argsNumber)
        assertEquals(0, StringFormat.of(".1f").argsNumber)

        assertEquals(1, StringFormat.of("{.1f}").argsNumber)
        assertEquals(1, StringFormat.of("{.1f} test").argsNumber)
        assertEquals(2, StringFormat.of("{.1f} {}").argsNumber)
        assertEquals(3, StringFormat.of("{.1f} {.2f} {.3f}").argsNumber)
        assertEquals(1, StringFormat.of("{%d.%m.%y %H:%M}").argsNumber)
        assertEquals(2, StringFormat.of("at {%H:%M} on {%A}").argsNumber)
    }

    @Test
    fun numeric_format() {
        val fmt = StringFormat.of("{.2f}")
        assertEquals("4.00", fmt.format(4))
    }

    @Test
    fun string_pattern_with_multiple_parameters() {
        val fmt = StringFormat.of("{.1f} x {.2f}")
        assertEquals("1.0 x 2.00", fmt.format(listOf(1, 2)))
    }

    @Test
    fun string_pattern_with_braces() {
        val fmt = StringFormat.of("{.1f} {{text}}")
        assertEquals("4.0 {text}", fmt.format(4))
    }

    @Test
    fun value_inside_braces() {
        val fmt = StringFormat.of("{{{.1f}}}")
        assertEquals("{4.0}", fmt.format(4))
    }

    @Test
    fun braces_in_value_should_not_be_altered() {
        assertEquals("{{hello}}", StringFormat.of("{}").format("{{hello}}"))
    }

    @Test
    fun use_original_value_in_the_string_pattern() {
        val fmt = StringFormat.of("original value = {}")
        assertEquals("original value = 4.2", fmt.format(4.2))

        val formatter = { value: Any -> StringFormat.of("{}").format(value) }
        assertEquals("4", formatter(4))
        assertEquals("4.123", formatter(4.123))
        assertEquals("{.2f}", formatter("{.2f}"))
        assertEquals("value is {}", formatter("value is {}"))
    }

    @Test
    fun static_text_in_format() {
        val fmt = StringFormat.of("static text")
        assertEquals("static text", fmt.format(emptyList()))
    }

    @Test
    fun numeric_format_for_the_string_value_will_be_ignored() {
        val fmt = StringFormat.of("{.1f} x {.2f}")
        assertEquals("A x B", fmt.format(listOf("A", "B")))
    }

    @Test
    fun different_number_of_parameters_in_the_pattern_and_number_of_values_to_format() {
        val fmt = StringFormat.of("{.1f} x {.2f} x {.3f}")
        assertEquals("1.0 x 2.00 x {.3f}", fmt.format(listOf(1, 2)))
    }

    @Test
    fun pattern_without_placeholders() {
        val fmt = StringFormat.of("It is .2f")
        assertEquals("It is .2f", fmt.format(42))
    }

    @Test
    fun wrong_number_of_arguments_in_pattern_for_one_arg() {
        val fmt = StringFormat.of("{.2f} {.2f}")
        assertEquals("3.14 {.2f}", fmt.format(3.14159))
    }

    @Test
    fun wrong_number_of_arguments_in_pattern_for_n_args() {
        val fmt = StringFormat.of("{.2f} {.2f}")
        assertEquals("3.14 2.72", fmt.format(listOf(3.14159, 2.71828, 1.61803)))
    }

    @Test
    fun non_numeric_and_non_string_value_formatted_using_toString() {
        val fmt = StringFormat.of("{.1f}")
        assertEquals("(key, value)", fmt.format("key" to "value"))
    }

    @Test
    fun string_similar_to_a_numeric_format_as_static_text() {
        val formattedString = StringFormat.of(".2f").format(emptyList())
        assertEquals(".2f", formattedString)
    }

    @Test
    fun try_to_format_static_text_as_number_format() {
        val fmt = StringFormat.of("pattern")
        assertEquals("pattern", fmt.format("text"))
    }

    private val dateTimeToFormat = DateTime(
        Date(6, Month.AUGUST, 2019),
        Time(4, 46, 35)
    ).toEpochMilliseconds(TZ)

    @Test
    fun dateTime_format() {
        assertEquals("August", StringFormat.of("{%B}").format(dateTimeToFormat))
        assertEquals("Tuesday", StringFormat.of("{%A}").format(dateTimeToFormat))
        assertEquals("2019", StringFormat.of("{%Y}").format(dateTimeToFormat))
        assertEquals("06.08.19", StringFormat.of("{%d.%m.%y}").format(dateTimeToFormat))
        assertEquals("06.08.19 04:46", StringFormat.of("{%d.%m.%y %H:%M}").format(dateTimeToFormat))
    }

    @Test
    fun string_pattern_with_Number_and_DateTime() {
        val fmt = StringFormat.of("{d}nd day of {%B}")
        assertEquals("2nd day of August", fmt.format(listOf(2, dateTimeToFormat)))
    }

    @Test
    fun use_DateTime_format_in_the_string_pattern() {
        val fmt = StringFormat.of(pattern = "at {%H:%M} on {%A}")
        assertEquals("at 04:46 on Tuesday", fmt.format(listOf(dateTimeToFormat, dateTimeToFormat)))
    }

    @Test
    fun non_dateTime_value_formatted_using_toString() {
        val fmt = StringFormat.of("{%d.%m.%y}")
        assertEquals("01.01.2000", fmt.format("01.01.2000"))
    }

    @Test
    fun try_to_use_undefined_pattern_inside_string_pattern() {
        val exception = assertFailsWith(IllegalArgumentException::class) {
            StringFormat.of("{.1f} x {PP}")
        }

        assertEquals("Can't detect type of pattern 'PP'", exception.message)
    }

    companion object {
        private val TZ = TimeZone.UTC
    }
}