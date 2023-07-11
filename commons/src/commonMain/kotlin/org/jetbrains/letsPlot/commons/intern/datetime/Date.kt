/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime

import kotlin.jvm.JvmOverloads

class Date(val day: Int, val month: Month, val year: Int) : Comparable<Date> {

    val weekDay: WeekDay
        get() {
            val daysFromOrigin = daysFrom(EPOCH)
            return WeekDay.values()[(daysFromOrigin + EPOCH_WEEKDAY.ordinal) % WeekDay.values().size]
        }

    val dateStart: DateTime
        get() = DateTime(this)

    val dateEnd: DateTime
        get() = DateTime(this, Time.DAY_END)

    init {
        validate()
    }

    private fun validate() {
        val daysInMonth = month.getDaysInYear(year)
        val isValid = day in 1..daysInMonth

        if (!isValid) {
            throw IllegalArgumentException()
        }
    }

    fun daysFrom(date: Date): Int {
        if (compareTo(date) < 0) {
            throw IllegalArgumentException()
        }

        var result = 0

        if (year != date.year) {
            val fromYear = date.year
            val toYear = year
            val leapYears = DateTimeUtil.leapYearsBetween(fromYear, toYear)
            val years = toYear - fromYear
            result += leapYears * DateTimeUtil.DAYS_IN_LEAP_YEAR + (years - leapYears) * DateTimeUtil.DAYS_IN_YEAR
        }

        return result + daysFromYearStart() - date.daysFromYearStart()
    }

    fun daysFromYearStart(): Int {
        var result = day
        var current = month.prev()
        while (current != null) {
            result += current.getDaysInYear(year)
            current = current.prev()
        }
        return result
    }

    fun addDays(days: Int): Date {
        @Suppress("NAME_SHADOWING")
        var days = days
        if (days < 0) {
            throw IllegalArgumentException()
        }
        if (days == 0) return this

        var day = this.day
        var month = this.month
        var year = this.year
        var lessThanYear = false

        if (days >= CACHE_DAYS && year == EPOCH.year) {
            year = CACHE_STAMP.year
            month = CACHE_STAMP.month
            day = CACHE_STAMP.day
            days -= CACHE_DAYS
        }

        while (days > 0) {
            val daysToNextMonth = month.getDaysInYear(year) - day + 1
            if (days < daysToNextMonth) {
                return Date(day + days, month, year)
            } else {
                if (lessThanYear) {
                    month = month.next()!!
                    day = 1
                    days -= daysToNextMonth
                } else {
                    val daysToNextYear = lastDayOf(year).daysFrom(Date(day, month, year)) + 1
                    if (days >= daysToNextYear) {
                        day = 1
                        month = Month.JANUARY
                        year += 1
                        days -= daysToNextYear
                    } else {
                        month = month.next()!!
                        day = 1
                        days -= daysToNextMonth
                        lessThanYear = true
                    }
                }
            }
        }

        return Date(day, month, year)
    }

    fun nextDate(): Date {
        return addDays(1)
    }

    fun prevDate(): Date {
        return subtractDays(1)
    }

    fun subtractDays(days: Int): Date {
        if (days < 0) {
            throw IllegalArgumentException()
        }
        if (days == 0) return this

        if (days < day) {
            return Date(day - days, month, year)
        } else {
            val daysToPrevYear = daysFrom(firstDayOf(year))
            return if (days > daysToPrevYear) {
                lastDayOf(year - 1).subtractDays(days - daysToPrevYear - 1)
            } else {
                lastDayOf(year, month.prev()!!).subtractDays(days - day)
            }
        }
    }

    override fun compareTo(other: Date): Int {
        if (year != other.year) return year - other.year
        return if (month.ordinal() != other.month.ordinal()) month.ordinal() - other.month.ordinal() else day - other.day

    }

    override fun equals(other: Any?): Boolean {
        if (other !is Date) return false

        val date = other as Date?
        return date!!.year == year &&
                date.month === month &&
                date.day == day
    }

    override fun hashCode(): Int {
        return year * 239 + month.hashCode() * 31 + day
    }

    override fun toString(): String {
        val result = StringBuilder()
        result.append(year)
        appendMonth(result)
        appendDay(result)
        return result.toString()
    }

    private fun appendDay(result: StringBuilder) {
        if (day < 10) {
            result.append("0")
        }
        result.append(day)
    }

    private fun appendMonth(result: StringBuilder) {
        val month = this.month.ordinal() + 1
        if (month < 10) {
            result.append("0")
        }
        result.append(month)
    }

    fun toPrettyString(): String {
        val result = StringBuilder()
        appendDay(result)
        result.append(".")
        appendMonth(result)
        result.append(".")
        result.append(year)
        return result.toString()
    }

    companion object {
        val EPOCH = Date(1, Month.JANUARY, 1970)
        private val EPOCH_WEEKDAY = WeekDay.THURSDAY

        private val CACHE_STAMP = Date(1, Month.JANUARY, 2012)
        private val CACHE_DAYS = CACHE_STAMP.daysFrom(EPOCH)

        fun parse(str: String): Date {
            if (str.length != 8) {
                throw RuntimeException()
            }

            val year = str.substring(0, 4).toInt()
            val month = str.substring(4, 6).toInt()
            val day = str.substring(6, 8).toInt()
            return Date(day, Month.values()[month - 1], year)
        }

        @JvmOverloads
        fun firstDayOf(year: Int, month: Month = Month.JANUARY): Date {
            return Date(1, month, year)
        }

        @JvmOverloads
        fun lastDayOf(year: Int, month: Month = Month.DECEMBER): Date {
            return Date(month.days, month, year)
        }
    }
}
