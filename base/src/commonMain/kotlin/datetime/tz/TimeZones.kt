package jetbrains.datalore.base.datetime.tz

import jetbrains.datalore.base.datetime.*

internal object TimeZones {
    private val MILLIS_IN_SECOND: Long = 1000
    private val MILLIS_IN_MINUTE = MILLIS_IN_SECOND * 60
    private val MILLIS_IN_HOUR = MILLIS_IN_MINUTE * 60
    private val MILLIS_IN_DAY = MILLIS_IN_HOUR * 24

    private fun toDateTime(instant: Instant, offset: Duration): DateTime {
        var instant = instant
        instant = instant.add(offset)

        val days = (instant.timeSinceEpoch / MILLIS_IN_DAY).toInt()
        val date = Date.EPOCH.addDays(days)
        var rest = instant.timeSinceEpoch % MILLIS_IN_DAY
        val hour = (rest / MILLIS_IN_HOUR).toInt()
        rest %= MILLIS_IN_HOUR
        val minutes = (rest / MILLIS_IN_MINUTE).toInt()
        rest %= MILLIS_IN_MINUTE
        val seconds = (rest / MILLIS_IN_SECOND).toInt()
        rest %= MILLIS_IN_SECOND
        val milliseconds = (rest % MILLIS_IN_SECOND).toInt()
        return DateTime(date, Time(hour, minutes, seconds, milliseconds))
    }

    private fun toInstant(dateTime: DateTime, offset: Duration): Instant {
        return Instant(toMillis(dateTime.date) + toMillis(dateTime.time)).sub(offset)
    }

    private fun toMillis(time: Time): Long {
        val minutes = time.hours * 60 + time.minutes
        val seconds = minutes * 60 + time.seconds
        val milliseconds = seconds * 1000 + time.milliseconds
        return milliseconds.toLong()
    }

    private fun toMillis(date: Date): Long {
        return date.daysFrom(Date.EPOCH) * MILLIS_IN_DAY
    }

    fun utc(): TimeZone {
        return object : TimeZone("UTC") {
            override fun toDateTime(instant: Instant): DateTime {
                return TimeZones.toDateTime(instant, Duration(0))
            }

            override fun toInstant(dateTime: DateTime): Instant {
                return TimeZones.toInstant(dateTime, Duration(0))
            }
        }
    }

    fun offset(id: String?, offset: Duration, base: TimeZone): TimeZone {
        return object : TimeZone(id) {
            override fun toDateTime(instant: Instant): DateTime {
                return base.toDateTime(instant.add(offset))
            }

            override fun toInstant(dateTime: DateTime): Instant {
                return base.toInstant(dateTime).sub(offset)
            }

        }
    }

    fun withEuSummerTime(id: String, offset: Duration): TimeZone {
        val startSpec = DateSpecs.last(WeekDay.SUNDAY, Month.MARCH)
        val endSpec = DateSpecs.last(WeekDay.SUNDAY, Month.OCTOBER)
        val utcChangeTime = Time(1, 0)
        return object : DSTimeZone(id, offset) {
            override fun getStartInstant(year: Int): Instant {
                return TimeZone.UTC.toInstant(DateTime(startSpec.getDate(year), utcChangeTime))
            }

            override fun getEndInstant(year: Int): Instant {
                return TimeZone.UTC.toInstant(DateTime(endSpec.getDate(year), utcChangeTime))
            }

        }
    }

    fun withUsSummerTime(id: String, offset: Duration): TimeZone {
        val startSpec = DateSpecs.first(WeekDay.SUNDAY, Month.MARCH, 2)
        val endSpec = DateSpecs.first(WeekDay.SUNDAY, Month.NOVEMBER)

        return object : DSTimeZone(id, offset) {
            override fun getStartInstant(year: Int): Instant {
                return TimeZone.UTC.toInstant(DateTime(startSpec.getDate(year), Time(2, 0))).sub(offset)
            }

            override fun getEndInstant(year: Int): Instant {
                return TimeZone.UTC.toInstant(DateTime(endSpec.getDate(year), Time(2, 0))).sub(offset.add(Duration.HOUR))
            }

        }
    }

    private abstract class DSTimeZone(id: String, offset: Duration) : TimeZone(id) {
        private val myTz: TimeZone
        private val mySummerTz: TimeZone

        init {
            myTz = offset(null, offset, TimeZone.UTC)
            mySummerTz = offset(null, offset.add(Duration.HOUR), TimeZone.UTC)
        }

        override fun toDateTime(instant: Instant): DateTime {
            val tzDt = myTz.toDateTime(instant)
            val start = getStartInstant(tzDt.year)
            val end = getEndInstant(tzDt.year)
            return if (instant.compareTo(start) > 0 && instant.compareTo(end) < 0) {
                mySummerTz.toDateTime(instant)
            } else {
                tzDt
            }
        }

        override fun toInstant(dateTime: DateTime): Instant {
            val startDt = toDateTime(getStartInstant(dateTime.year))
            val endDt = toDateTime(getEndInstant(dateTime.year))

            return if (dateTime.compareTo(startDt) > 0 && dateTime.compareTo(endDt) < 0) {
                mySummerTz.toInstant(dateTime)
            } else {
                myTz.toInstant(dateTime)
            }
        }

        protected abstract fun getStartInstant(year: Int): Instant

        protected abstract fun getEndInstant(year: Int): Instant
    }
}
