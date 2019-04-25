package jetbrains.datalore.visualization.plot.gog.common.time

import jetbrains.datalore.base.datetime.Date
import jetbrains.datalore.base.datetime.DateTime
import jetbrains.datalore.base.datetime.Instant
import jetbrains.datalore.base.datetime.tz.TimeZone

object TimeUtil {
    fun asDateTimeUTC(instant: Double): DateTime {
        try {
            return TimeZone.UTC.toDateTime(Instant(Math.round(instant)))
        } catch (ignored: RuntimeException) {
            throw IllegalArgumentException("Can't create DateTime from instant $instant")
        }

    }

    fun asInstantUTC(dateTime: DateTime): Long {
        return TimeZone.UTC.toInstant(dateTime).timeSinceEpoch
    }

    fun yearStart(year: Int): DateTime {
        return DateTime(Date.firstDayOf(year))
    }
}
