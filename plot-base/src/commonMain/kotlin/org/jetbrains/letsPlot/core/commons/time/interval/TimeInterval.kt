/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.time.interval

import org.jetbrains.letsPlot.commons.intern.datetime.Duration
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone

interface TimeInterval {

    val tickFormatPattern: String

    /**
     * @param start instant
     * @param end   instant
     * @return Returns every time interval after or equal to start and before end.
     */
    fun range(start: Double, end: Double, tz: TimeZone?): List<Double>

    companion object {
        fun milliseconds(count: Int): TimeInterval {
            return DurationInterval(Duration.MS, count)
        }

        fun seconds(count: Int): TimeInterval {
            return DurationInterval(Duration.SECOND, count)
        }

        fun minutes(count: Int): TimeInterval {
            return DurationInterval(Duration.MINUTE, count)
        }

        fun hours(count: Int): TimeInterval {
            return HourInterval(count)
        }

        fun days(count: Int): TimeInterval {
            return DayInterval(count)
        }

        fun weeks(count: Int): TimeInterval {
            return WeekInterval(count)
        }

        fun months(count: Int): TimeInterval {
            return MonthInterval(count)
        }

        fun years(count: Int): TimeInterval {
            return YearInterval(count)
        }
    }
}
