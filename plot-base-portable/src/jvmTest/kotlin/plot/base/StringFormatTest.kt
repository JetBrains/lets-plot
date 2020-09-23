/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import jetbrains.datalore.plot.base.util.StringFormat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StringFormatTest {

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
    fun `string pattern with curly brackets`() {
        val formatPattern = "{.1f} {{text}}"
        val valueToFormat = 4
        val formattedString = StringFormat(formatPattern).format(valueToFormat)
        assertEquals("4.0 {text}", formattedString)
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
        assertEquals("Wrong format pattern \"$formatPattern\" to format values=$valuesToFormat", exception.message)
    }

    @Test
    fun `try to format non-numeric and non-string value`() {
        val formatPattern = "{.1f}"
        val valueToFormat = mapOf(1 to 2)

        val exception = assertFailsWith(IllegalStateException::class) {
            StringFormat(formatPattern).format(valueToFormat)
        }
        assertEquals("Wrong value to format as a number: $valueToFormat", exception.message)
    }
}