/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlinx.datetime.LocalDate as KotlinxLocalDate

/**
 * Represents a calendar date (year, month, day).
 * Since it represents only a calendar date, no timezone information is needed or used.
 *
 */
class Date : Comparable<Date> {
    /**
     * The [day] is 1-based
     */
    constructor(day: Int, month: Month, year: Int) {
        this.kotlinxLocalDate = KotlinxLocalDate(year, month.number, day)
    }

    internal constructor(kotlinxLocalDate: KotlinxLocalDate) {
        this.kotlinxLocalDate = kotlinxLocalDate
    }

    internal val kotlinxLocalDate: KotlinxLocalDate

    val day: Int get() = kotlinxLocalDate.dayOfMonth
    val month: Month get() = Month.of(kotlinxLocalDate.monthNumber)
    val year: Int get() = kotlinxLocalDate.year
    val weekDay: WeekDay get() = WeekDay.entries[kotlinxLocalDate.dayOfWeek.ordinal]

    fun nextDate(): Date = Date(kotlinxLocalDate.plus(1, DateTimeUnit.DAY))
    fun prevDate(): Date = Date(kotlinxLocalDate.plus(-1, DateTimeUnit.DAY))

    fun daysFromYearStart(): Int {
        val yearStart = KotlinxLocalDate(year, 1, 1)
        return kotlinxLocalDate.toEpochDays() - yearStart.toEpochDays()
    }

    fun daysUntil(other: Date): Int {
        return kotlinxLocalDate.daysUntil(other.kotlinxLocalDate)
    }

    override fun compareTo(other: Date) = kotlinxLocalDate.compareTo(other.kotlinxLocalDate)

    override fun hashCode() = kotlinxLocalDate.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Date) return false
        return kotlinxLocalDate == other.kotlinxLocalDate
    }

    override fun toString() = kotlinxLocalDate.toString()

    companion object {
        val EPOCH = Date(1, Month.JANUARY, 1970)

        fun parse(s: String): Date = Date(KotlinxLocalDate.parse(s))
    }
}