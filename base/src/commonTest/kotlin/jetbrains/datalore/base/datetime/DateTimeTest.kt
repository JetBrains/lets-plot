package jetbrains.datalore.base.datetime

import kotlin.test.Test
import kotlin.test.assertEquals

class DateTimeTest {

    @Test
    fun parsing() {
        assertParsed(DateTime(Date(23, Month.SEPTEMBER, 1978), Time(23, 2)))
    }

    private fun assertParsed(dateTime: DateTime) {
        assertEquals(dateTime, DateTime.parse(dateTime.toString()))
    }
}
