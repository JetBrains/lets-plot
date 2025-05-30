/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime

import kotlinx.datetime.LocalTime as KotlinxLocalTime

/**
 * Represents a time of day (hours, minutes, seconds, milliseconds)
 * without date or timezone information (a.k.a. local time).
 * Since it represents only time-of-day, no timezone information is needed or used.
 *
 * The supported ranges of components:
 * - [hour] `0..23`
 * - [minute] `0..59`
 * - [second] `0..59`
 * - [milliseconds] `0..999`
 *
 */
class Time : Comparable<Time> {
    constructor(hours: Int, minutes: Int, seconds: Int = 0, milliseconds: Int = 0) {
        this.kotlinxLocalTime = KotlinxLocalTime(
            hours,
            minutes,
            seconds,
            milliseconds * 1_000_000
        )
    }

    internal constructor(kotlinxLocalTime: KotlinxLocalTime) {
        this.kotlinxLocalTime = kotlinxLocalTime
    }

    internal val kotlinxLocalTime: KotlinxLocalTime

    val hours: Int get() = kotlinxLocalTime.hour
    val minutes: Int get() = kotlinxLocalTime.minute
    val seconds: Int get() = kotlinxLocalTime.second
    val milliseconds: Int get() = kotlinxLocalTime.nanosecond / 1_000_000
    val nanoseconds: Int get() = kotlinxLocalTime.nanosecond

    override fun compareTo(other: Time) = kotlinxLocalTime.compareTo(other.kotlinxLocalTime)

    override fun hashCode() = kotlinxLocalTime.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Time) return false
        return kotlinxLocalTime == other.kotlinxLocalTime
    }

    /**
     * Converts this time value to the extended ISO 8601 string representation.
     * I.e. uses > 3 digits after the decimal point for nanoseconds.
     */
    override fun toString() = kotlinxLocalTime.toString()

    companion object {
        val DAY_START = Time(0, 0)
        val DAY_END = ofNanos(23, 59, 59, 999_999_999)

        /**
         * Creates a Time instance with nanosecond precision.
         *
         * @param hours The hour-of-day (0-23)
         * @param minutes The minute-of-hour (0-59)
         * @param seconds The second-of-minute (0-59)
         * @param nanoseconds The nanosecond-of-second (0-999,999,999)
         * @return A new Time instance
         */
        fun ofNanos(hours: Int, minutes: Int, seconds: Int = 0, nanoseconds: Int = 0): Time {
            return Time(KotlinxLocalTime(hours, minutes, seconds, nanoseconds))
        }

        fun parse(s: String): Time {
            return try {
                if (s.indexOf(':') == 1) {
                    // "H:mm" -> "0H:mm"
                    Time(KotlinxLocalTime.parse("0" + s))
                } else {
                    Time(KotlinxLocalTime.parse(s))
                }
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid time format: $s", e)
            }
        }
    }
}