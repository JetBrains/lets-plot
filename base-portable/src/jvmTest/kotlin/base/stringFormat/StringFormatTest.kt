/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.stringFormat

import jetbrains.datalore.base.datetime.Date
import jetbrains.datalore.base.datetime.DateTime
import jetbrains.datalore.base.datetime.Month
import jetbrains.datalore.base.datetime.Time
import jetbrains.datalore.base.datetime.tz.TimeZone
import jetbrains.datalore.base.stringFormat.StringFormat.FormatType.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StringFormatTest {

    @Test
    fun `check expected number of arguments`() {
        assertEquals(0, StringFormat.create("text").argsNumber)
        assertEquals(1, StringFormat.create("{.1f}").argsNumber)
        assertEquals(1, StringFormat.create("{.1f} test").argsNumber)
        assertEquals(2, StringFormat.create("{.1f} {}").argsNumber)
        assertEquals(3, StringFormat.create("{.1f} {.2f} {.3f}").argsNumber)
        assertEquals(1, StringFormat.create("%d.%m.%y %H:%M", DATETIME_FORMAT).argsNumber)
        assertEquals(2, StringFormat.create("at {%H:%M} on {%A}", STRING_FORMAT).argsNumber)
    }

    @Test
    fun `numeric format`() {
        val formatPattern = ".2f"
        val valueToFormat = 4
        val formattedString = StringFormat.create(formatPattern).format(valueToFormat)
        assertEquals("4.00", formattedString)
    }

    @Test
    fun `numeric format in the string pattern`() {
        val formatPattern = "{.2f}"
        val valueToFormat = 4
        val formattedString = StringFormat.create(formatPattern).format(valueToFormat)
        assertEquals("4.00", formattedString)
    }

    @Test
    fun `string pattern with multiple parameters`() {
        val formatPattern = "{.1f} x {.2f}"
        val valuesToFormat = listOf(1, 2)
        val formattedString = StringFormat.create(formatPattern).format(valuesToFormat)
        assertEquals("1.0 x 2.00", formattedString)
    }

    @Test
    fun `string pattern with braces`() {
        val formatPattern = "{.1f} {{text}}"
        val valueToFormat = 4
        val formattedString = StringFormat.create(formatPattern).format(valueToFormat)
        assertEquals("4.0 {text}", formattedString)
    }

    @Test
    fun `value inside braces`() {
        val formatPattern = "{{{.1f}}}"
        val valueToFormat = 4
        val formattedString = StringFormat.create(formatPattern).format(valueToFormat)
        assertEquals("{4.0}", formattedString)
    }

    @Test
    fun `use original value in the string pattern`() {
        val formatPattern = "original value = {}"
        val valueToFormat = 4.2
        val formattedString = StringFormat.create(formatPattern).format(valueToFormat)
        assertEquals("original value = 4.2", formattedString)

        val formatter = { value: Any -> StringFormat.forOneArg("{}").format(value) }
        assertEquals("4", formatter(4))
        assertEquals("4.123", formatter(4.123))
        assertEquals("{.2f}", formatter("{.2f}"))
        assertEquals("value is {}", formatter("value is {}"))
    }

    @Test
    fun `static text in format`() {
        val formatPattern = "static text"
        val formattedString = StringFormat.create(formatPattern).format(emptyList())
        assertEquals("static text", formattedString)
    }

    @Test
    fun `numeric format for the string value will be ignored`() {
        val formatPattern = "{.1f} x {.2f}"
        val valuesToFormat = listOf("A", "B")
        val formattedString = StringFormat.create(formatPattern).format(valuesToFormat)
        assertEquals("A x B", formattedString)
    }

    @Test
    fun `different number of parameters in the pattern and number of values to format`() {
        val formatPattern = "{.1f} x {.2f} x {.3f}"
        val valuesToFormat = listOf(1, 2)

        val exception = assertFailsWith(IllegalStateException::class) {
            StringFormat.create(formatPattern).format(valuesToFormat)
        }
        assertEquals(
            "Can't format values [1, 2] with pattern '{.1f} x {.2f} x {.3f}'. Wrong number of arguments: expected 3 instead of 2",
            exception.message
        )
    }

    @Test
    fun `wrong number of arguments in pattern`() {
        assertFailsWith(IllegalArgumentException::class) {
            StringFormat.forOneArg("{.2f} {.2f}")
        }.let { exception ->
            assertEquals(
                "Wrong number of arguments in pattern '{.2f} {.2f}' . Expected 1 argument instead of 2",
                exception.message
            )
        }
        assertFailsWith(IllegalArgumentException::class) {
            StringFormat.forNArgs("{.2f} {.2f}", argCount = 3)
        }.let { exception ->
            assertEquals(
                "Wrong number of arguments in pattern '{.2f} {.2f}' . Expected 3 arguments instead of 2",
                exception.message
            )
        }
    }

    @Test
    fun `try to format non-numeric and non-string value`() {
        val formatPattern = "{.1f}"
        val valueToFormat = mapOf(1 to 2)

        val exception = assertFailsWith(IllegalStateException::class) {
            StringFormat.create(formatPattern).format(valueToFormat)
        }
        assertEquals(
            "Failed to format value with type SingletonMap. Supported types are Number and String.",
            exception.message
        )
    }

    @Test
    fun `string similar to a numeric format as static text`() {
        val formattedString = StringFormat.create(".2f", type = STRING_FORMAT).format(emptyList())
        assertEquals(".2f", formattedString)
    }

    @Test
    fun `try to format static text as number format`() {
        val exception = assertFailsWith(IllegalArgumentException::class) {
            StringFormat.create("pattern", type = NUMBER_FORMAT).format("text")
        }
        assertEquals(
            "Wrong number format pattern: 'pattern'",
            exception.message
        )
    }

    private val dateTimeToFormat = TimeZone.UTC.toInstant(
        DateTime(Date(6, Month.AUGUST, 2019), Time(4, 46, 35))
    ).timeSinceEpoch

    @Test
    fun `DateTime format`() {
        assertEquals("August", StringFormat.create("%B").format(dateTimeToFormat))
        assertEquals("Tuesday", StringFormat.create("%A").format(dateTimeToFormat))
        assertEquals("2019", StringFormat.create("%Y").format(dateTimeToFormat))
        assertEquals("06.08.19", StringFormat.create("%d.%m.%y").format(dateTimeToFormat))
        assertEquals("06.08.19 04:46", StringFormat.create("%d.%m.%y %H:%M").format(dateTimeToFormat))
    }

    @Test
    fun `string pattern with Number and DateTime`() {
        val formatPattern = "{d}nd day of {%B}"
        val valuesToFormat = listOf(2, dateTimeToFormat)
        val formattedString = StringFormat.create(formatPattern, STRING_FORMAT).format(valuesToFormat)
        assertEquals("2nd day of August", formattedString)
    }

    @Test
    fun `use DateTime format in the string pattern`() {
        assertEquals(
            "at 04:46 on Tuesday",
            StringFormat.create(
                pattern = "at {%H:%M} on {%A}",
                type = STRING_FORMAT
            ).format(listOf(dateTimeToFormat, dateTimeToFormat))
        )
    }
    @Test
    fun `DateTime format can be used to form the string without braces in its pattern`() {
        assertEquals(
            expected = "at 04:46 on Tuesday",
            StringFormat.create(
                pattern = "at %H:%M on %A",
                type = DATETIME_FORMAT
            ).format(dateTimeToFormat)
        )
    }

    @Test
    fun `Number pattern as DateTime format will return string with pattern`() {
        assertEquals(
            expected = ".1f",
            StringFormat.create(".1f", type = DATETIME_FORMAT).format(dateTimeToFormat)
        )
    }

    @Test
    fun `try to format static text as DateTime format`() {
        val exception = assertFailsWith(IllegalStateException::class) {
            StringFormat.create("%d.%m.%y").format("01.01.2000")
        }
        assertEquals(
            "Value '01.01.2000' to be formatted as DateTime expected to be a Number, but was String",
            exception.message
        )
    }

    @Test
    fun `try to use undefined pattern inside string pattern`() {
        val formatPattern = "{.1f} x {PP}"
        val valuesToFormat = listOf(1, 2)

        val exception = assertFailsWith(IllegalStateException::class) {
            StringFormat.create(formatPattern).format(valuesToFormat)
        }

        assertEquals(
            "Can't detect type of pattern 'PP' used in string pattern '{.1f} x {PP}'",
            exception.message
        )
    }
}