/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime

open class Month private constructor(val days: Int, private val myOrdinal: Int, private val myName: String) {

    fun ordinal(): Int {
        return myOrdinal
    }

    open fun getDaysInYear(year: Int): Int {
        return days
    }

    open fun getDaysInLeapYear(): Int {
        return days
    }

    fun prev(): Month? {
        return if (myOrdinal == 0) null else values()[myOrdinal - 1]
    }

    operator fun next(): Month? {
        val values = values()
        return if (myOrdinal == values.size - 1) null else values[myOrdinal + 1]
    }

    override fun toString(): String {
        return myName
    }

    private class VarLengthMonth(days: Int, private val myDaysInLeapYear: Int, ordinal: Int, name: String) : Month(days, ordinal, name) {

        override fun getDaysInLeapYear(): Int {
            return myDaysInLeapYear
        }

        override fun getDaysInYear(year: Int): Int {
            return if (DateTimeUtil.isLeap(year)) {
                getDaysInLeapYear()
            } else {
                days
            }
        }
    }

    companion object {
        val JANUARY = Month(31, 0, "January")

        val FEBRUARY: Month = VarLengthMonth(28, 29, 1, "February")
        val MARCH = Month(31, 2, "March")
        val APRIL = Month(30, 3, "April")
        val MAY = Month(31, 4, "May")
        val JUNE = Month(30, 5, "June")
        val JULY = Month(31, 6, "July")
        val AUGUST = Month(31, 7, "August")
        val SEPTEMBER = Month(30, 8, "September")
        val OCTOBER = Month(31, 9, "October")
        val NOVEMBER = Month(30, 10, "November")
        val DECEMBER = Month(31, 11, "December")

        private val VALUES = arrayOf(JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER)

        fun values(): Array<Month> {
            return VALUES
        }
    }
}
