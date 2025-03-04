/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Month
import org.jetbrains.letsPlot.commons.intern.datetime.tz.TimeZone.Companion.UTC
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport

object VegaDataUtil {
    fun parseVegaDataset(content: String, url: String): Any =
        when {
            url.endsWith(".json") -> JsonSupport.parse(content)!!
            url.endsWith("data/stocks.csv") -> parseCsv(content) { column: String, value: String ->
                when (column) {
                    "price" -> value.toDouble()
                    "date" -> {
                        val (monthName, day, year) = value.split(" ")
                        val month = when (monthName) {
                            "Jan" -> "1"
                            "Feb" -> "2"
                            "Mar" -> "3"
                            "Apr" -> "4"
                            "May" -> "5"
                            "Jun" -> "6"
                            "Jul" -> "7"
                            "Aug" -> "8"
                            "Sep" -> "9"
                            "Oct" -> "10"
                            "Nov" -> "11"
                            "Dec" -> "12"
                            else -> error("Unexpected month: $monthName")
                        }

                        dateTimeToEpoch(year, month, day)
                    }

                    else -> value
                }
            }
            url.endsWith("data/seattle-weather.csv") -> parseCsv(content) { column: String, value: String ->
                when (column) {
                    "date" -> {
                        if (value.isEmpty()) return@parseCsv null
                        val (year, month, day) = value.split("-")
                        dateTimeToEpoch(year, month, day)
                    }
                    "precipitation", "temp_max", "temp_min", "wind" -> value.toDouble()
                    else -> value
                }
            }

            else -> parseCsv(content)
        }

    // Month is 1-based, e.g. "1" for January
    private fun dateTimeToEpoch(year: String, month: String, day: String): Long {
        val date = Date(day = day.toInt(), month = Month.values()[month.toInt() - 1], year = year.toInt())
        return UTC.toInstant(DateTime(date)).timeSinceEpoch
    }

    private fun parseCsv(
        string: String,
        transform: (columnName: String, columnValue: String) -> Any? = { _, v -> v }
    ): List<Map<String, Any?>> {
        val columns = string.lineSequence().first().split(",")
        val data = string.lineSequence()
            .drop(1)
            .map { line ->
                columns.zip(line.split(","))
                    .associate { (column, value) -> column to transform(column, value) }
            }.toList()

        return data
    }

}
