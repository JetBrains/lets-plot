/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime

class Duration(val totalMillis: Long) : Comparable<Duration> {
    val totalWeeks: Long get() = totalMillis / WEEK.totalMillis
    val totalDays: Long get() = totalMillis / DAY.totalMillis
    val totalHours: Long get() = totalMillis / DAY.totalMillis
    val totalMinutes: Long get() = totalMillis / MINUTE.totalMillis

    // Components of the duration.
    // day: 0..6
    // hour: 0..23
    // minute: 0..59
    // second: 0..59
    // millis: 0..999
    val week: Long get() = totalMillis / WEEK.totalMillis
    val day: Long get() = totalMillis % WEEK.totalMillis / DAY.totalMillis
    val hour: Long get() = totalMillis % DAY.totalMillis / HOUR.totalMillis
    val minute: Long get() = totalMillis % HOUR.totalMillis / MINUTE.totalMillis
    val second: Long get() = totalMillis % MINUTE.totalMillis / SECOND.totalMillis
    val millis: Long get() = totalMillis % SECOND.totalMillis / MS.totalMillis

    val isPositive: Boolean
        get() = totalMillis > 0

    fun mul(times: Number): Duration {
        return Duration(totalMillis * times.toLong())
    }

    fun add(duration: Duration): Duration {
        return Duration(this.totalMillis + duration.totalMillis)
    }

    fun sub(duration: Duration): Duration {
        return Duration(this.totalMillis - duration.totalMillis)
    }

    operator fun div(duration: Duration): Double {
        return this.totalMillis / duration.totalMillis.toDouble()
    }

    override fun compareTo(other: Duration): Int {
        return totalMillis.compareTo(other.totalMillis)
    }

    override fun hashCode(): Int {
        return totalMillis.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Duration) return false
        return totalMillis == other.totalMillis
    }

    override fun toString(): String {
        return "Duration : " + totalMillis + "ms"
    }

    companion object {
        val MS = Duration(1)
        val SECOND = MS.mul(1000)
        val MINUTE = SECOND.mul(60)
        val HOUR = MINUTE.mul(60)
        val DAY = HOUR.mul(24)
        val WEEK = DAY.mul(7)
    }
}
