/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime

import org.jetbrains.letsPlot.commons.intern.datetime.tz.TimeZone
import kotlin.jvm.JvmOverloads

class DateTime @JvmOverloads constructor(val date: Date, val time: Time = Time.DAY_START) : Comparable<DateTime> {

    val year: Int
        get() = date.year

    val month: Month
        get() = date.month

    val day: Int
        get() = date.day

    val weekDay: WeekDay
        get() = date.weekDay

    val hours: Int
        get() = time.hours

    val minutes: Int
        get() = time.minutes

    val seconds: Int
        get() = time.seconds

    val milliseconds: Int
        get() = time.milliseconds

    fun changeDate(date: Date): DateTime {
        return DateTime(date, time)
    }

    fun changeTime(time: Time): DateTime {
        return DateTime(date, time)
    }

    fun add(duration: Duration): DateTime {
        val utcInstant = TimeZone.UTC.toInstant(this)
        return TimeZone.UTC.toDateTime(utcInstant.add(duration))
    }

    fun to(otherTime: DateTime): Duration {
        val currentInstant = TimeZone.UTC.toInstant(this)
        val otherInstant = TimeZone.UTC.toInstant(otherTime)
        return currentInstant.to(otherInstant)
    }

    fun isBefore(dateTime: DateTime): Boolean {
        return compareTo(dateTime) < 0
    }

    fun isAfter(dateTime: DateTime): Boolean {
        return compareTo(dateTime) > 0
    }

    override fun hashCode(): Int {
        return date.hashCode() * 31 + time.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is DateTime) return false

        val otherDateTime = other as DateTime?

        return date == otherDateTime!!.date && time == otherDateTime.time
    }

    override fun compareTo(other: DateTime): Int {
        val dateComparison = date.compareTo(other.date)
        return if (dateComparison != 0) dateComparison else time.compareTo(other.time)
    }

    override fun toString(): String {
        return date.toString() + "T" + time
    }

    fun toPrettyString(): String {
        return time.toPrettyHMString() + " " + date.toPrettyString()
    }

    companion object {

        fun parse(s: String): DateTime {
            if (s.length < 15) {
                throw IllegalArgumentException()
            }

            return DateTime(Date.parse(s.substring(0, 8)), Time.parse(s.substring(9)))
        }
    }
}
