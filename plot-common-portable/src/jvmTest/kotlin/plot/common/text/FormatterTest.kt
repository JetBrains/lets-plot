/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.text

import jetbrains.datalore.base.datetime.Date
import jetbrains.datalore.base.datetime.DateTime
import jetbrains.datalore.base.datetime.Month
import jetbrains.datalore.base.datetime.Time
import jetbrains.datalore.base.datetime.tz.TimeZone
import jetbrains.datalore.plot.common.data.DataType
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatterTest {
    private fun testInstant(): Double {
        val baseDate = Date(1, Month.JANUARY, 2015)
        val baseTime = Time(7, 7, 7, 7)             // 07:07:07.007
        val instant = TimeZone.UTC.toInstant(DateTime(baseDate, baseTime))
        return instant.timeSinceEpoch.toDouble()
    }

    @BeforeTest
    fun setUSAsDefaultLocale() {
        Locale.setDefault(Locale.US)
    }

    @Test
    fun time() {
        val testMillis = testInstant()

        var tooltipFormatter = Formatter.tooltip(DataType.INSTANT)
        var dataFormatter = Formatter.tableCell(DataType.INSTANT)
        assertEquals("Thu, Jan 1, 2015 7:07 AM", tooltipFormatter(testMillis))
        assertEquals("Thu, Jan 1, '15", dataFormatter(testMillis))

        tooltipFormatter = Formatter.tooltip(DataType.INSTANT_OF_DAY)
        dataFormatter = Formatter.tableCell(DataType.INSTANT_OF_DAY)
        assertEquals("Thu, Jan 1, 2015", tooltipFormatter(testMillis))
        assertEquals("Jan 1", dataFormatter(testMillis))

        tooltipFormatter =
            Formatter.tooltip(DataType.INSTANT_OF_MONTH)
        dataFormatter = Formatter.tableCell(DataType.INSTANT_OF_MONTH)
        assertEquals("January 2015", tooltipFormatter(testMillis))
        assertEquals("Jan", dataFormatter(testMillis))

        // ToDo: `Q` is not supported by java.text.SimpleDateFormat
//        tooltipFormatter = Formatter.tooltip(DataType.INSTANT_OF_HALF_YEAR)
//        dataFormatter = Formatter.tableCell(DataType.INSTANT_OF_HALF_YEAR)
//        assertEquals("Q1 2015", tooltipFormatter.apply(testMillis))
//        assertEquals("Semester 1", dataFormatter.apply(testMillis))

        // ToDo: `Q` is not supported by java.text.SimpleDateFormat
//        tooltipFormatter = Formatter.tooltip(DataType.INSTANT_OF_QUARTER)
//        dataFormatter = Formatter.tableCell(DataType.INSTANT_OF_QUARTER)
//        assertEquals("Q1 2015", tooltipFormatter.apply(testMillis))
//        assertEquals("Q1", dataFormatter.apply(testMillis))

        tooltipFormatter = Formatter.tooltip(DataType.INSTANT_OF_YEAR)
        dataFormatter = Formatter.tableCell(DataType.INSTANT_OF_YEAR)
        assertEquals("Jan 2015", tooltipFormatter(testMillis))
        assertEquals("2015", dataFormatter(testMillis))
    }

    @Test
    @Ignore
    fun number() {
        val expected = listOf(
                "19,999",
                "19,999.55",
                "19,999.56",
                "null",
                "NaN",
                "\u221e",
                "\u221e")

        val values = listOf(
                19_999.00,
                19_999.55,
                19_999.56,
                null,
                Double.NaN,
                Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY
        )

        assertNumbersFormattedByTooltipFormatter(expected, values)
        assertNumbersFormattedByTableCellFormatter(expected, values)
    }

    private fun assertNumbersFormattedByTooltipFormatter(expected: List<String>, values: List<*>) {
        assertValuesFormatted(expected, values,
            Formatter.tooltip(DataType.NUMBER)
        )
    }

    private fun assertNumbersFormattedByTableCellFormatter(expected: List<String>, values: List<*>) {
        assertValuesFormatted(expected, values,
            Formatter.tableCell(DataType.NUMBER)
        )
    }

    private fun assertValuesFormatted(expected: List<String>, values: List<*>, formatter: (Any?) -> String) {
        assertEquals(expected.size, values.size)
        val expectedIter = expected.iterator()
        for (value in values) {
            val nextExpected = expectedIter.next()
            assertEquals(nextExpected, formatter(value))
        }
    }
}
