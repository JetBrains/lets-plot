/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.datetime


import jetbrains.datalore.base.datetime.DateTimeUtil.BASE_YEAR
import jetbrains.datalore.base.datetime.tz.TimeZone

// TODO: Better to replace java.util.Date with java.time
@Suppress("DEPRECATION")
object JvmDateTimeUtil {
    fun toJavaDate(time: DateTime?): java.util.Date? {
        return if (time == null) null else java.util.Date(
                time.year - BASE_YEAR,
                time.month.ordinal(), time.day, time.hours, time.minutes, time.seconds
        )

    }

    fun toJavaDate(date: Date?): java.util.Date? {
        return if (date == null) null else java.util.Date(date.year - BASE_YEAR, date.month.ordinal(), date.day)

    }

    fun fromJavaDate(date: java.util.Date?, sourceZone: TimeZone?, targetZone: TimeZone?): DateTime? {
        if (date == null) return null

        var dateTime = fromJavaDate(date)
        if (sourceZone == null && targetZone == null) {
            return dateTime
        }
        dateTime = targetZone!!.convertTo(dateTime!!, sourceZone!!)
        return dateTime
    }

    fun fromJavaDate(date: java.util.Date?): DateTime? {
        return if (date == null) null else DateTime(
            Date(
                date.date,
                Month.values()[date.month],
                BASE_YEAR + date.year
            ),
            Time(date.hours, date.minutes, date.seconds)
        )

    }

    fun now(): DateTime? {
        return fromJavaDate(java.util.Date(System.currentTimeMillis()))
    }

    fun toJavaDate(time: DateTime?, sourceZone: TimeZone, targetZone: TimeZone): java.util.Date? {
        return if (time == null) null else toJavaDate(
            sourceZone.convertTo(
                time,
                targetZone
            )
        )

    }
}
