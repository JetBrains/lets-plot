/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.time.interval

object NiceTimeInterval {
    private val AUTO_STEPS_MS = doubleArrayOf(1000.0, 5000.0, 15000.0, 30000.0, // 1-, 5-, 15- and 30-second.
            6e4, 5 * 6e4, 15 * 6e4, 30 * 6e4, // 1-, 5-, 15- and 30-minute.
            36e5, 3 * 36e5, 6 * 36e5, 12 * 36e5, // 1-, 3-, 6- and 12-hour.
            864e5, 2 * 864e5, // 1- and 2-day.
            6048e5, // 1-week.
            2592e6, 3 * 2592e6, // 1- and 3-month.
        YearInterval.MS                                  // 1-year.
    )

    private val AUTO_INTERVALS = arrayOf(
        TimeInterval.seconds(1),
        TimeInterval.seconds(5),
        TimeInterval.seconds(15),
        TimeInterval.seconds(30),

        TimeInterval.minutes(1),
        TimeInterval.minutes(5),
        TimeInterval.minutes(15),
        TimeInterval.minutes(30),

        TimeInterval.hours(1),
        TimeInterval.hours(3),
        TimeInterval.hours(6),
        TimeInterval.hours(12),

        TimeInterval.days(1),
        TimeInterval.days(2),

        TimeInterval.weeks(1),

        TimeInterval.months(1),
        TimeInterval.months(3),

        TimeInterval.years(1)
    )

    fun forMillis(interval: Double): TimeInterval {
        if (interval <= AUTO_STEPS_MS[0]) {
            return AUTO_INTERVALS[0]
        }

        var result = AUTO_INTERVALS[AUTO_STEPS_MS.size - 1]
        for (i in 1 until AUTO_STEPS_MS.size) {
            if (AUTO_STEPS_MS[i] >= interval) {
                val deltaDown = interval - AUTO_STEPS_MS[i - 1]
                val deltaUp = AUTO_STEPS_MS[i] - interval
                if (deltaDown < deltaUp) {
                    result = AUTO_INTERVALS[i - 1]
                } else {
                    result = AUTO_INTERVALS[i]
                }
                break
            }
        }
        return result
    }
}
