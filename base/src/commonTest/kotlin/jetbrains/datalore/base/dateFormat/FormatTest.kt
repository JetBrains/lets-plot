package jetbrains.datalore.base.dateFormat

import jetbrains.datalore.base.datetime.Date
import jetbrains.datalore.base.datetime.DateTime
import jetbrains.datalore.base.datetime.Month
import jetbrains.datalore.base.datetime.Time
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatTest {
    private val date = Date(6, Month.AUGUST, 2019)
    private val time = Time(4, 46, 35)
    private val dateTime = DateTime(date, time)

    @Test
    fun datePatterns() {
        assertEquals("Tue", Format("%a").apply(dateTime))
        assertEquals("Tuesday", Format("%A").apply(dateTime))
        assertEquals("Aug", Format("%b").apply(dateTime))
        assertEquals("August", Format("%B").apply(dateTime))
        assertEquals("06", Format("%d").apply(dateTime))
        assertEquals("6", Format("%e").apply(dateTime))
        assertEquals("218", Format("%j").apply(dateTime))
        assertEquals("08", Format("%m").apply(dateTime))
        assertEquals("2", Format("%w").apply(dateTime))
        assertEquals("19", Format("%y").apply(dateTime))
        assertEquals("2019", Format("%Y").apply(dateTime))
    }

    @Test
    fun timePatterns() {
        assertEquals("04", Format("%H").apply(dateTime))
        assertEquals("04", Format("%I").apply(dateTime))
        assertEquals("4", Format("%l").apply(dateTime))
        assertEquals("46", Format("%M").apply(dateTime))
        assertEquals("am", Format("%P").apply(dateTime))
        assertEquals("AM", Format("%p").apply(dateTime))
        assertEquals("35", Format("%S").apply(dateTime))
    }

    @Test
    fun leadingZeros() {
        val date = Date(6, Month.JANUARY, 2019)
        val time = Time(4, 3, 2)
        val dateTime = DateTime(date, time)
        assertEquals("04", Format("%H").apply(dateTime))
        assertEquals("04", Format("%I").apply(dateTime))
        assertEquals("4", Format("%l").apply(dateTime))
        assertEquals("03", Format("%M").apply(dateTime))
        assertEquals("02", Format("%S").apply(dateTime))

        assertEquals("06", Format("%d").apply(dateTime))
        assertEquals("6", Format("%e").apply(dateTime))
        assertEquals("006", Format("%j").apply(dateTime))
        assertEquals("01", Format("%m").apply(dateTime))
    }

    @Test
    fun isoFormat() {
        val f = Format("%Y-%m-%dT%H:%M:%S")
        assertEquals("2019-08-06T04:46:35", f.apply(dateTime))
    }

    @Test
    fun randomFormat() {
        val f = Format("----!%%%YY%md%dT%H:%M:%S%%%")
        assertEquals("----!%%2019Y08d06T04:46:35%%%", f.apply(dateTime))
    }
}