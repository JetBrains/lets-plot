/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.time.interval

import jetbrains.datalore.base.datetime.Duration
import jetbrains.datalore.plot.common.data.DataType
import jetbrains.datalore.plot.common.text.Formatter

abstract class TimeInterval protected constructor(val count: Int) {

    abstract val tickFormatPattern: String

    open val tickFormatter: (Any) -> String
        get() = Formatter.time(tickFormatPattern)

    /**
     * @param start instant
     * @param end   instant
     * @return Returns every time interval after or equal to start and before end.
     */
    abstract fun range(start: Double, end: Double): List<Double>

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
            return DurationInterval(Duration.HOUR, count)
        }

        fun days(count: Int): TimeInterval {
            return DurationInterval(Duration.DAY, count)
        }

        fun weeks(count: Int): TimeInterval {
            return DurationInterval(Duration.WEEK, count)
        }

        fun months(count: Int): TimeInterval {
            return MonthInterval(count)
        }

        private fun quarter(count: Int): TimeInterval {
            return QuarterInterval(count)
        }

        private fun semester(count: Int): TimeInterval {
            return SemesterInterval(count)
        }

        fun years(count: Int): TimeInterval {
            return YearInterval(count)
        }

        fun fromIntervalDataType(dataType: DataType): TimeInterval {
            return when (dataType) {
                DataType.INSTANT_OF_DAY -> days(
                    1
                )
                DataType.INSTANT_OF_MONTH -> months(
                    1
                )
                DataType.INSTANT_OF_QUARTER -> quarter(
                    1
                )
                DataType.INSTANT_OF_HALF_YEAR -> semester(
                    1
                )
                DataType.INSTANT_OF_YEAR -> years(
                    1
                )
                else -> throw IllegalArgumentException("Can't create interval from data type: $dataType")
            }
        }
    }
}
