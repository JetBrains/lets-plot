/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.time.interval

import org.jetbrains.letsPlot.core.commons.data.DataType

enum class NiceTimeInterval(
    private val timeInterval: TimeInterval
) : TimeInterval by timeInterval {
    ONE_SECOND(TimeInterval.seconds(1)),
    FIVE_SECONDS(TimeInterval.seconds(5)),
    FIFTEEN_SECONDS(TimeInterval.seconds(15)),
    THIRTY_SECONDS(TimeInterval.seconds(30)),

    ONE_MINUTE(TimeInterval.minutes(1)),
    FIVE_MINUTES(TimeInterval.minutes(5)),
    FIFTEEN_MINUTES(TimeInterval.minutes(15)),
    THIRTY_MINUTES(TimeInterval.minutes(30)),

    ONE_HOUR(TimeInterval.hours(1)),
    THREE_HOURS(TimeInterval.hours(3)),
    SIX_HOURS(TimeInterval.hours(6)),
    TWELVE_HOURS(TimeInterval.hours(12)),

    ONE_DAY(TimeInterval.days(1)),
    TWO_DAYS(TimeInterval.days(2)),

    ONE_WEEK(TimeInterval.weeks(1)),

    ONE_MONTH(TimeInterval.months(1)),
    THREE_MONTHS(TimeInterval.months(3)),

    ONE_YEAR(TimeInterval.years(1));

    companion object {
        private val AUTO_STEPS_MS = doubleArrayOf(
            1000.0, 5000.0, 15000.0, 30000.0, // 1-, 5-, 15- and 30-second.
            6e4, 5 * 6e4, 15 * 6e4, 30 * 6e4, // 1-, 5-, 15- and 30-minute.
            36e5, 3 * 36e5, 6 * 36e5, 12 * 36e5, // 1-, 3-, 6- and 12-hour.
            864e5, 2 * 864e5, // 1- and 2-day.
            6048e5, // 1-week.
            2592e6, 3 * 2592e6, // 1- and 3-month.
            YearInterval.MS                                  // 1-year.
        )

//        private val AUTO_INTERVALS = arrayOf(
//            TimeInterval.seconds(1),
//            TimeInterval.seconds(5),
//            TimeInterval.seconds(15),
//            TimeInterval.seconds(30),
//
//            TimeInterval.minutes(1),
//            TimeInterval.minutes(5),
//            TimeInterval.minutes(15),
//            TimeInterval.minutes(30),
//
//            TimeInterval.hours(1),
//            TimeInterval.hours(3),
//            TimeInterval.hours(6),
//            TimeInterval.hours(12),
//
//            TimeInterval.days(1),
//            TimeInterval.days(2),
//
//            TimeInterval.weeks(1),
//
//            TimeInterval.months(1),
//            TimeInterval.months(3),
//
//            TimeInterval.years(1)
//        )

        fun minIntervalOf(dataType: DataType): NiceTimeInterval? {
            return when (dataType) {
                DataType.DATE_MILLIS -> ONE_DAY
                else -> null
            }
        }

        fun maxIntervalOf(dataType: DataType): NiceTimeInterval? {
            return when (dataType) {
                DataType.TIME_MILLIS -> TWELVE_HOURS
                else -> null
            }
        }

        fun forMillis(
            millis: Double,
            minInterval: NiceTimeInterval?,
            maxInterval: NiceTimeInterval?,
        ): NiceTimeInterval {
            val niceInterval = forMillisIntern(millis)
            return if (minInterval != null && niceInterval < minInterval) {
                minInterval
            } else if (maxInterval != null && niceInterval > maxInterval) {
                maxInterval
            } else {
                niceInterval
            }
        }

        private fun forMillisIntern(millis: Double): NiceTimeInterval {
            if (millis <= AUTO_STEPS_MS[0]) {
                return entries[0]
            }

            var result = entries[AUTO_STEPS_MS.size - 1]
            for (i in 1 until AUTO_STEPS_MS.size) {
                if (AUTO_STEPS_MS[i] >= millis) {
                    val deltaDown = millis - AUTO_STEPS_MS[i - 1]
                    val deltaUp = AUTO_STEPS_MS[i] - millis
                    if (deltaDown < deltaUp) {
                        result = entries[i - 1]
                    } else {
                        result = entries[i]
                    }
                    break
                }
            }
            return result
        }
    }
}