/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.ExponentFormat
import org.jetbrains.letsPlot.core.commons.time.TimeUtil
import org.jetbrains.letsPlot.core.commons.time.interval.NiceTimeInterval
import org.jetbrains.letsPlot.core.commons.time.interval.TimeInterval
import org.jetbrains.letsPlot.core.commons.time.interval.YearInterval
import kotlin.math.round

class DateTimeBreaksHelper(
    rangeStart: Double,
    rangeEnd: Double,
    count: Int,
    private val providedFormatter: ((Any) -> String)?,
    minInterval: TimeInterval? = null,
) : BreaksHelperBase(rangeStart, rangeEnd, count) {

    override val breaks: List<Double>
    val formatter: (Any) -> String
    val pattern: String

    init {
        val step = targetStep
        var dateTimeFormatter: (Number) -> String

        if (step < 1000) {        // milliseconds
            val interval = TimeScaleTickFormatterFactory(minInterval)
            dateTimeFormatter = interval.getFormatter(step)
            pattern = interval.formatPattern(step)
            // compute step so that it is multiple of automatic time steps
            breaks = LinearBreaksHelper(rangeStart, rangeEnd, count, DUMMY_FORMATTER, ExponentFormat.E).breaks

        } else {

            val start = normalStart
            val end = normalEnd

            var ticks: MutableList<Double>? = null
            if (minInterval != null) {
                ticks = minInterval.range(start, end).toMutableList()
            }

            if (ticks != null && ticks.size <= count) {
                // same or smaller interval requested -> stay with min interval
                dateTimeFormatter = minInterval!!.tickFormatter
                pattern = minInterval.tickFormatPattern
                // otherwise - larger step requested -> compute ticks
            } else if (step > YearInterval.MS) {        // years
                dateTimeFormatter = YearInterval.TICK_FORMATTER
                pattern = YearInterval.TICK_FORMAT
                ticks = ArrayList()
                val startDateTime = TimeUtil.asDateTimeUTC(start)
                var startYear = startDateTime.year
                if (startDateTime.isAfter(TimeUtil.yearStart(startYear))) {
                    startYear++
                }
                val endYear = TimeUtil.asDateTimeUTC(end).year
                val helper = LinearBreaksHelper(
                    startYear.toDouble(),
                    endYear.toDouble(),
                    count,
                    exponentFormat = ExponentFormat.E,
                    providedFormatter = DUMMY_FORMATTER
                )
                for (tickYear in helper.breaks) {
                    val tickDate = TimeUtil.yearStart(round(tickYear).toInt())
                    ticks.add(TimeUtil.asInstantUTC(tickDate).toDouble())
                }
            } else {
                val interval = NiceTimeInterval.forMillis(step)
                dateTimeFormatter = interval.tickFormatter
                pattern = interval.tickFormatPattern
                ticks = interval.range(start, end).toMutableList()
            }

            if (isReversed) {
                ticks.reverse()
            }
            breaks = ticks
        }

        formatter = providedFormatter ?: { v: Any -> dateTimeFormatter(v as Number) }
    }
}
