package jetbrains.datalore.visualization.plot.common.time.interval

import jetbrains.datalore.base.datetime.Date
import jetbrains.datalore.base.datetime.DateTime
import jetbrains.datalore.base.datetime.Month

internal class MonthInterval(count: Int) : MeasuredInDays(count) {

    override val tickFormatPattern: String
        get() = "MMM"

    override fun getFirstDayContaining(instant: DateTime): Date {
        var firstDay = instant.date
        firstDay = Date.firstDayOf(firstDay.year, firstDay.month)
        return firstDay
    }

    override fun addInterval(toInstant: DateTime): DateTime {
        var result = toInstant
        for (i in 0 until count) {
            result = addMonth(result)
        }
        return result
    }

    private fun addMonth(toInstant: DateTime): DateTime {
        var year = toInstant.year
        val month = toInstant.month
        var next = month!!.next()
        if (next == null) {
            next = Month.JANUARY
            year++
        }
        return DateTime(Date.firstDayOf(year, next))
    }
}
