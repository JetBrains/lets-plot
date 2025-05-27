/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime

enum class Month(private val displayName: String) {
    JANUARY("JAN"),
    FEBRUARY("FEB"),
    MARCH("MAR"),
    APRIL("APR"),
    MAY("MAY"),
    JUNE("JUN"),
    JULY("JUL"),
    AUGUST("AUG"),
    SEPTEMBER("SEP"),
    OCTOBER("OCT"),
    NOVEMBER("NOV"),
    DECEMBER("DEC");

    val number: Int = ordinal + 1

    operator fun next(): Month? {
        return Month.entries.getOrNull(ordinal + 1)
    }

    override fun toString(): String {
        return displayName
    }

    companion object {
        /**
         * * @param number 1-based month number: [1..12]
         */
        fun of(number: Int): Month = Month.entries[number - 1]
    }
}