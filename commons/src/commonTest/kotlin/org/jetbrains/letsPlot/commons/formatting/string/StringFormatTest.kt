/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.string

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.FormatType.*
import org.jetbrains.letsPlot.commons.intern.datetime.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StringFormatTest {

    @Test
    fun check_expected_number_of_arguments() {
        assertEquals(0, createStringFormat("text").argsNumber)
        assertEquals(1, createStringFormat("{.1f}").argsNumber)
        assertEquals(1, createStringFormat("{.1f} test").argsNumber)
        assertEquals(2, createStringFormat("{.1f} {}").argsNumber)
        assertEquals(3, createStringFormat("{.1f} {.2f} {.3f}").argsNumber)
        assertEquals(1, createStringFormat("%d.%m.%y %H:%M", DATETIME_FORMAT).argsNumber)
        assertEquals(2, createStringFormat("at {%H:%M} on {%A}", STRING_FORMAT).argsNumber)
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

        val formatter = { value: Any -> StringFormat.forOneArg("{}", tz = null).format(value) }
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
        val valuesToFormat = listOf(1, 2)

        val exception = assertFailsWith(IllegalStateException::class) {
            createStringFormat(formatPattern).format(valuesToFormat)
        }
        assertEquals(
            "Can't format values [1, 2] with pattern '{.1f} x {.2f} x {.3f}'. Wrong number of arguments: expected 3 instead of 2",
            exception.message
        )
    }

    @Test
    fun wrong_number_of_arguments_in_pattern() {
        assertFailsWith(IllegalArgumentException::class) {
            StringFormat.forOneArg("{.2f} {.2f}", tz = null)
        }.let { exception ->
            assertEquals(
                "Wrong number of arguments in pattern '{.2f} {.2f}' . Expected 1 argument instead of 2",
                exception.message
            )
        }
        assertFailsWith(IllegalArgumentException::class) {
            StringFormat.forNArgs("{.2f} {.2f}", argCount = 3, tz = null)
        }.let { exception ->
            assertEquals(
                "Wrong number of arguments in pattern '{.2f} {.2f}' . Expected 3 arguments instead of 2",
                exception.message
            )
        }
    }

    @Test
    fun try_to_format_non_numeric_and_non_string_value() {
        val formatPattern = "{.1f}"
        val valueToFormat = mapOf(1 to 2)

        val exception = assertFailsWith(IllegalStateException::class) {
            createStringFormat(formatPattern).format(valueToFormat)
        }

        // Actual type in message varies depending on the target platform
        val errorMessage = exception.message
            ?.replace("SingletonMap", "Map")
            ?.replace("HashMap", "Map")

        assertEquals(
            "Failed to format value with type Map. Supported types are Number and String.",
            errorMessage
        )
    }

    @Test
    fun string_similar_to_a_numeric_format_as_static_text() {
        val formattedString = createStringFormat(".2f", type = STRING_FORMAT).format(emptyList())
        assertEquals(".2f", formattedString)
    }

    @Test
    fun try_to_format_static_text_as_number_format() {
        val exception = assertFailsWith(IllegalArgumentException::class) {
            createStringFormat("pattern", type = NUMBER_FORMAT).format("text")
        }
        assertEquals(
            "Wrong number format pattern: 'pattern'",
            exception.message
        )
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
        val formattedString = createStringFormat(formatPattern, STRING_FORMAT).format(valuesToFormat)
        assertEquals("2nd day of August", formattedString)
    }

    @Test
    fun use_DateTime_format_in_the_string_pattern() {
        assertEquals(
            "at 04:46 on Tuesday",
            createStringFormat(
                pattern = "at {%H:%M} on {%A}",
                type = STRING_FORMAT
            ).format(listOf(dateTimeToFormat, dateTimeToFormat))
        )
    }

    @Test
    fun dateTime_format_can_be_used_to_form_the_string_without_braces_in_its_pattern() {
        assertEquals(
            expected = "at 04:46 on Tuesday",
            createStringFormat(
                pattern = "at %H:%M on %A",
                type = DATETIME_FORMAT
            ).format(dateTimeToFormat)
        )
    }

    @Test
    fun number_pattern_as_DateTime_format_will_return_string_with_pattern() {
        assertEquals(
            expected = ".1f",
            createStringFormat(".1f", type = DATETIME_FORMAT).format(dateTimeToFormat)
        )
    }

    @Test
    fun try_to_format_static_text_as_DateTime_format() {
        val exception = assertFailsWith(IllegalStateException::class) {
            createStringFormat("%d.%m.%y").format("01.01.2000")
        }
        assertEquals(
            "Expected Unix timestamp in milliseconds (Number), but got '01.01.2000' (String)",
            exception.message
        )
    }

    @Test
    fun try_to_use_undefined_pattern_inside_string_pattern() {
        val formatPattern = "{.1f} x {PP}"
        val valuesToFormat = listOf(1, 2)

        val exception = assertFailsWith(IllegalStateException::class) {
            createStringFormat(formatPattern).format(valuesToFormat)
        }

        assertEquals(
            "Can't detect type of pattern 'PP' used in string pattern '{.1f} x {PP}'",
            exception.message
        )
    }

    companion object {
        private val TZ = TimeZone.UTC

        private fun createStringFormat(pattern: String): StringFormat {
            return StringFormat.create(pattern, tz = null)
        }

        private fun createStringFormat(pattern: String, type: StringFormat.FormatType): StringFormat {
            return StringFormat.create(pattern, type, tz = null)
        }
    }
}