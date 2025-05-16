/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime

import kotlinx.datetime.toInstant
import kotlinx.datetime.LocalDateTime as KotlinxLocalDateTime

/**
 * Represents a date and time without timezone information.
 * I.e. this is a representation of a calendar date and wall-clock time.
 * A.k.a. local date and time.
 */
class DateTime : Comparable<DateTime> {
    constructor(date: Date, time: Time = Time(0, 0, 0)) {
        this.kotlinxLocalDateTime = KotlinxLocalDateTime(date.kotlinxLocalDate, time.kotlinxLocalTime)
    }

    internal constructor(kotlinxLocalDateTime: KotlinxLocalDateTime) {
        this.kotlinxLocalDateTime = kotlinxLocalDateTime
    }

    internal val kotlinxLocalDateTime: KotlinxLocalDateTime

    val year: Int get() = kotlinxLocalDateTime.year
    val month: Month get() = Month.of(kotlinxLocalDateTime.monthNumber)
    val day: Int get() = kotlinxLocalDateTime.dayOfMonth
    val hours: Int get() = kotlinxLocalDateTime.hour
    val minutes: Int get() = kotlinxLocalDateTime.minute
    val seconds: Int get() = kotlinxLocalDateTime.second
    val milliseconds: Int get() = kotlinxLocalDateTime.nanosecond / 1_000_000

    val date: Date get() = Date(kotlinxLocalDateTime.date)
    val time: Time get() = Time(kotlinxLocalDateTime.time)

    val weekDay: WeekDay get() = WeekDay.entries[kotlinxLocalDateTime.dayOfWeek.ordinal]

    fun toInstant(tz: TimeZone): Instant {
        return Instant(kotlinxLocalDateTime.toInstant(tz.kotlinxTz))
    }

    fun add(duration: Duration, tz: TimeZone): DateTime {
        val instant = toInstant(tz)
        val instant2 = instant.add(duration)
        return instant2.toDateTime(tz)
    }

    override fun compareTo(other: DateTime) = kotlinxLocalDateTime.compareTo(other.kotlinxLocalDateTime)

    override fun hashCode() = kotlinxLocalDateTime.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DateTime) return false
        return kotlinxLocalDateTime == other.kotlinxLocalDateTime
    }

    override fun toString() = kotlinxLocalDateTime.toString()

//    fun toPrettyString(): String {
//        return time.toPrettyString() + " " + date.toPrettyString()
//    }

    companion object {
        val EPOCH = DateTime(Date.EPOCH, Time(0, 0, 0))

        fun parse(s: String): DateTime = DateTime(KotlinxLocalDateTime.parse(s))
    }
}