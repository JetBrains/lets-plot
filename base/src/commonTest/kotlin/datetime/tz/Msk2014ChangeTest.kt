package jetbrains.datalore.base.datetime.tz

import jetbrains.datalore.base.datetime.Date
import jetbrains.datalore.base.datetime.DateTime
import jetbrains.datalore.base.datetime.Month
import jetbrains.datalore.base.datetime.Time
import kotlin.test.Test
import kotlin.test.assertEquals

class Msk2014ChangeTest {

    @Test
    fun testBeforeChange() {
        val utcTime = DateTime(BEFORE, Time(12, 0))
        val mskDateTime = TimeZone.UTC.convertTo(utcTime, TimeZone.MOSCOW)
        val expected = DateTime(BEFORE, Time(16, 0))
        assertEquals(expected, mskDateTime)
    }

    @Test
    fun testAfterChange() {
        val utcTime = DateTime(AFTER, Time(12, 0))
        val mskDateTime = TimeZone.UTC.convertTo(utcTime, TimeZone.MOSCOW)
        val expected = DateTime(AFTER, Time(15, 0))
        assertEquals(expected, mskDateTime)
    }

    companion object {
        private val BEFORE = Date(24, Month.OCTOBER, 2014)
        private val AFTER = Date(27, Month.OCTOBER, 2014)
    }
}
