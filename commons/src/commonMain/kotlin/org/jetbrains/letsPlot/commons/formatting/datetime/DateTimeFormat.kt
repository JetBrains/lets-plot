/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.datetime

import org.jetbrains.letsPlot.commons.formatting.datetime.Pattern.Companion.Kind
import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Time

class DateTimeFormat(private val spec: List<SpecPart>) {

    constructor(spec: String): this(parse(spec))

    open class SpecPart(val str: String) {
        open fun exec(dateTime: DateTime) = str
    }

    class PatternSpecPart(str: String): SpecPart(str) {
        val pattern: Pattern = Pattern.patternByString(str) ?: throw IllegalArgumentException("Wrong date-time pattern: '$str'")

        override fun exec(dateTime: DateTime): String {
            return getValueForPattern(pattern, dateTime)
        }
    }

    fun apply(dateTime: DateTime): String = spec.joinToString("") { it.exec(dateTime) }

    fun apply(date: Date): String =
        spec
            .filter {
                when {
                    (it is PatternSpecPart && it.pattern.kind == Kind.DATE) -> true
                    it !is PatternSpecPart -> true
                    else -> false
                }
            }
            .joinToString("") { it.exec(DateTime(date)) }

    fun apply(time: Time): String =
        spec
            .filter {
                when {
                    (it is PatternSpecPart && it.pattern.kind == Kind.TIME) -> true
                    it !is PatternSpecPart -> true
                    else -> false
                }
            }
            .joinToString("") { it.exec(DateTime(Date.EPOCH, time)) }

    companion object {
        fun parse(str: String): List<SpecPart> {
            val result = mutableListOf<SpecPart>()
            val resultSequence = Pattern.PATTERN_REGEX.findAll(str)
            var lastIndex = 0
            resultSequence.forEach {
                val value = it.value
                val range = it.range
                val startIndex = range.first
                val endIndex = range.last
                if (startIndex > 0) {
                    val spec = SpecPart(str.substring(lastIndex until startIndex))
                    result.add(spec)
                }
                result.add(PatternSpecPart(value))
                lastIndex = endIndex + 1
            }

            if (lastIndex < str.length) {
                result.add(SpecPart(str.substring(lastIndex)))
            }

            return result
        }

        private fun getValueForPattern(type: Pattern, dateTime: DateTime): String =
            when(type) {
                Pattern.SECOND -> leadZero(dateTime.seconds)
                Pattern.MINUTE -> leadZero(dateTime.minutes)
                Pattern.HOUR_12 -> getHours12(dateTime).toString()
                Pattern.HOUR_12_LEADING_ZERO -> leadZero(getHours12(dateTime))
                Pattern.HOUR_24 -> leadZero(getHours24(dateTime))
                Pattern.MERIDIAN_LOWER -> getMeridian(dateTime)
                Pattern.MERIDIAN_UPPER -> getMeridian(dateTime).uppercase()
                Pattern.DAY_OF_WEEK -> getWeekDayNumber(dateTime)
                Pattern.DAY_OF_WEEK_ABBR -> DateLocale.weekDayAbbr[dateTime.weekDay] ?: ""
                Pattern.DAY_OF_WEEK_FULL -> DateLocale.weekDayFull[dateTime.weekDay] ?: ""
                Pattern.DAY_OF_MONTH -> dateTime.day.toString()
                Pattern.DAY_OF_MONTH_LEADING_ZERO -> leadZero(dateTime.day)
                Pattern.DAY_OF_THE_YEAR -> leadZero(dateTime.date.daysFromYearStart(), 3)
                Pattern.MONTH -> leadZero(dateTime.month.ordinal() + 1)
                Pattern.MONTH_ABBR -> DateLocale.monthAbbr[dateTime.month] ?: ""
                Pattern.MONTH_FULL -> DateLocale.monthFull[dateTime.month] ?: ""
                Pattern.YEAR_SHORT -> dateTime.year.toString().substring(2)
                Pattern.YEAR_FULL -> dateTime.year.toString()
            }

        private fun leadZero(value: Int, length: Int = 2): String = value.toString().padStart(length, '0')

        private fun getHours12(dateTime: DateTime): Int {
            val hours = dateTime.hours
            return when {
                hours == 0 -> 12
                hours <= 12 -> hours
                else -> hours - 12
            }
        }

        private fun getHours24(dateTime: DateTime): Int = dateTime.hours

        private fun getMeridian(dateTime: DateTime): String {
            val hours = dateTime.hours
            return when {
                hours == 24 -> "am"
                hours < 12 -> "am"
                else -> "pm"
            }
        }

        private fun getWeekDayNumber(dateTime: DateTime): String {
            var num = dateTime.weekDay.ordinal + 1
            if (num == 7) {
                num = 0
            }
            return num.toString()
        }
    }
}