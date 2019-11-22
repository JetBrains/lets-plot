/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.breaks

import jetbrains.datalore.plot.common.time.TimeUtil
import jetbrains.datalore.plot.common.time.interval.NiceTimeInterval
import jetbrains.datalore.plot.common.time.interval.TimeInterval
import jetbrains.datalore.plot.common.time.interval.YearInterval
import kotlin.math.round

class DateTimeBreaksHelper(
        rangeStart: Double,
        rangeEnd: Double,
        count: Int,
        minInterval: TimeInterval?
) : BreaksHelperBase(rangeStart, rangeEnd, count) {

    override val breaks: List<Double>
    override val labelFormatter: (Any) -> String

    constructor(rangeStart: Double, rangeEnd: Double, count: Int) : this(rangeStart, rangeEnd, count, null)

    init {

        val step = targetStep
        if (step < 1000) {        // milliseconds
            labelFormatter = QuantitativeTickFormatterFactory.forTimeScale(
                minInterval
            ).getFormatter(step)
            // compute step so that it is multiple of automatic time steps
            breaks = LinearBreaksHelper(rangeStart, rangeEnd, count).breaks

        } else {

            val start = normalStart
            val end = normalEnd

            var ticks: MutableList<Double>? = null
            if (minInterval != null) {
                ticks = minInterval.range(start, end).toMutableList()
            }

            if (ticks != null && ticks.size <= count) {
                // same or smaller interval requested -> stay with min interval
                labelFormatter = minInterval!!.tickFormatter
                // otherwise - larger step requested -> compute ticks
            } else if (step > YearInterval.MS) {        // years
                labelFormatter = YearInterval.TICK_FORMATTER
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
                    count
                )
                for (tickYear in helper.breaks) {
                    val tickDate = TimeUtil.yearStart(round(tickYear).toInt())
                    ticks.add(TimeUtil.asInstantUTC(tickDate).toDouble())
                }
            } else {
                val interval = NiceTimeInterval.forMillis(step)
                labelFormatter = interval.tickFormatter
                ticks = interval.range(start, end).toMutableList()
            }

            if (isReversed) {
                ticks.reverse()
            }
            breaks = ticks
        }
    }
}
