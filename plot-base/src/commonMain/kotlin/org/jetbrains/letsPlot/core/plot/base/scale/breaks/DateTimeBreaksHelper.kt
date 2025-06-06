/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.datetime.DateTimeFormatUtil.createInstantFormatter
import org.jetbrains.letsPlot.commons.formatting.string.StringFormat.ExponentFormat.Companion.DEF_EXPONENT_FORMAT
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone
import org.jetbrains.letsPlot.core.commons.time.interval.NiceTimeInterval
import org.jetbrains.letsPlot.core.commons.time.interval.TimeInterval
import org.jetbrains.letsPlot.core.commons.time.interval.YearInterval
import kotlin.math.round

class DateTimeBreaksHelper constructor(
    rangeStart: Double,
    rangeEnd: Double,
    count: Int,
    private val providedFormatter: ((Any) -> String)?,
    minInterval: NiceTimeInterval?,
    maxInterval: NiceTimeInterval?,
    private val tz: TimeZone?,
) : BreaksHelperBase(rangeStart, rangeEnd, count) {

    override val breaks: List<Double>
    val formatter: (Any) -> String
    val pattern: String
    private val timeZone: TimeZone get() = tz ?: TimeZone.UTC

    init {
        val step = targetStep

        pattern = if (step < 1000) {        // milliseconds
            // regular nice breaks
            breaks = LinearBreaksHelper(rangeStart, rangeEnd, count, DUMMY_FORMATTER, DEF_EXPONENT_FORMAT).breaks
            // milliseconds formatter
            if (minInterval != null) {
                minInterval.tickFormatPattern
            } else {
                TimeInterval.milliseconds(1).tickFormatPattern
            }

        } else {

            val start = normalStart
            val end = normalEnd

            var ticks: MutableList<Double>? = null
            if (minInterval != null) {
                ticks = minInterval.range(start, end, tz).toMutableList()
            }

            val pattern = if (ticks != null && ticks.size <= count) {
                // same or smaller interval requested -> stay with the min interval
                (minInterval as TimeInterval).tickFormatPattern
                // otherwise - larger step requested -> compute ticks
            } else if (step > YearInterval.MS) {        // years
                ticks = ArrayList()
                val startDateTime = DateTime.ofEpochMilliseconds(start, timeZone)
                var startYear = startDateTime.year
                if (startDateTime > DateTime.ofYearStart(startYear)) {
                    startYear++
                }
                val endYear = DateTime.ofEpochMilliseconds(end, timeZone).year
                val helper = LinearBreaksHelper(
                    startYear.toDouble(),
                    endYear.toDouble(),
                    count,
                    expFormat = DEF_EXPONENT_FORMAT,
                    providedFormatter = DUMMY_FORMATTER
                )
                for (tickYear in helper.breaks) {
                    val tickDate = DateTime.ofYearStart(round(tickYear).toInt())
                    ticks.add(tickDate.toEpochMilliseconds(timeZone).toDouble())
                }

                if (maxInterval != null) {
                    // max interval is guaranteed to be less than a year interval.
                    (maxInterval as TimeInterval).tickFormatPattern
                } else {
                    YearInterval.TICK_FORMAT
                }
            } else {
                val interval = NiceTimeInterval.forMillis(step, minInterval, maxInterval)
                ticks = interval.range(start, end, tz).toMutableList()
                interval.tickFormatPattern
            }

            if (isReversed) {
                ticks.reverse()
            }
            breaks = ticks

            pattern
        }

        formatter = providedFormatter ?: createInstantFormatter(pattern, timeZone)
    }
}
