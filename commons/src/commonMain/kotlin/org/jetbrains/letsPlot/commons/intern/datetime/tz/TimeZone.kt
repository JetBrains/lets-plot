/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime.tz

import org.jetbrains.letsPlot.commons.intern.datetime.*

abstract class TimeZone protected constructor(val id: String?) {

    init {
//        if (id != null) {
//            ourTimeZones.put(id, this)
//        }
    }

    abstract fun toDateTime(instant: Instant): DateTime
    abstract fun toInstant(dateTime: DateTime): Instant

    fun convertTo(toConvert: DateTime, to: TimeZone): DateTime {
        return if (to === this) toConvert else to.toDateTime(toInstant(toConvert))
    }

    fun convertTimeAtDay(srcTime: Time, dstDate: Date, dstTimeZone: TimeZone): Time {
        var src = DateTime(dstDate, srcTime)
        var dst = convertTo(src, dstTimeZone)
        val dayDiff = dstDate.compareTo(dst.date)
        if (dayDiff != 0) {
            val correctedDay = if (dayDiff > 0) dstDate.nextDate() else dstDate.prevDate()
            src = DateTime(correctedDay, srcTime)
            dst = convertTo(src, dstTimeZone)
        }
        return dst.time
    }

    fun getTimeZoneShift(instant: Instant): Duration {
        val utcDateTime = toDateTime(instant)
        return instant.to(UTC.toInstant(utcDateTime))
    }

    override fun toString(): String {
        return id!!
    }

    companion object {
//        private val ourTimeZones = TreeMap<String, TimeZone>()

        val UTC = TimeZones.utc()
        val BERLIN = TimeZones.withEuSummerTime("Europe/Berlin", Duration.HOUR.mul(1))
        val MOSCOW: TimeZone = TimeZoneMoscow()
        val NY = TimeZones.withUsSummerTime("America/New_York", Duration.HOUR.mul(-5))
    }
}
