package jetbrains.datalore.visualization.plot.gog.core.scale.breaks

import jetbrains.datalore.base.datetime.*
import jetbrains.datalore.base.datetime.tz.TimeZone
import jetbrains.datalore.visualization.plot.gog.common.text.DateTimeFormatUtil
import jetbrains.datalore.visualization.plot.gog.common.time.interval.TimeInterval
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeTickFormatTest {

    @Test
    fun intervalSecondsDefault() {
        val sec = format(BASE_DATE_TIME, TimeInterval.seconds(1).tickFormatPattern)
        assertEquals("07", sec)
    }

    @Test
    fun intervalMinutesDefault() {
        val min = format(BASE_DATE_TIME, TimeInterval.minutes(1).tickFormatPattern)
        assertEquals("07", min)
    }

    @Test
    fun intervalHoursDefault() {
        val hour = format(BASE_DATE_TIME, TimeInterval.hours(1).tickFormatPattern)
        assertEquals("7:07", hour)
    }

    @Test
    fun intervalDaysDefault() {
        val day = format(BASE_DATE_TIME, TimeInterval.days(1).tickFormatPattern)
        assertEquals("Jan 1", day)
    }

    @Test
    fun intervalWeeksDefault() {
        val week = format(BASE_DATE_TIME, TimeInterval.weeks(1).tickFormatPattern)
        assertEquals("Jan 1", week)
    }

    @Test
    fun intervalMonthsDefault() {
        val month = format(BASE_DATE_TIME, TimeInterval.months(1).tickFormatPattern)
        assertEquals("Jan", month)
    }

    @Test
    fun intervalYearsDefault() {
        val year = format(BASE_DATE_TIME, TimeInterval.years(1).tickFormatPattern)
        assertEquals("2013", year)
    }

    private fun format(dateTime: DateTime, pattern: String): String {
        return DateTimeFormatUtil.formatDate(JvmDateTimeUtil.toJavaDate(dateTime)!!, pattern)
    }

    companion object {
        private val BASE_DATE = Date(1, Month.JANUARY, 2013)
        private val BASE_TIME = Time(7, 7, 7, 7)             // 07:07:07.007
        private val BASE_DATE_TIME = DateTime(BASE_DATE, BASE_TIME)
        private val baseInstant = TimeZone.UTC.toInstant(DateTime(BASE_DATE, BASE_TIME))
    }
}
