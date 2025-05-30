/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime

class Duration(val duration: Long) : Comparable<Duration> {
    val totalWeeks: Long get() = duration / WEEK.duration
    val totalDays: Long get() = duration / DAY.duration
    val totalHours: Long get() = duration / DAY.duration
    val totalMinutes: Long get() = duration / MINUTE.duration

    // Components of the duration.
    // day: 0..6
    // hour: 0..23
    // minute: 0..59
    // second: 0..59
    // millis: 0..999
    val week: Long get() = duration / WEEK.duration
    val day: Long get() = duration % WEEK.duration / DAY.duration
    val hour: Long get() = duration % DAY.duration / HOUR.duration
    val minute: Long get() = duration % HOUR.duration / MINUTE.duration
    val second: Long get() = duration % MINUTE.duration / SECOND.duration
    val millis: Long get() = duration % SECOND.duration / MS.duration

    val isPositive: Boolean
        get() = duration > 0

    fun mul(times: Long): Duration {
        return Duration(duration * times)
    }

    fun add(duration: Duration): Duration {
        return Duration(this.duration + duration.duration)
    }

    fun sub(duration: Duration): Duration {
        return Duration(this.duration - duration.duration)
    }

    operator fun div(duration: Duration): Double {
        return this.duration / duration.duration.toDouble()
    }

    override fun compareTo(other: Duration): Int {
        val delta = duration - other.duration
        return if (delta > 0) {
            1
        } else if (delta == 0L) {
            0
        } else {
            -1
        }
    }

    override fun hashCode(): Int {
        return duration.toInt()
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is Duration) false else duration == other.duration

    }

    override fun toString(): String {
        return "Duration : " + duration + "ms"
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
