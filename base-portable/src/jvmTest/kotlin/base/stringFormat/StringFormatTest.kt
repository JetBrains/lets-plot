/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.stringFormat

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StringFormatTest {

    @Test
    fun `check expected number of arguments`() {
        assertEquals(0, StringFormat("text").argsNumber)
        assertEquals(1, StringFormat("{.1f}").argsNumber)
        assertEquals(1, StringFormat("{.1f} test").argsNumber)
        assertEquals(2, StringFormat("{.1f} {}").argsNumber)
        assertEquals(3, StringFormat("{.1f} {.2f} {.3f}").argsNumber)
    }

    @Test
    fun `numeric format`() {
        val formatPattern = ".2f"
        val valueToFormat = 4
        val formattedString = StringFormat(formatPattern).format(valueToFormat)
        assertEquals("4.00", formattedString)
    }

    @Test
    fun `numeric format in the string pattern`() {
        val formatPattern = "{.2f}"
        val valueToFormat = 4
        val formattedString = StringFormat(formatPattern).format(valueToFormat)
        assertEquals("4.00", formattedString)
    }

    @Test
    fun `string pattern with multiple parameters`() {
        val formatPattern = "{.1f} x {.2f}"
        val valuesToFormat = listOf(1, 2)
        val formattedString = StringFormat(formatPattern).format(valuesToFormat)
        assertEquals("1.0 x 2.00", formattedString)
    }

    @Test
    fun `string pattern with braces`() {
        val formatPattern = "{.1f} {{text}}"
        val valueToFormat = 4
        val formattedString = StringFormat(formatPattern).format(valueToFormat)
        assertEquals("4.0 {text}", formattedString)
    }

    @Test
    fun `value inside braces`() {
        val formatPattern = "{{{.1f}}}"
        val valueToFormat = 4
        val formattedString = StringFormat(formatPattern).format(valueToFormat)
        assertEquals("{4.0}", formattedString)
    }

    @Test
    fun `use original value in the string pattern`() {
        val formatPattern = "original value = {}"
        val valueToFormat = 4.2
        val formattedString = StringFormat(formatPattern).format(valueToFormat)
        assertEquals("original value = 4.2", formattedString)
    }

    @Test
    fun `static text in format`() {
        val formatPattern = "static text"
        val formattedString = StringFormat(formatPattern).format(emptyList())
        assertEquals("static text", formattedString)
    }

    @Test
    fun `numeric format for the string value will be ignored`() {
        val formatPattern = "{.1f} x {.2f}"
        val valuesToFormat = listOf("A", "B")
        val formattedString = StringFormat(formatPattern).format(valuesToFormat)
        assertEquals("A x B", formattedString)
    }

    @Test
    fun `different number of parameters in the pattern and number of values to format`() {
        val formatPattern = "{.1f} x {.2f} x {.3f}"
        val valuesToFormat = listOf(1, 2)

        val exception = assertFailsWith(IllegalStateException::class) {
            StringFormat(formatPattern).format(valuesToFormat)
        }
        assertEquals("Can't format values [1, 2] with pattern \"{.1f} x {.2f} x {.3f}\"). Wrong number of arguments: expected 3 instead of 2", exception.message)
    }

    @Test
    fun `try to format non-numeric and non-string value`() {
        val formatPattern = "{.1f}"
        val valueToFormat = mapOf(1 to 2)

        val exception = assertFailsWith(IllegalStateException::class) {
            StringFormat(formatPattern).format(valueToFormat)
        }
        assertEquals(
            "Failed to format value with type SingletonMap. Supported types are Number and String.",
            exception.message
        )
    }
}